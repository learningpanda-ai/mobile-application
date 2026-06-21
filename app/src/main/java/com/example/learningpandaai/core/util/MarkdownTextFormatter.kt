package com.example.learningpandaai.core.util

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle

/**
 * Lightweight inline markdown for chat bubbles — supports **bold** segments from the tutor API.
 */
object MarkdownTextFormatter {

    private val boldPattern = Regex("""\*\*(.+?)\*\*""")

    fun toAnnotatedString(raw: String): AnnotatedString = buildAnnotatedString {
        var cursor = 0
        boldPattern.findAll(raw).forEach { match ->
            if (match.range.first > cursor) {
                append(raw.substring(cursor, match.range.first))
            }
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                append(match.groupValues[1])
            }
            cursor = match.range.last + 1
        }
        if (cursor < raw.length) {
            append(raw.substring(cursor))
        }
    }
}
