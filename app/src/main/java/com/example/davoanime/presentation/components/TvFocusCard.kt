package com.example.davoanime.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.davoanime.presentation.util.LocalIsTv

@Composable
fun TvFocusCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val isTv = LocalIsTv.current
    if (!isTv) {
        Box(modifier = modifier) { content() }
        return
    }

    var isFocused by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.05f else 1f,
        animationSpec = tween(durationMillis = 300),
        label = "tvFocusScale"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .then(
                if (isFocused) Modifier.border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(16.dp)
                ) else Modifier
            )
            .onFocusChanged { isFocused = it.isFocused }
            .focusable()
    ) {
        content()
    }
}
