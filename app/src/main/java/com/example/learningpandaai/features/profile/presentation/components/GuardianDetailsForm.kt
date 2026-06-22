package com.example.learningpandaai.features.profile.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import com.example.learningpandaai.core.designsystem.components.appOutlinedTextFieldColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.learningpandaai.core.designsystem.theme.TextPrimary
import com.example.learningpandaai.core.designsystem.theme.BorderSubtle
import com.example.learningpandaai.core.designsystem.theme.PureWhite
import com.example.learningpandaai.core.designsystem.theme.BorderDefault
import com.example.learningpandaai.core.designsystem.theme.TextTertiary
import com.example.learningpandaai.core.designsystem.theme.TextOnChip
import com.example.learningpandaai.features.profile.presentation.GuardianFormState

@Composable
fun GuardianDetailsForm(
    sectionTitle: String,
    guardian: GuardianFormState,
    nameLabel: String,
    phoneLabel: String,
    onNameChanged: (String) -> Unit,
    onPhoneChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    ProfileCard(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.People,
                contentDescription = null,
                tint = TextOnChip
            )
            Text(
                text = sectionTitle,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold
            )
        }
        Column(
            modifier = Modifier.padding(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = guardian.name,
                onValueChange = onNameChanged,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(nameLabel) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = guardianFieldColors()
            )
            OutlinedTextField(
                value = guardian.phone,
                onValueChange = onPhoneChanged,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(phoneLabel) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                shape = RoundedCornerShape(12.dp),
                colors = guardianFieldColors()
            )
        }
    }
}

@Composable
private fun guardianFieldColors() = appOutlinedTextFieldColors(
    focusedBorderColor = BorderDefault,
    unfocusedBorderColor = BorderSubtle,
    focusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
    focusedContainerColor = MaterialTheme.colorScheme.surface,
    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
    cursorColor = MaterialTheme.colorScheme.onSurface,
)
