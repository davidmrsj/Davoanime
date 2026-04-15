package com.example.davoanime.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import com.example.davoanime.presentation.util.LocalIsTv

private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = OnPrimaryContainer,
    secondary = Secondary,
    onSecondary = OnSecondary,
    background = Background,
    onBackground = OnBackground,
    surface = Surface,
    onSurface = OnSurface,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = OnSurfaceVariant,
    error = Error,
    onError = OnError,
    outline = Outline,
    outlineVariant = OutlineVariant
)

@Composable
fun DavoanimeTheme(
    content: @Composable () -> Unit
) {
    val isTv = LocalIsTv.current
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = if (isTv) TvTypography else MaterialTheme.typography,
        content = content
    )
}
