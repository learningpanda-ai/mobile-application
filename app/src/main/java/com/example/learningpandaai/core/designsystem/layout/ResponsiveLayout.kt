package com.example.learningpandaai.core.designsystem.layout

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Breakpoint above which chat content is capped and centered (tablets / unfolded devices). */
val ExpandedScreenBreakpoint = 840.dp

/** Max readable width for chat-style screens on large displays. */
val ChatContentMaxWidth = 640.dp

/** Below this width, tab labels may collapse to icons-only to avoid clipping. */
val NarrowTabBreakpoint = 380.dp

enum class ScreenWidthClass {
    Compact,
    Expanded
}

fun Dp.toScreenWidthClass(): ScreenWidthClass =
    if (this >= ExpandedScreenBreakpoint) ScreenWidthClass.Expanded else ScreenWidthClass.Compact

/**
 * Centers content and caps width on large screens so phone layouts do not stretch edge-to-edge
 * on tablets. On compact phones the content uses the full available width.
 */
@Composable
fun ResponsiveContentWidth(
    modifier: Modifier = Modifier,
    maxContentWidth: Dp = ChatContentMaxWidth,
    content: @Composable BoxWithConstraintsScope.(ScreenWidthClass) -> Unit
) {
    BoxWithConstraints(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        val widthClass = maxWidth.toScreenWidthClass()
        val contentModifier = if (widthClass == ScreenWidthClass.Expanded) {
            Modifier.widthIn(max = maxContentWidth)
        } else {
            Modifier
        }
        BoxWithConstraints(modifier = contentModifier.fillMaxSize()) {
            content(widthClass)
        }
    }
}
