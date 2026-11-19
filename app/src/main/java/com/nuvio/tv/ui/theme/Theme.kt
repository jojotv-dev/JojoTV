package com.nuvio.tv.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.MaterialTheme as ComposeMaterialTheme
import androidx.compose.material3.Shapes as ComposeShapes
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Shapes
import androidx.tv.material3.darkColorScheme
import com.nuvio.tv.domain.model.AppFont
import com.nuvio.tv.domain.model.AppTheme
import com.nuvio.tv.domain.model.CustomThemeColors
import com.nuvio.tv.domain.model.SettingsUiStyle

data class NuvioExtendedColors(
    val backgroundElevated: Color,
    val backgroundCard: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val focusRing: Color,
    val focusBackground: Color,
    val rating: Color
)

val LocalNuvioColors = staticCompositionLocalOf {
    NuvioColorScheme(ThemeColors.Ocean)
}

val LocalNuvioExtendedColors = staticCompositionLocalOf {
    NuvioExtendedColors(
        backgroundElevated = Color(0xFF1A1A1A),
        backgroundCard = Color(0xFF242424),
        textSecondary = Color(0xFFB3B3B3),
        textTertiary = Color(0xFF808080),
        focusRing = ThemeColors.Ocean.focusRing,
        focusBackground = ThemeColors.Ocean.focusBackground,
        rating = Color(0xFFFFD700)
    )
}

val LocalNuvioTextStyles = staticCompositionLocalOf { NuvioTextStyles }

val LocalAppTheme = staticCompositionLocalOf { AppTheme.WHITE }

val LocalThemePalette = staticCompositionLocalOf { ThemeColors.White }

val LocalSettingsUiStyle = staticCompositionLocalOf { SettingsUiStyle.CLASSIC }

val LocalNuvioFocusRingStyle = staticCompositionLocalOf {
    createFocusRingStyle(ThemeColors.Ocean)
}

private object AppCornerRadius {
    val dp = mutableIntStateOf(12)
}

