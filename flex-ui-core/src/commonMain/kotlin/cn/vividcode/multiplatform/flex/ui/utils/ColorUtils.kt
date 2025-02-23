package cn.vividcode.multiplatform.flex.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import cn.vividcode.multiplatform.flex.ui.theme.LocalDarkTheme
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * 颜色变亮
 * @param factor 因子, < 1: 变暗, > 1: 变亮
 */
@Deprecated("")
private fun Color.brightness(factor: Float): Color {
	val hsl = this.rgbToHsl()
	val h = hsl[0]
	val s = hsl[1]
	val l = min(1f, max(0f, hsl[2] * factor))
	return hslToRgb(h, s, l)
}

@Deprecated("")
/**
 * 内容颜色变深
 */
internal val Color.darkenWithContent: Color
	@Composable
	get() = this.brightness(
		factor = if (LocalDarkTheme.current) 0.95f else 0.85f
	)

/**
 * 内容颜色变浅
 */
@Deprecated("")
internal val Color.lightenWithContent: Color
	@Composable
	get() = this.brightness(
		factor = if (LocalDarkTheme.current) 1.05f else 1.15f
	)

/**
 * 颜色变深
 */
@Deprecated("")
internal val Color.darkenWithColor: Color
	@Composable
	get() = this.brightness(
		factor = if (LocalDarkTheme.current) 0.94f else 0.9f
	)

/**
 * 颜色变浅
 */
@Deprecated("")
internal val Color.lightenWithColor: Color
	@Composable
	get() = this.brightness(
		factor = if (LocalDarkTheme.current) 1.06f else 1.1f
	)

/**
 * 内容颜色 (禁用)
 */
@Deprecated("")
internal val Color.disabledWithContent: Color
	get() = this.copy(alpha = 0.8f)

/**
 * 颜色 (禁用)
 */
@Deprecated("")
internal val Color.disabledWithColor: Color
	get() = this.copy(alpha = 0.6f)

/**
 * RGB 转 HSL
 */
@Deprecated("")
private fun Color.rgbToHsl(): FloatArray {
	val max = maxOf(red, green, blue)
	val min = minOf(red, green, blue)
	val delta = max - min
	val l = (max + min) / 2f
	val s = if (delta == 0f) 0f else delta / (1 - abs(2 * l - 1))
	val h = if (delta == 0f) 0f else when (max) {
		red -> ((green - blue) / delta + (if (green < blue) 6 else 0)) / 6f
		green -> ((blue - red) / delta + 2) / 6f
		else -> ((red - green) / delta + 4) / 6f
	}
	return floatArrayOf(h, s, l)
}

/**
 * HSL 转 RGB
 */
@Deprecated("")
private fun hslToRgb(h: Float, s: Float, l: Float): Color {
	val c = (1 - abs(2 * l - 1)) * s
	val x = c * (1 - abs((h * 6) % 2 - 1))
	val m = l - c / 2
	
	val (r, g, b) = when {
		h < 1f / 6f -> Triple(c, x, 0f)
		h < 2f / 6f -> Triple(x, c, 0f)
		h < 3f / 6f -> Triple(0f, c, x)
		h < 4f / 6f -> Triple(0f, x, c)
		h < 5f / 6f -> Triple(x, 0f, c)
		else -> Triple(c, 0f, x)
	}
	
	return Color(r + m, g + m, b + m)
}

internal fun Color.darken(fraction: Float): Color {
	return lerp(this, Color.Black, fraction)
}

internal fun Color.lighten(fraction: Float): Color {
	return lerp(this, Color.White, fraction)
}