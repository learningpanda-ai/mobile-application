package com.example.learningpandaai.core.designsystem.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.learningpandaai.core.designsystem.theme.BrandSecondary
import com.example.learningpandaai.core.designsystem.theme.StatusError

/**
 * Theme-aware [OutlinedTextField] colors that keep input text readable in light and dark mode.
 * Always sets [focusedTextColor] and [unfocusedTextColor] from [MaterialTheme.colorScheme.onSurface].
 */
@Composable
fun appOutlinedTextFieldColors(
    focusedBorderColor: Color = BrandSecondary,
    unfocusedBorderColor: Color = MaterialTheme.colorScheme.outlineVariant,
    focusedLabelColor: Color = BrandSecondary,
    unfocusedLabelColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    focusedContainerColor: Color = Color.Transparent,
    unfocusedContainerColor: Color = Color.Transparent,
    cursorColor: Color = BrandSecondary,
): TextFieldColors {
    val onSurface = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant

    return OutlinedTextFieldDefaults.colors(
        focusedTextColor = onSurface,
        unfocusedTextColor = onSurface,
        disabledTextColor = onSurface.copy(alpha = 0.38f),
        focusedBorderColor = focusedBorderColor,
        unfocusedBorderColor = unfocusedBorderColor,
        disabledBorderColor = unfocusedBorderColor.copy(alpha = 0.38f),
        focusedLabelColor = focusedLabelColor,
        unfocusedLabelColor = unfocusedLabelColor,
        disabledLabelColor = unfocusedLabelColor.copy(alpha = 0.38f),
        focusedPlaceholderColor = onSurfaceVariant,
        unfocusedPlaceholderColor = onSurfaceVariant,
        cursorColor = cursorColor,
        focusedContainerColor = focusedContainerColor,
        unfocusedContainerColor = unfocusedContainerColor,
        disabledContainerColor = unfocusedContainerColor.copy(alpha = 0.38f),
        errorTextColor = onSurface,
        errorBorderColor = StatusError,
        errorLabelColor = StatusError,
        errorSupportingTextColor = StatusError,
        errorCursorColor = StatusError,
        errorContainerColor = focusedContainerColor,
    )
}
