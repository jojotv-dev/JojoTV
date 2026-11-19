package com.nuvio.tv.ui.util

import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import kotlin.math.abs

private data class LogoColorProfile(
    val luminance: Double,
    val saturation: Double
)

private const val HERO_TEXT_BACKGROUND_ALPHA = 0.8

private fun logoColorProfile(bitmap: Bitmap): LogoColorProfile? {
    val width = bitmap.width
    val height = bitmap.height
    val step = maxOf(1, minOf(width, height) / 20)
    var totalLuminance = 0.0
    var totalSaturation = 0.0
    var count = 0
    val hsv = FloatArray(3)
    var x = 0
    while (x < width) {
        var y = 0
        while (y < height) {
            val pixel = bitmap.getPixel(x, y)
            val alpha = (pixel ushr 24) and 0xFF
            if (alpha > 50) {
                totalLuminance += pixelLuminance(pixel)
                android.graphics.Color.colorToHSV(pixel, hsv)
                totalSaturation += hsv[1]
                count++
            }
            y += step
        }
        x += step
    }
    if (count == 0) return null
    return LogoColorProfile(
        luminance = totalLuminance / count,
        saturation = totalSaturation / count
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
    if (profile.saturation >= 0.2 || profile.luminance in 0.3..0.7) return null

    val fallbackLuminance = colorLuminance(fallbackBackground)
    val backgroundLuminance = backdrop?.let(::heroTextAreaLuminance)?.let { backdropLuminance ->
        backdropLuminance * (1.0 - HERO_TEXT_BACKGROUND_ALPHA) +
            fallbackLuminance * HERO_TEXT_BACKGROUND_ALPHA
    } ?: fallbackLuminance
    if (abs(profile.luminance - backgroundLuminance) >= 0.35) return null

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
    return profile.luminance < 0.3 && profile.saturation < 0.2
}
