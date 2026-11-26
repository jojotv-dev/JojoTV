package com.nuvio.tv.ui.util

import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import kotlin.math.abs

private data class LogoColorProfile(
    val darkPixelRatio: Double,
    val lightPixelRatio: Double,
    val coloredPixelRatio: Double,
)

private const val HERO_TEXT_BACKGROUND_ALPHA = 0.8
private const val LOGO_DARK_PIXEL_LUMINANCE = 0.42
private const val LOGO_LIGHT_PIXEL_LUMINANCE = 0.58
private const val LOGO_TONE_PIXEL_RATIO = 0.4
private const val LOGO_COLORED_PIXEL_RATIO = 0.08

private fun logoColorProfile(bitmap: Bitmap): LogoColorProfile? {
    val width = bitmap.width
    val height = bitmap.height
    val step = maxOf(1, minOf(width, height) / 32)
    var totalWeight = 0.0
    var darkWeight = 0.0
    var lightWeight = 0.0
    var coloredWeight = 0.0
    var x = 0
    while (x < width) {
        var y = 0
        while (y < height) {
            val pixel = bitmap.getPixel(x, y)
            val alpha = (pixel ushr 24) and 0xFF
            if (alpha > 50) {
                val weight = alpha / 255.0
                val luminance = pixelLuminance(pixel)
                totalWeight += weight
                if (luminance < LOGO_DARK_PIXEL_LUMINANCE) darkWeight += weight
                if (luminance > LOGO_LIGHT_PIXEL_LUMINANCE) lightWeight += weight

                val red = ((pixel ushr 16) and 0xFF) / 255.0
                val green = ((pixel ushr 8) and 0xFF) / 255.0
                val blue = (pixel and 0xFF) / 255.0
                val brightestChannel = maxOf(red, green, blue)
                val channelSpread = brightestChannel - minOf(red, green, blue)
                // Ignore tiny RGB differences in near-black antialiasing; HSV saturation
                // makes those artifacts look colorful even when the logo appears black.
                if (brightestChannel >= 0.22 && channelSpread >= 0.10) {
                    coloredWeight += weight
                }
            }
            y += step
        }
        x += step
    }
    if (totalWeight == 0.0) return null
    return LogoColorProfile(
        darkPixelRatio = darkWeight / totalWeight,
        lightPixelRatio = lightWeight / totalWeight,
        coloredPixelRatio = coloredWeight / totalWeight,
    )
}

/**
 * Chooses a monochrome logo tint only when the logo and the rendered hero background would
 * blend together. The backdrop sample is biased toward the lower-left title area and blended
 * with the hero's strong background gradient.
 */
internal fun logoContrastTint(
    logo: Bitmap,
    backdrop: Bitmap?,
    fallbackBackground: Color
): Color? {
    val profile = logoColorProfile(logo) ?: return null
    if (profile.coloredPixelRatio > LOGO_COLORED_PIXEL_RATIO) return null
    val logoLuminance = when {
        profile.darkPixelRatio >= LOGO_TONE_PIXEL_RATIO -> 0.0
        profile.lightPixelRatio >= LOGO_TONE_PIXEL_RATIO -> 1.0
        else -> return null
    }

    val fallbackLuminance = colorLuminance(fallbackBackground)
    val backgroundLuminance = backdrop?.let(::heroTextAreaLuminance)?.let { backdropLuminance ->
        backdropLuminance * (1.0 - HERO_TEXT_BACKGROUND_ALPHA) +
            fallbackLuminance * HERO_TEXT_BACKGROUND_ALPHA
    } ?: fallbackLuminance
    if (abs(logoLuminance - backgroundLuminance) >= 0.35) return null

    return if (backgroundLuminance < 0.5) Color.White else Color.Black
}

private fun heroTextAreaLuminance(bitmap: Bitmap): Double? {
    val right = maxOf(1, (bitmap.width * 0.55f).toInt())
    val top = (bitmap.height * 0.4f).toInt().coerceIn(0, bitmap.height - 1)
    val regionWidth = right
    val regionHeight = bitmap.height - top
    val step = maxOf(1, minOf(regionWidth, regionHeight) / 20)
    var totalLuminance = 0.0
    var count = 0
    var x = 0
    while (x < right) {
        var y = top
        while (y < bitmap.height) {
            val pixel = bitmap.getPixel(x, y)
            if (((pixel ushr 24) and 0xFF) > 50) {
                totalLuminance += pixelLuminance(pixel)
                count++
            }
            y += step
        }
        x += step
    }
    return if (count == 0) null else totalLuminance / count
}

private fun pixelLuminance(pixel: Int): Double {
    val r = ((pixel ushr 16) and 0xFF) / 255.0
    val g = ((pixel ushr 8) and 0xFF) / 255.0
    val b = (pixel and 0xFF) / 255.0
    return 0.2126 * r + 0.7152 * g + 0.0722 * b
}

private fun colorLuminance(color: Color): Double =
    0.2126 * color.red + 0.7152 * color.green + 0.0722 * color.blue

/**
 * Returns true when a logo's visible pixels are dark and nearly monochrome.
 * Colorful artwork is left unchanged.
 */
internal fun isLogoDarkAndMonochrome(bitmap: Bitmap): Boolean {
    val profile = logoColorProfile(bitmap) ?: return false
    return profile.darkPixelRatio >= LOGO_TONE_PIXEL_RATIO &&
        profile.coloredPixelRatio <= LOGO_COLORED_PIXEL_RATIO
}
