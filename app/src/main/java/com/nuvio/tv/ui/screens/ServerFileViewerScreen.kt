package com.nuvio.tv.ui.screens

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.ui.graphics.Color
import androidx.tv.material3.Border
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil3.compose.AsyncImage
import com.nuvio.tv.ui.theme.NuvioTheme
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.IOException
import kotlin.math.min

private const val MAX_TEXT_FILE_BYTES = 1_048_576

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun ServerFileViewerScreen(
    uri: String,
    fileName: String,
    mimeType: String,
    imageEntries: List<ServerBrowserEntry>,
    currentImagePath: String,
    onPrepareImage: suspend (ServerBrowserEntry) -> String,
    onPrepareThumbnail: suspend (ServerBrowserEntry) -> Uri? = { it.uri?.let(Uri::parse) },
    showCoverflow: Boolean = false,
    onBack: () -> Unit,
    slideshowEnabled: Boolean = false,
    slideshowIntervalSeconds: Int = 5,
) {
    BackHandler(onBack = onBack)
    when {
        mimeType.startsWith("image/") -> ServerImageViewer(
            uri = uri,
            fileName = fileName,
            imageEntries = imageEntries,
            currentImagePath = currentImagePath,
            onPrepareImage = onPrepareImage,
            onPrepareThumbnail = onPrepareThumbnail,
            showCoverflow = showCoverflow,
            onBack = onBack,
            slideshowEnabled = slideshowEnabled,
            slideshowIntervalSeconds = slideshowIntervalSeconds,
        )
        mimeType == "application/pdf" -> ServerPdfViewer(uri, fileName, onBack)
        else -> ServerTextViewer(uri, fileName, onBack)
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun ServerImageViewer(
    uri: String,
    fileName: String,
    imageEntries: List<ServerBrowserEntry>,
    currentImagePath: String,
    onPrepareImage: suspend (ServerBrowserEntry) -> String,
    onPrepareThumbnail: suspend (ServerBrowserEntry) -> Uri?,
    showCoverflow: Boolean,
    onBack: () -> Unit,
    slideshowEnabled: Boolean,
    slideshowIntervalSeconds: Int,
) {
    var failed by remember(uri) { mutableStateOf(false) }
    var displayedUri by remember(uri) { mutableStateOf(uri) }
    var displayedName by remember(fileName) { mutableStateOf(fileName) }
    var activeIndex by remember(imageEntries, currentImagePath) {
        mutableIntStateOf(imageEntries.indexOfFirst { it.path == currentImagePath })
    }
    var navigationDirection by remember(uri) { mutableIntStateOf(1) }
    var loadingNext by remember(uri) { mutableStateOf(false) }
    var navigationError by remember(uri) { mutableStateOf(false) }
    var coverThumbnails by remember(imageEntries) { mutableStateOf<Map<Int, Uri>>(emptyMap()) }
    val scope = rememberCoroutineScope()

    fun movePhoto(direction: Int, wrapAtEnds: Boolean = false) {
        if (loadingNext || activeIndex !in imageEntries.indices) return
        val rawIndex = activeIndex + direction
        val nextIndex = if (wrapAtEnds) {
            Math.floorMod(rawIndex, imageEntries.size)
        } else {
            rawIndex.coerceIn(0, imageEntries.lastIndex)
        }
        if (nextIndex == activeIndex) return
        val nextEntry = imageEntries[nextIndex]
        navigationDirection = direction
        scope.launch {
            loadingNext = true
            navigationError = false
            try {
                displayedUri = onPrepareImage(nextEntry)
                displayedName = nextEntry.name
                activeIndex = nextIndex
                failed = false
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                navigationError = true
            } finally {
                loadingNext = false
            }
        }
    }

    LaunchedEffect(slideshowEnabled, slideshowIntervalSeconds, activeIndex, loadingNext, imageEntries) {
        if (!slideshowEnabled || imageEntries.size < 2 || loadingNext || activeIndex !in imageEntries.indices) {
            return@LaunchedEffect
        }
        delay(slideshowIntervalSeconds.coerceIn(1, 10) * 1_000L)
        movePhoto(direction = 1, wrapAtEnds = true)
    }

    LaunchedEffect(activeIndex, imageEntries, showCoverflow) {
        if (!showCoverflow || activeIndex !in imageEntries.indices || imageEntries.size < 2) {
            coverThumbnails = emptyMap()
            return@LaunchedEffect
        }

        val neighborIndices = listOf(activeIndex - 1, activeIndex + 1)
            .filter { it in imageEntries.indices }
        val prepared = mutableMapOf<Int, Uri>()
        for (index in neighborIndices) {
            try {
                val entry = imageEntries[index]
                val thumbnail = entry.uri?.let(Uri::parse) ?: onPrepareThumbnail(entry)
                if (thumbnail != null) prepared[index] = thumbnail
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                // A failed side preview should not block opening the selected photo.
            }
        }
        coverThumbnails = prepared
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black)
            .onPreviewKeyEvent { event ->
                if (event.type != KeyEventType.KeyDown || imageEntries.isEmpty()) {
                    false
                } else {
                    when (event.key) {
                        Key.DirectionLeft -> { movePhoto(-1); true }
                        Key.DirectionRight -> { movePhoto(1); true }
                        else -> false
                    }
                }
            }
    ) {
        BoxWithConstraints(Modifier.fillMaxSize()) {
            val coverMaxWidth = (maxWidth * 0.20f).coerceIn(180.dp, 320.dp)
            val coverMaxHeight = (maxHeight * 0.58f).coerceIn(240.dp, 440.dp)
            val previousIndex = activeIndex - 1
            val nextIndex = activeIndex + 1

            if (showCoverflow && previousIndex in imageEntries.indices) {
                coverThumbnails[previousIndex]?.let { thumbnail ->
                    FreeboxCoverflowPhotoCard(
                        entry = imageEntries[previousIndex],
                        thumbnailUri = thumbnail,
                        rotationY = 18f,
                        maxWidth = coverMaxWidth,
                        maxHeight = coverMaxHeight,
                        onClick = { movePhoto(-1) },
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 18.dp)
                            .zIndex(1f),
                    )
                }
            }

            if (showCoverflow && nextIndex in imageEntries.indices) {
                coverThumbnails[nextIndex]?.let { thumbnail ->
                    FreeboxCoverflowPhotoCard(
                        entry = imageEntries[nextIndex],
                        thumbnailUri = thumbnail,
                        rotationY = -18f,
                        maxWidth = coverMaxWidth,
                        maxHeight = coverMaxHeight,
                        onClick = { movePhoto(1) },
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 18.dp)
                            .zIndex(1f),
                    )
                }
            }

            AnimatedContent(
                targetState = displayedUri to displayedName,
                transitionSpec = {
                    val enterFrom = if (navigationDirection >= 0) 1 else -1
                    (slideInHorizontally { it * enterFrom / 5 } + fadeIn()) togetherWith
                        (slideOutHorizontally { -it * enterFrom / 5 } + fadeOut())
                },
                label = "freebox-photo-coverflow",
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth(if (showCoverflow) 0.56f else 1f)
                    .fillMaxHeight(if (showCoverflow) 0.88f else 1f)
                    .padding(if (showCoverflow) 12.dp else 24.dp)
                    .zIndex(2f),
            ) { (imageUri, imageName) ->
                AsyncImage(
                    model = Uri.parse(imageUri),
                    contentDescription = imageName,
                    contentScale = ContentScale.Fit,
                    onError = { if (imageUri == displayedUri) failed = true },
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
        FileViewerHeader(displayedName, onBack, Modifier.align(Alignment.TopStart).padding(24.dp))
        if (failed) {
            Text(
                text = "Cette image ne peut pas être affichée.",
                style = MaterialTheme.typography.titleMedium,
                color = NuvioTheme.colors.TextPrimary,
                modifier = Modifier.align(Alignment.Center),
            )
        }
        if (loadingNext) {
            Text(
                text = "Ouverture de la photo…",
                style = MaterialTheme.typography.bodyMedium,
                color = NuvioTheme.colors.TextPrimary,
                modifier = Modifier.align(Alignment.BottomCenter).padding(24.dp),
            )
        } else if (navigationError) {
            Text(
                text = "Impossible d’ouvrir cette photo.",
                style = MaterialTheme.typography.bodyMedium,
                color = NuvioTheme.colors.TextPrimary,
                modifier = Modifier.align(Alignment.BottomCenter).padding(24.dp),
            )
        }
        if (!loadingNext && !navigationError && activeIndex in imageEntries.indices) {
            Text(
                text = "${activeIndex + 1} / ${imageEntries.size}",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp)
                    .background(Color.Black.copy(alpha = 0.72f), RoundedCornerShape(50))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            )
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun FreeboxCoverflowPhotoCard(
    entry: ServerBrowserEntry,
    thumbnailUri: Uri,
    rotationY: Float,
    maxWidth: Dp,
    maxHeight: Dp,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var imageAspectRatio by remember(thumbnailUri) { mutableStateOf(1f) }
    val aspectRatio = imageAspectRatio.takeIf { it.isFinite() && it > 0f } ?: 1f
    val cardWidth = minOf(maxWidth, maxHeight * aspectRatio)
    val cardHeight = cardWidth / aspectRatio

    Card(
        onClick = onClick,
        modifier = modifier
            .size(width = cardWidth, height = cardHeight)
            .graphicsLayer {
                cameraDistance = 12f * density
                this.rotationY = rotationY
                shadowElevation = 22.dp.toPx()
            },
        colors = CardDefaults.colors(
            containerColor = Color.Black.copy(alpha = 0.92f),
            focusedContainerColor = NuvioTheme.colors.FocusBackground,
        ),
        shape = CardDefaults.shape(shape = RoundedCornerShape(14.dp)),
        scale = CardDefaults.scale(focusedScale = 1.02f, pressedScale = 0.98f),
    ) {
        AsyncImage(
            model = thumbnailUri,
            contentDescription = entry.name,
            contentScale = ContentScale.Fit,
            onSuccess = { result ->
                val intrinsicSize = result.painter.intrinsicSize
                if (intrinsicSize.width.isFinite() && intrinsicSize.height.isFinite() &&
                    intrinsicSize.width > 0f && intrinsicSize.height > 0f
                ) {
                    imageAspectRatio = intrinsicSize.width / intrinsicSize.height
                }
            },
            modifier = Modifier.fillMaxSize().padding(8.dp),
        )
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun ServerPdfViewer(uri: String, fileName: String, onBack: () -> Unit) {
    val context = LocalContext.current
    var pageIndex by remember(uri) { mutableIntStateOf(0) }
    var pageCount by remember(uri) { mutableIntStateOf(0) }
    var bitmap by remember(uri) { mutableStateOf<Bitmap?>(null) }
    var error by remember(uri) { mutableStateOf<String?>(null) }
    var loading by remember(uri) { mutableStateOf(true) }

    LaunchedEffect(uri, pageIndex) {
        loading = true
        error = null
        try {
            val page = withContext(Dispatchers.IO) {
                val descriptor = context.contentResolver.openFileDescriptor(Uri.parse(uri), "r")
                    ?: throw IOException("PDF indisponible.")
                descriptor.use { fileDescriptor ->
                    PdfRenderer(fileDescriptor).use { renderer ->
                        if (renderer.pageCount <= 0) throw IOException("Ce PDF ne contient aucune page.")
                        val safeIndex = pageIndex.coerceIn(0, renderer.pageCount - 1)
                        val pdfPage = renderer.openPage(safeIndex)
                        try {
                            val metrics = context.resources.displayMetrics
                            val maxWidth = (metrics.widthPixels - 64f * metrics.density).toInt().coerceAtLeast(1)
                            val maxHeight = (metrics.heightPixels - 190f * metrics.density).toInt().coerceAtLeast(1)
                            val scale = min(maxWidth / pdfPage.width.toFloat(), maxHeight / pdfPage.height.toFloat())
                            val width = (pdfPage.width * scale).toInt().coerceAtLeast(1)
                            val height = (pdfPage.height * scale).toInt().coerceAtLeast(1)
                            val rendered = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                            rendered.eraseColor(android.graphics.Color.WHITE)
                            pdfPage.render(rendered, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                            PdfPageResult(rendered, renderer.pageCount)
                        } finally {
                            pdfPage.close()
                        }
                    }
                }
            }
            bitmap = page.bitmap
            pageCount = page.count
            pageIndex = pageIndex.coerceIn(0, (page.count - 1).coerceAtLeast(0))
            loading = false
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (failure: Exception) {
            error = failure.message ?: "PDF impossible à lire."
            loading = false
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(NuvioTheme.colors.Background).padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        FileViewerHeader(fileName, onBack)
        Box(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            when {
                bitmap != null -> Image(
                    bitmap = bitmap!!.asImageBitmap(),
                    contentDescription = "$fileName, page ${pageIndex + 1}",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize(),
                )
                loading -> Text("Ouverture du PDF…", color = NuvioTheme.colors.TextSecondary)
                else -> Text(error ?: "PDF impossible à lire.", color = NuvioTheme.colors.TextPrimary)
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            FileViewerControl(label = "Page précédente", enabled = pageIndex > 0, onClick = { pageIndex-- })
            Text(
                text = if (pageCount > 0) "Page ${pageIndex + 1} sur $pageCount" else "",
                style = MaterialTheme.typography.bodyMedium,
                color = NuvioTheme.colors.TextSecondary,
                modifier = Modifier.padding(horizontal = 18.dp),
            )
            FileViewerControl(label = "Page suivante", enabled = pageIndex + 1 < pageCount, onClick = { pageIndex++ })
        }
    }
}

@Composable
private fun ServerTextViewer(uri: String, fileName: String, onBack: () -> Unit) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    var content by remember(uri) { mutableStateOf("Chargement du fichier…") }
    var loadError by remember(uri) { mutableStateOf(false) }

    LaunchedEffect(uri) {
        try {
            val loaded = withContext(Dispatchers.IO) {
                val input = context.contentResolver.openInputStream(Uri.parse(uri))
                    ?: throw IOException("Fichier indisponible.")
                input.use { stream ->
                    val bytes = ByteArrayOutputStream()
                    val buffer = ByteArray(8 * 1024)
                    var total = 0
                    while (total < MAX_TEXT_FILE_BYTES) {
                        val count = stream.read(buffer, 0, min(buffer.size, MAX_TEXT_FILE_BYTES - total))
                        if (count < 0) break
                        bytes.write(buffer, 0, count)
                        total += count
                    }
                    val truncated = stream.read() >= 0
                    String(bytes.toByteArray(), Charsets.UTF_8) to truncated
                }
            }
            content = if (loaded.second) {
                "${loaded.first}\n\n[Affichage limité aux premiers 1 Mo du fichier]"
            } else loaded.first
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (_: Exception) {
            loadError = true
            content = "Impossible d’ouvrir ce fichier texte."
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(NuvioTheme.colors.Background).padding(28.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        FileViewerHeader(fileName, onBack)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(
                    NuvioTheme.colors.BackgroundCard,
                    NuvioTheme.configuredShape(RoundedCornerShape(14.dp))
                )
                .verticalScroll(scrollState)
                .onPreviewKeyEvent { event ->
                    if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
                    when (event.key) {
                        Key.DirectionUp -> { scope.launch { scrollState.scrollBy(-320f) }; true }
                        Key.DirectionDown -> { scope.launch { scrollState.scrollBy(320f) }; true }
                        else -> false
                    }
                }
                .focusable()
                .padding(20.dp),
        ) {
            SelectionContainer {
                Text(
                    text = content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (loadError) NuvioTheme.colors.TextSecondary else NuvioTheme.colors.TextPrimary,
                )
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun FileViewerHeader(fileName: String, onBack: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FileViewerControl("Retour", onClick = onBack, backIcon = true)
        Text(
            text = fileName,
            style = MaterialTheme.typography.titleMedium,
            color = NuvioTheme.colors.TextPrimary,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun FileViewerControl(
    label: String,
    enabled: Boolean = true,
    onClick: () -> Unit,
    backIcon: Boolean = false,
) {
    val shape = NuvioTheme.configuredShape(RoundedCornerShape(12.dp))
    val disabledColor = NuvioTheme.colors.BackgroundCard.copy(alpha = .5f)
    val containerColor = if (enabled) NuvioTheme.colors.BackgroundCard else disabledColor
    val focusedContainerColor = if (enabled) NuvioTheme.colors.FocusBackground else disabledColor
    Card(
        onClick = { if (enabled) onClick() },
        modifier = Modifier.focusProperties { canFocus = enabled },
        colors = CardDefaults.colors(
            containerColor = containerColor,
            focusedContainerColor = focusedContainerColor,
        ),
        border = CardDefaults.border(
            focusedBorder = Border(
                border = NuvioTheme.focusRing.border(NuvioTheme.spacing.xxs),
                shape = shape,
            ),
        ),
        shape = CardDefaults.shape(shape = shape),
        scale = CardDefaults.scale(focusedScale = 1f, pressedScale = 1f),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (backIcon) {
                androidx.tv.material3.Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = label,
                    tint = NuvioTheme.colors.TextPrimary,
                    modifier = Modifier.size(20.dp),
                )
            }
            Text(label, color = NuvioTheme.colors.TextPrimary, style = MaterialTheme.typography.labelLarge)
        }
    }
}

private data class PdfPageResult(val bitmap: Bitmap, val count: Int)
