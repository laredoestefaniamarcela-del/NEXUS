package com.nexus.mobilestore.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ColorScheme = lightColorScheme(
    primary = AzulOscuro,
    secondary = AzulMedio,
    tertiary = CelesteActivo,
    background = Blanco,
    surface = GrisClaro,
    onPrimary = Blanco,
    onSecondary = Blanco,
    onBackground = Negro,
    onSurface = Negro,
)

@Composable
fun NexusMobileStoreTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ColorScheme,
        typography = Typography,
        content = content
    )
}