package com.example.learningpandaai.features.profile.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.learningpandaai.core.designsystem.components.appOutlinedTextFieldColors
import com.example.learningpandaai.core.designsystem.theme.StatusError
import com.example.learningpandaai.features.profile.presentation.DeleteAccountDialogState
import com.example.learningpandaai.features.profile.presentation.DeleteAccountStep
import com.example.learningpandaai.features.profile.presentation.ProfileLabels

@Composable
fun SignOutConfirmDialog(
    labels: ProfileLabels,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = labels.signOutDialogTitle,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = labels.signOutDialogMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = colorScheme.onSurfaceVariant
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = labels.signOutConfirmLabel,
                    color = StatusError,
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = labels.signOutCancelLabel)
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
fun DeleteAccountDialog(
    labels: ProfileLabels,
    state: DeleteAccountDialogState,
    onDismiss: () -> Unit,
    onContinueToOtp: () -> Unit,
    onOtpChanged: (String) -> Unit,
    onConfirmDelete: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    when (state.step) {
        DeleteAccountStep.Warning -> {
            AlertDialog(
                onDismissRequest = onDismiss,
                title = {
                    Text(
                        text = labels.deleteDialogTitle,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = StatusError
                    )
                },
                text = {
                    Column {
                        Text(
                            text = labels.deleteDialogWarning,
                            style = MaterialTheme.typography.bodyMedium,
                            color = colorScheme.onSurfaceVariant
                        )
                        if (state.errorMessage != null) {
                            Text(
                                text = state.errorMessage,
                                style = MaterialTheme.typography.bodySmall,
                                color = StatusError,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Start
                            )
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = onContinueToOtp,
                        enabled = !state.isSendingOtp
                    ) {
                        if (state.isSendingOtp) {
                            CircularProgressIndicator(
                                modifier = Modifier.fillMaxWidth(0.25f),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = labels.deleteDialogContinueLabel,
                                color = StatusError,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                },
                dismissButton = {
                    TextButton(onClick = onDismiss) {
                        Text(text = labels.deleteCancelLabel)
                    }
                },
                shape = RoundedCornerShape(20.dp)
            )
        }

        DeleteAccountStep.OtpEntry -> {
            AlertDialog(
                onDismissRequest = { if (!state.isDeleting) onDismiss() },
                title = {
                    Text(
                        text = labels.deleteOtpTitle,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column {
                        Text(
                            text = labels.deleteOtpMessage,
                            style = MaterialTheme.typography.bodyMedium,
                            color = colorScheme.onSurfaceVariant
                        )
                        OutlinedTextField(
                            value = state.otpInput,
                            onValueChange = onOtpChanged,
                            label = { Text(labels.deleteOtpHint) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            enabled = !state.isDeleting && !state.isSendingOtp,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                            shape = RoundedCornerShape(14.dp),
                            colors = appOutlinedTextFieldColors(
                                focusedBorderColor = colorScheme.secondary,
                                focusedLabelColor = colorScheme.secondary,
                                cursorColor = colorScheme.secondary
                            )
                        )
                        if (state.isSendingOtp) {
                            Text(
                                text = "Sending code…",
                                style = MaterialTheme.typography.bodySmall,
                                color = colorScheme.onSurfaceVariant
                            )
                        }
                        if (state.errorMessage != null) {
                            Text(
                                text = state.errorMessage,
                                style = MaterialTheme.typography.bodySmall,
                                color = StatusError
                            )
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = onConfirmDelete,
                        enabled = !state.isDeleting && !state.isSendingOtp
                    ) {
                        if (state.isDeleting) {
                            CircularProgressIndicator(
                                modifier = Modifier.fillMaxWidth(0.25f),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = labels.deleteConfirmLabel,
                                color = StatusError,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = onDismiss,
                        enabled = !state.isDeleting
                    ) {
                        Text(text = labels.deleteCancelLabel)
                    }
                },
                shape = RoundedCornerShape(20.dp)
            )
        }
    }
}
