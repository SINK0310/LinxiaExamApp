package com.linxia.exam.presentation.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF1B5E20), // 临夏绿
    primaryContainer = Color(0xFFC8E6C9),
    secondary = Color(0xFFE65100), // 橙色
    secondaryContainer = Color(0xFFFFE0B2),
    tertiary = Color(0xFF1565C0), // 蓝色
    tertiaryContainer = Color(0xFFBBDEFB),
    surface = Color(0xFFFAFAFA),
    surfaceVariant = Color(0xFFF5F5F5),
    background = Color(0xFFFFFFFF),
    error = Color(0xFFB00020),
    onPrimary = Color(0xFFFFFFFF),
    onPrimaryContainer = Color(0xFF1B5E20),
    onSecondary = Color(0xFFFFFFFF),
    onSecondaryContainer = Color(0xFFE65100),
    onTertiary = Color(0xFFFFFFFF),
    onTertiaryContainer = Color(0xFF0D47A1),
    onSurface = Color(0xFF1D1D1D),
    onSurfaceVariant = Color(0xFF424242),
    onBackground = Color(0xFF1D1D1D),
    onError = Color(0xFFFFFFFF),
    outline = Color(0xFF90A4AE),
    outlineVariant = Color(0xFFB0BEC5),
    scrim = Color(0xFF000000),
    shadow = Color(0xFF000000),
    inverseSurface = Color(0xFF2D2D2D),
    inverseOnSurface = Color(0xFFFFFFFF),
    inversePrimary = Color(0xFFA5D6A7)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFA5D6A7),
    primaryContainer = Color(0xFF2E7D32),
    secondary = Color(0xFFFFB74D),
    secondaryContainer = Color(0xFFBF360C),
    tertiary = Color(0xFF64B5F6),
    tertiaryContainer = Color(0xFF1565C0),
    surface = Color(0xFF1E1E1E),
    surfaceVariant = Color(0xFF2D2D2D),
    background = Color(0xFF121212),
    error = Color(0xFFCF6679),
    onPrimary = Color(0xFF1B5E20),
    onPrimaryContainer = Color(0xFFA5D6A7),
    onSecondary = Color(0xFFBF360C),
    onSecondaryContainer = Color(0xFFFFB74D),
    onTertiary = Color(0xFF0D47A1),
    onTertiaryContainer = Color(0xFF64B5F6),
    onSurface = Color(0xFFFFFFFF),
    onSurfaceVariant = Color(0xFFB0BEC5),
    onBackground = Color(0xFFFFFFFF),
    onError = Color(0xFF000000),
    outline = Color(0xFF546E7A),
    outlineVariant = Color(0xFF455A64),
    scrim = Color(0xFF000000),
    shadow = Color(0xFF000000),
    inverseSurface = Color(0xFFFAFAFA),
    inverseOnSurface = Color(0xFF1D1D1D),
    inversePrimary = Color(0xFF1B5E20)
)

@Composable
fun LinxiaTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = LinxiaTypography,
        content = content
    )
}