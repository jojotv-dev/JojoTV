package com.nuvio.tv.ui.screens.freebox

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

@Composable
fun FreeboxPhotoViewerScreen(
    photoUrl: String,
    title: String,
    headers: Map<String, String>,
    onBackPress: () -> Unit
) {
    BackHandler(onBack = onBackPress)

    val httpClient = remember { OkHttpClient() }
    var loadState by remember(photoUrl, headers) { mutableStateOf<PhotoLoadState>(PhotoLoadState.Loading) }

    LaunchedEffect(photoUrl, headers) {
        loadState = PhotoLoadState.Loading
        loadState = loadFreeboxBitmap(httpClient, photoUrl, headers)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(36.dp),
        contentAlignment = Alignment.Center
    ) {
        when (val state = loadState) {
            PhotoLoadState.Loading -> CircularProgressIndicator()
            is PhotoLoadState.Error -> PhotoErrorContent(
                title = title,
                message = state.message,
                onBackPress = onBackPress
            )
            is PhotoLoadState.Ready -> {
                Image(
                    bitmap = state.bitmap.asImageBitmap(),
                    contentDescription = title,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
                Text(
                    text = title,
                    color = Color.White.copy(alpha = 0.86f),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(10.dp)
                )
            }
        }
    }
}

@Composable
private fun PhotoErrorContent(
    title: String,
    message: String,
    onBackPress: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Text(
            text = title,
            color = Color.White,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
        Text(
            text = message,
            color = Color.White.copy(alpha = 0.72f),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Card(
            onClick = onBackPress,
            colors = CardDefaults.colors(
                containerColor = Color(0xFFFF7A00),
                focusedContainerColor = Color(0xFFFF9B32)
            )
        ) {
            Text(
                text = "Retour",
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(horizontal = 28.dp, vertical = 14.dp)
            )
        }
    }
}

internal suspend fun loadFreeboxBitmap(
    httpClient: OkHttpClient,
    photoUrl: String,
    headers: Map<String, String>
): PhotoLoadState = withContext(Dispatchers.IO) {
    runCatching {
        val requestBuilder = Request.Builder().url(photoUrl).get()
        headers.forEach { (name, value) ->
            if (name.isNotBlank() && value.isNotBlank()) {
                requestBuilder.header(name, value)
            }
        }
        httpClient.newCall(requestBuilder.build()).execute().use { response ->
            if (!response.isSuccessful) {
                error("Freebox HTTP ${response.code}")
            }
            val bytes = response.body?.bytes() ?: error("Corps de réponse vide.")
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                ?: error("Format d'image non pris en charge.")
        }
    }.fold(
        onSuccess = { PhotoLoadState.Ready(it) },
        onFailure = { PhotoLoadState.Error(it.message ?: "Impossible d'ouvrir la photo.") }
    )
}

internal sealed interface PhotoLoadState {
    data object Loading : PhotoLoadState
    data class Ready(val bitmap: Bitmap) : PhotoLoadState
    data class Error(val message: String) : PhotoLoadState
}
