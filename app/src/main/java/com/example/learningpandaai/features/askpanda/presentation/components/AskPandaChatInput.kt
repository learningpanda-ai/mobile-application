package com.example.learningpandaai.features.askpanda.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.learningpandaai.core.designsystem.components.appOutlinedTextFieldColors
import com.example.learningpandaai.core.util.ChatInputLimits

@Composable
fun AskPandaChatInput(
    value: String,
    placeholder: String,
    sendContentDescription: String,
    isSendEnabled: Boolean,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    val inputShape = RoundedCornerShape(12.dp)

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.weight(1f),
            placeholder = {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorScheme.onSurfaceVariant
                )
            },
            shape = inputShape,
            colors = appOutlinedTextFieldColors(
                focusedBorderColor = colorScheme.outlineVariant,
                unfocusedBorderColor = Color.Transparent,
                focusedContainerColor = colorScheme.surfaceVariant,
                unfocusedContainerColor = colorScheme.surfaceVariant,
                cursorColor = colorScheme.onSurface,
            ),
            maxLines = 4,
            singleLine = false,
            supportingText = if (value.length >= ChatInputLimits.MESSAGE_MAX_LENGTH - 200) {
                {
                    Text(
                        text = "${value.length}/${ChatInputLimits.MESSAGE_MAX_LENGTH}",
                        style = MaterialTheme.typography.labelSmall,
                        color = colorScheme.onSurfaceVariant
                    )
                }
            } else {
                null
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions(
                onSend = {
                    if (isSendEnabled) onSend()
                }
            )
        )
        Box(
            modifier = Modifier
                .padding(start = 10.dp)
                .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
                .size(48.dp)
                .clip(CircleShape)
                .background(if (isSendEnabled) colorScheme.primary else colorScheme.surfaceVariant)
                .clickable(enabled = isSendEnabled, onClick = onSend),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = sendContentDescription,
                tint = if (isSendEnabled) colorScheme.onPrimary else colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
