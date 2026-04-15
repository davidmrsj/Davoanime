package com.example.davoanime.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp

private val defaultTypography = Typography()

val TvTypography = Typography(
    displayLarge = defaultTypography.displayLarge.copy(fontSize = (57 * 1.3).sp),
    displayMedium = defaultTypography.displayMedium.copy(fontSize = (45 * 1.3).sp),
    displaySmall = defaultTypography.displaySmall.copy(fontSize = (36 * 1.3).sp),
    headlineLarge = defaultTypography.headlineLarge.copy(fontSize = (32 * 1.3).sp),
    headlineMedium = defaultTypography.headlineMedium.copy(fontSize = (28 * 1.3).sp),
    headlineSmall = defaultTypography.headlineSmall.copy(fontSize = (24 * 1.3).sp),
    titleLarge = defaultTypography.titleLarge.copy(fontSize = (22 * 1.3).sp),
    titleMedium = defaultTypography.titleMedium.copy(fontSize = (16 * 1.3).sp),
    titleSmall = defaultTypography.titleSmall.copy(fontSize = (14 * 1.3).sp),
    bodyLarge = defaultTypography.bodyLarge.copy(fontSize = (16 * 1.3).sp),
    bodyMedium = defaultTypography.bodyMedium.copy(fontSize = (14 * 1.3).sp),
    bodySmall = defaultTypography.bodySmall.copy(fontSize = (12 * 1.3).sp),
    labelLarge = defaultTypography.labelLarge.copy(fontSize = (14 * 1.3).sp),
    labelMedium = defaultTypography.labelMedium.copy(fontSize = (12 * 1.3).sp),
    labelSmall = defaultTypography.labelSmall.copy(fontSize = (11 * 1.3).sp)
)
