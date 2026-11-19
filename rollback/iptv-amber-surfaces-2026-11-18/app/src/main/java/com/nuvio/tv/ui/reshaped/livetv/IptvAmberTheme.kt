package com.nuvio.tv.ui.reshaped.livetv

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.darkColorScheme
import com.nuvio.tv.domain.model.AppTheme
import com.nuvio.tv.ui.theme.LocalAppTheme
import com.nuvio.tv.ui.theme.LocalNuvioColors
import com.nuvio.tv.ui.theme.LocalNuvioExtendedColors
import com.nuvio.tv.ui.theme.LocalNuvioFocusRingStyle
import com.nuvio.tv.ui.theme.LocalThemePalette
import com.nuvio.tv.ui.theme.NuvioColorScheme
import com.nuvio.tv.ui.theme.NuvioExtendedColors
import com.nuvio.tv.ui.theme.NuvioTheme
import com.nuvio.tv.ui.theme.ThemeColors
import com.nuvio.tv.ui.theme.createFocusRingStyle

/** Keeps the IPTV Films, Séries, and TV screens on the same Amber accent. */
@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
internal fun IptvAmberTheme(content: @Composable () -> Unit) {
    val parentColors = NuvioTheme.colors
    val amoledMode = parentColors.Background == Color.Black
    val amoledSurfacesMode = parentColors.Surface == Color.Black
    val palette = ThemeColors.Amber
    val colors = remember(amoledMode, amoledSurfacesMode) {
        NuvioColorScheme(palette, amoledMode, amoledSurfacesMode)
    }
    val extendedColors = remember(colors) {
        NuvioExtendedColors(
            backgroundElevated = colors.BackgroundElevated,
            backgroundCard = colors.BackgroundCard,
            textSecondary = colors.TextSecondary,
            textTertiary = colors.TextTertiary,
            focusRing = colors.FocusRing,
            focusBackground = colors.FocusBackground,
            rating = colors.Rating,
        )
    }
    val focusRingStyle = remember(palette) { createFocusRingStyle(palette) }
    val materialColors = remember(colors) {
        darkColorScheme(
            primary = colors.Primary,
            onPrimary = colors.OnPrimary,
            secondary = colors.Secondary,
            onSecondary = colors.OnSecondary,
            background = colors.Background,
            surface = colors.Surface,
            surfaceVariant = colors.SurfaceVariant,
            onBackground = colors.TextPrimary,
            onSurface = colors.TextPrimary,
            onSurfaceVariant = colors.TextSecondary,
            error = colors.Error,
        )
    }

    CompositionLocalProvider(
        LocalNuvioColors provides colors,
        LocalNuvioExtendedColors provides extendedColors,
        LocalAppTheme provides AppTheme.AMBER,
        LocalThemePalette provides palette,
        LocalNuvioFocusRingStyle provides focusRingStyle,
    ) {
        MaterialTheme(
            colorScheme = materialColors,
            shapes = MaterialTheme.shapes,
            typography = MaterialTheme.typography,
            content = content,
        )
    }
}