private data class AppCornerSize(private val source: CornerSize) : CornerSize {
    override fun toPx(shapeSize: Size, density: Density): Float {
        if (source.toPx(shapeSize, density) <= 0f) return 0f
        return with(density) { AppCornerRadius.dp.intValue.dp.toPx() }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun NuvioTheme(
    appTheme: AppTheme = AppTheme.WHITE,
    appFont: AppFont = AppFont.INTER,
    amoledMode: Boolean = false,
    amoledSurfacesMode: Boolean = false,
    settingsUiStyle: SettingsUiStyle = SettingsUiStyle.CLASSIC,
    customThemeColors: CustomThemeColors = CustomThemeColors.Default,
    cornerRadiusDp: Int = 12,
    content: @Composable () -> Unit
) {
    val palette = androidx.compose.runtime.remember(appTheme, customThemeColors) {
        ThemeColors.getColorPalette(appTheme, customThemeColors)
    }
    val focusRingStyle = createFocusRingStyle(palette)
    val colorScheme = NuvioColorScheme(
        palette = palette,
        amoledMode = amoledMode,
        amoledSurfacesMode = amoledSurfacesMode
    )
    val typography = buildNuvioTypography(getFontFamily(appFont))
    val textStyles = buildNuvioTextStyles(typography)
    val materialCornerShape = remember(cornerRadiusDp) {
        RoundedCornerShape(cornerRadiusDp.coerceAtLeast(0).dp)
    }
    val materialShapes = remember(materialCornerShape) {
        Shapes(
            extraSmall = materialCornerShape,
            small = materialCornerShape,
            medium = materialCornerShape,
            large = materialCornerShape,
            extraLarge = materialCornerShape
        )
    }
    val composeShapes = remember(materialCornerShape) {
        ComposeShapes(
            extraSmall = materialCornerShape,
            small = materialCornerShape,
            medium = materialCornerShape,
            large = materialCornerShape,
            extraLarge = materialCornerShape
        )
    }

    SideEffect {
        AppCornerRadius.dp.intValue = cornerRadiusDp.coerceAtLeast(0)
    }

    val materialColorScheme = darkColorScheme(
        primary = colorScheme.Primary,
        onPrimary = colorScheme.OnPrimary,
        secondary = colorScheme.Secondary,
        onSecondary = colorScheme.OnSecondary,
        background = colorScheme.Background,
        surface = colorScheme.Surface,
        surfaceVariant = colorScheme.SurfaceVariant,
        onBackground = colorScheme.TextPrimary,
        onSurface = colorScheme.TextPrimary,
        onSurfaceVariant = colorScheme.TextSecondary,
        error = colorScheme.Error
    )

    val extendedColors = NuvioExtendedColors(
        backgroundElevated = colorScheme.BackgroundElevated,
        backgroundCard = colorScheme.BackgroundCard,
        textSecondary = colorScheme.TextSecondary,
        textTertiary = colorScheme.TextTertiary,
        focusRing = colorScheme.FocusRing,
        focusBackground = colorScheme.FocusBackground,
        rating = colorScheme.Rating
    )

    CompositionLocalProvider(
        LocalNuvioColors provides colorScheme,
        LocalNuvioExtendedColors provides extendedColors,
        LocalNuvioTextStyles provides textStyles,
        LocalAppTheme provides appTheme,
        LocalThemePalette provides palette,
        LocalSettingsUiStyle provides settingsUiStyle,
        LocalNuvioFocusRingStyle provides focusRingStyle
    ) {
        ComposeMaterialTheme(shapes = composeShapes) {
            MaterialTheme(
                colorScheme = materialColorScheme,
                shapes = materialShapes,
                typography = typography,
                content = content
            )
        }
    }
}

object NuvioTheme {
    val palette: ThemeColorPalette
        @Composable
        @ReadOnlyComposable
        get() = LocalThemePalette.current

    val colors: NuvioColorScheme
        @Composable
        @ReadOnlyComposable
        get() = LocalNuvioColors.current

    val extendedColors: NuvioExtendedColors
        @Composable
        @ReadOnlyComposable
        get() = LocalNuvioExtendedColors.current

    val textStyles: NuvioTextStyleTokens
        @Composable
        @ReadOnlyComposable
        get() = LocalNuvioTextStyles.current

    val spacing: NuvioSpacingTokens
        get() = NuvioSpacing.tokens

    val radii: NuvioRadiusTokens
        get() = NuvioRadii.tokens

    val appCornerRadiusDp: Int
        get() = AppCornerRadius.dp.intValue

    val appCornerShape: RoundedCornerShape
        get() = configuredShape(RoundedCornerShape(1.dp))

    fun configuredShape(shape: RoundedCornerShape): RoundedCornerShape = RoundedCornerShape(
        topStart = AppCornerSize(shape.topStart),
        topEnd = AppCornerSize(shape.topEnd),
        bottomEnd = AppCornerSize(shape.bottomEnd),
        bottomStart = AppCornerSize(shape.bottomStart)
    )

    val shapes: NuvioShapeTokens
        get() = NuvioShapes.tokens.let { shapes ->
            shapes.copy(
                posterCard = configuredShape(shapes.posterCard as RoundedCornerShape),
                backdropCard = configuredShape(shapes.backdropCard as RoundedCornerShape),
                collectionCard = configuredShape(shapes.collectionCard as RoundedCornerShape),
                button = configuredShape(shapes.button as RoundedCornerShape),
                iconButton = configuredShape(shapes.iconButton as RoundedCornerShape),
                chip = configuredShape(shapes.chip as RoundedCornerShape),
                badge = configuredShape(shapes.badge as RoundedCornerShape),
                dialog = configuredShape(shapes.dialog as RoundedCornerShape),
                sidePanel = configuredShape(shapes.sidePanel as RoundedCornerShape),
                sidebar = configuredShape(shapes.sidebar as RoundedCornerShape),
                navItem = configuredShape(shapes.navItem as RoundedCornerShape),
                progress = configuredShape(shapes.progress as RoundedCornerShape),
                slider = configuredShape(shapes.slider as RoundedCornerShape),
                field = configuredShape(shapes.field as RoundedCornerShape),
                menu = configuredShape(shapes.menu as RoundedCornerShape)
            )
        }

    val sizes: NuvioSizeTokens
        get() = NuvioSizes.tokens

    val strokes: NuvioStrokeTokens
        get() = NuvioStrokes.tokens

    val elevations: NuvioElevationTokens
        get() = NuvioElevations.tokens

    val effects: NuvioEffectTokens
        get() = NuvioEffects.tokens

    val motion: NuvioMotionTokens
        get() = NuvioMotion.tokens

    val focus: NuvioFocusTokens
        get() = NuvioFocus.tokens

    val focusRing: NuvioFocusRingStyle
        @Composable
        @ReadOnlyComposable
        get() = LocalNuvioFocusRingStyle.current

    val layout: NuvioLayoutTokens
        get() = NuvioLayout.tokens

    val media: NuvioMediaTokens
        get() = NuvioMedia.tokens

    val components: NuvioComponentTokens
        get() = NuvioComponents.tokens.let { components ->
            val appRadius = AppCornerRadius.dp.intValue.dp
            components.copy(
                posterCard = components.posterCard.copy(cornerRadius = appRadius),
                backdropCard = components.backdropCard.copy(cornerRadius = appRadius),
                collectionCard = components.collectionCard.copy(cornerRadius = appRadius),
                continueWatchingCard = components.continueWatchingCard.copy(cornerRadius = appRadius),
                episodeCard = components.episodeCard.copy(cornerRadius = appRadius),
                sidebar = components.sidebar.copy(panelRadius = appRadius),
                dialog = components.dialog.copy(cornerRadius = appRadius),
                sidePanel = components.sidePanel.copy(cornerRadius = appRadius),
                settings = components.settings.copy(
                    containerRadius = appRadius,
                    secondaryCardRadius = appRadius
                ),
                skeletonCornerRadius = appRadius
            )
        }

    val currentTheme: AppTheme
        @Composable
        @ReadOnlyComposable
        get() = LocalAppTheme.current

    val settingsUiStyle: SettingsUiStyle
        @Composable
        @ReadOnlyComposable
        get() = LocalSettingsUiStyle.current
}
