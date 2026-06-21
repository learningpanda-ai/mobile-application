package com.example.learningpandaai.features.askpanda.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ChatMarkdownText(
    text: String,
    style: TextStyle,
    color: Color,
    modifier: Modifier = Modifier
) {
    val lines = text.replace("\r\n", "\n").split('\n')
    var inCodeBlock = false
    val codeBlockLines = mutableListOf<String>()

    Column(modifier = modifier) {
        lines.forEach { rawLine ->
            val line = rawLine.trimEnd()
            when {
                line.trim() == "```" -> {
                    if (inCodeBlock) {
                        CodeBlockText(lines = codeBlockLines.toList(), color = color)
                        codeBlockLines.clear()
                        inCodeBlock = false
                    } else {
                        inCodeBlock = true
                    }
                }
                inCodeBlock -> codeBlockLines.add(line)
                line.isBlank() -> Spacer(modifier = Modifier.height(6.dp))
                line.startsWith("### ") -> MarkdownLineText(
                    text = line.removePrefix("### ").trim(),
                    style = style.copy(fontWeight = FontWeight.SemiBold),
                    color = color
                )
                line.startsWith("## ") -> MarkdownLineText(
                    text = line.removePrefix("## ").trim(),
                    style = style.copy(fontWeight = FontWeight.Bold, fontSize = style.fontSize * 1.02f),
                    color = color
                )
                line.startsWith("# ") -> MarkdownLineText(
                    text = line.removePrefix("# ").trim(),
                    style = style.copy(fontWeight = FontWeight.Bold, fontSize = style.fontSize * 1.08f),
                    color = color
                )
                isBulletLine(line) -> BulletLine(line = stripBulletPrefix(line), style = style, color = color)
                else -> MarkdownLineText(text = line, style = style, color = color)
            }
        }
        if (inCodeBlock && codeBlockLines.isNotEmpty()) {
            CodeBlockText(lines = codeBlockLines, color = color)
        }
    }
}

@Composable
private fun MarkdownLineText(text: String, style: TextStyle, color: Color) {
    Text(
        text = parseInlineMarkdown(text),
        style = style,
        color = color,
        modifier = Modifier.padding(vertical = 1.dp)
    )
}

@Composable
private fun BulletLine(line: String, style: TextStyle, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
    ) {
        Text(
            text = "•",
            style = style,
            color = color,
            modifier = Modifier.width(16.dp)
        )
        Text(
            text = parseInlineMarkdown(line),
            style = style,
            color = color,
            modifier = Modifier.weight(1f, fill = false)
        )
    }
}

@Composable
private fun CodeBlockText(lines: List<String>, color: Color) {
    val colorScheme = MaterialTheme.colorScheme
    Text(
        text = lines.joinToString("\n"),
        style = MaterialTheme.typography.bodySmall.copy(
            fontFamily = FontFamily.Monospace,
            lineHeight = 18.sp
        ),
        color = color,
        modifier = Modifier
            .padding(vertical = 4.dp)
            .background(
                color = colorScheme.onSurface.copy(alpha = 0.06f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 10.dp, vertical = 8.dp)
    )
}

private fun isBulletLine(line: String): Boolean {
    val trimmed = line.trimStart()
    return trimmed.startsWith("- ") ||
        trimmed.startsWith("* ") ||
        trimmed.startsWith("• ")
}

private fun stripBulletPrefix(line: String): String {
    val trimmed = line.trimStart()
    return when {
        trimmed.startsWith("- ") -> trimmed.removePrefix("- ").trim()
        trimmed.startsWith("* ") -> trimmed.removePrefix("* ").trim()
        trimmed.startsWith("• ") -> trimmed.removePrefix("• ").trim()
        else -> trimmed
    }
}

private fun parseInlineMarkdown(raw: String): AnnotatedString = buildAnnotatedString {
    var index = 0
    while (index < raw.length) {
        val boldStart = raw.indexOf("**", index)
        val codeStart = raw.indexOf('`', index)
        val italicStart = findItalicStart(raw, index)

        val next = listOf(
            boldStart.takeIf { it >= 0 }?.let { it to TokenType.BOLD },
            codeStart.takeIf { it >= 0 }?.let { it to TokenType.CODE },
            italicStart.takeIf { it >= 0 }?.let { it to TokenType.ITALIC }
        ).filterNotNull().minByOrNull { it.first }

        if (next == null) {
            append(raw.substring(index))
            break
        }

        val (start, type) = next
        if (start > index) append(raw.substring(index, start))

        when (type) {
            TokenType.BOLD -> {
                val end = raw.indexOf("**", start + 2)
                if (end < 0) {
                    append(raw.substring(start))
                    break
                }
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(raw.substring(start + 2, end))
                }
                index = end + 2
            }
            TokenType.CODE -> {
                val end = raw.indexOf('`', start + 1)
                if (end < 0) {
                    append(raw.substring(start))
                    break
                }
                withStyle(SpanStyle(fontFamily = FontFamily.Monospace, fontSize = 13.sp)) {
                    append(raw.substring(start + 1, end))
                }
                index = end + 1
            }
            TokenType.ITALIC -> {
                val marker = raw[start]
                val end = raw.indexOf(marker, start + 1)
                if (end < 0) {
                    append(raw.substring(start))
                    break
                }
                withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                    append(raw.substring(start + 1, end))
                }
                index = end + 1
            }
        }
    }
}

private enum class TokenType { BOLD, CODE, ITALIC }

private fun findItalicStart(raw: String, fromIndex: Int): Int {
    for (i in fromIndex until raw.length) {
        if (raw[i] != '*') continue
        if (i + 1 < raw.length && raw[i + 1] == '*') continue
        if (i > 0 && raw[i - 1] == '*') continue
        return i
    }
    return -1
}
