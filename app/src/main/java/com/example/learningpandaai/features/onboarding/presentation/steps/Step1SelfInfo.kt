package com.example.learningpandaai.features.onboarding.presentation.steps

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.learningpandaai.core.data.IndiaLocations
import com.example.learningpandaai.core.designsystem.components.EmailDomainChipRow
import com.example.learningpandaai.core.designsystem.components.appOutlinedTextFieldColors
import com.example.learningpandaai.core.designsystem.theme.StatusError
import com.example.learningpandaai.features.onboarding.presentation.OnboardingUiState
import com.example.learningpandaai.features.onboarding.presentation.OnboardingViewModel
import com.example.learningpandaai.features.onboarding.presentation.components.OnboardingDimens
import com.example.learningpandaai.features.onboarding.presentation.components.OnboardingFieldShape
import com.example.learningpandaai.features.onboarding.presentation.components.OnboardingSectionCard
import com.example.learningpandaai.features.onboarding.presentation.components.OnboardingStepTitle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Step1SelfInfo(
    uiState: OnboardingUiState,
    viewModel: OnboardingViewModel
) {
    val languageOptions = listOf("English")
    val statesList = IndiaLocations.states
    val availableCities = IndiaLocations.citiesFor(uiState.selectedState)

    var languageDropdownExpanded by remember { mutableStateOf(false) }
    var stateDropdownExpanded by remember { mutableStateOf(false) }
    var cityDropdownExpanded by remember { mutableStateOf(false) }

    val colorScheme = MaterialTheme.colorScheme
    val fieldColors = appOutlinedTextFieldColors(
        focusedBorderColor = colorScheme.secondary,
        unfocusedBorderColor = colorScheme.outline,
        focusedLabelColor = colorScheme.secondary,
        cursorColor = colorScheme.secondary,
        focusedContainerColor = colorScheme.surface,
        unfocusedContainerColor = colorScheme.surface
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(OnboardingDimens.SectionSpacing),
        horizontalAlignment = Alignment.Start,
        modifier = Modifier.fillMaxWidth()
    ) {
        OnboardingStepTitle(
            title = "Tell us about yourself",
            subtitle = "A few quick details to personalize your learning path."
        )

        if (uiState.errorMessage != null) {
            Text(
                text = uiState.errorMessage,
                style = MaterialTheme.typography.bodySmall,
                color = StatusError,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp)
            )
        }

        OnboardingSectionCard(title = "Student details") {
            Row(horizontalArrangement = Arrangement.spacedBy(OnboardingDimens.FieldSpacing)) {
                OutlinedTextField(
                    value = uiState.firstName,
                    onValueChange = { viewModel.onFirstNameChanged(it) },
                    label = { Text("First name") },
                    placeholder = {
                        Text("e.g. Aarav", color = colorScheme.onSurfaceVariant)
                    },
                    modifier = Modifier.weight(1f),
                    shape = OnboardingFieldShape,
                    singleLine = true,
                    colors = fieldColors
                )
                OutlinedTextField(
                    value = uiState.lastName,
                    onValueChange = { viewModel.onLastNameChanged(it) },
                    label = { Text("Last name") },
                    placeholder = {
                        Text("e.g. Sharma", color = colorScheme.onSurfaceVariant)
                    },
                    modifier = Modifier.weight(1f),
                    shape = OnboardingFieldShape,
                    singleLine = true,
                    colors = fieldColors
                )
            }

            ExposedDropdownMenuBox(
                expanded = languageDropdownExpanded,
                onExpandedChange = { languageDropdownExpanded = it }
            ) {
                OutlinedTextField(
                    value = uiState.appLanguage,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Language") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = languageDropdownExpanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    shape = OnboardingFieldShape,
                    colors = fieldColors
                )
                ExposedDropdownMenu(
                    expanded = languageDropdownExpanded,
                    onDismissRequest = { languageDropdownExpanded = false }
                ) {
                    languageOptions.forEach { language ->
                        DropdownMenuItem(
                            text = { Text(language) },
                            onClick = {
                                viewModel.onAppLanguageChanged(language)
                                languageDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            ExposedDropdownMenuBox(
                expanded = stateDropdownExpanded,
                onExpandedChange = { stateDropdownExpanded = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = uiState.selectedState,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("State") },
                    placeholder = { Text("Choose state", color = colorScheme.onSurfaceVariant) },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = stateDropdownExpanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    shape = OnboardingFieldShape,
                    singleLine = true,
                    colors = fieldColors
                )
                ExposedDropdownMenu(
                    expanded = stateDropdownExpanded,
                    onDismissRequest = { stateDropdownExpanded = false }
                ) {
                    statesList.forEach { stateName ->
                        DropdownMenuItem(
                            text = { OnboardingDropdownLabel(stateName) },
                            onClick = {
                                viewModel.onStateChanged(stateName)
                                stateDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            ExposedDropdownMenuBox(
                expanded = cityDropdownExpanded,
                onExpandedChange = {
                    if (availableCities.isNotEmpty()) cityDropdownExpanded = it
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = uiState.cityInput,
                    onValueChange = {},
                    readOnly = true,
                    enabled = availableCities.isNotEmpty(),
                    label = { Text("City / town") },
                    placeholder = {
                        Text(
                            if (availableCities.isNotEmpty()) "Choose city" else "Select state first",
                            color = colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = cityDropdownExpanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    shape = OnboardingFieldShape,
                    singleLine = true,
                    colors = fieldColors
                )
                ExposedDropdownMenu(
                    expanded = cityDropdownExpanded,
                    onDismissRequest = { cityDropdownExpanded = false }
                ) {
                    availableCities.forEach { cityName ->
                        DropdownMenuItem(
                            text = { OnboardingDropdownLabel(cityName) },
                            onClick = {
                                viewModel.onCityChanged(cityName)
                                cityDropdownExpanded = false
                            }
                        )
                    }
                }
            }
        }

        OnboardingSectionCard(title = "Parent or guardian contact") {
            HorizontalDivider(color = colorScheme.outline.copy(alpha = 0.5f))

            OutlinedTextField(
                value = uiState.parentName,
                onValueChange = { viewModel.onParentNameChanged(it) },
                label = { Text("Parent / guardian name") },
                placeholder = {
                    Text("e.g. Priya Sharma", color = colorScheme.onSurfaceVariant)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = OnboardingFieldShape,
                singleLine = true,
                colors = fieldColors
            )

            OutlinedTextField(
                value = uiState.parentMobile,
                onValueChange = { viewModel.onParentMobileChanged(it) },
                label = { Text("Parent mobile") },
                placeholder = {
                    Text("e.g. 9876543210", color = colorScheme.onSurfaceVariant)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = OnboardingFieldShape,
                singleLine = true,
                isError = uiState.parentPhoneError,
                supportingText = if (uiState.parentPhoneError) {
                    {
                        Text(
                            text = "Enter exactly 10 digits",
                            style = MaterialTheme.typography.bodySmall,
                            color = StatusError
                        )
                    }
                } else {
                    null
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                colors = fieldColors
            )

            OutlinedTextField(
                value = uiState.parentEmail,
                onValueChange = { viewModel.onParentEmailChanged(it) },
                label = { Text("Parent email") },
                placeholder = {
                    Text("e.g. parent@gmail.com", color = colorScheme.onSurfaceVariant)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = OnboardingFieldShape,
                singleLine = true,
                isError = uiState.parentEmailError,
                supportingText = if (uiState.parentEmailError) {
                    {
                        Text(
                            text = "Include @ in the email",
                            style = MaterialTheme.typography.bodySmall,
                            color = StatusError
                        )
                    }
                } else {
                    null
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors = fieldColors
            )

            EmailDomainChipRow(
                currentEmail = uiState.parentEmail,
                onEmailUpdated = { viewModel.onParentEmailChanged(it) }
            )
        }
    }
}

@Composable
private fun OnboardingDropdownLabel(text: String) {
    Text(
        text = text,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.fillMaxWidth()
    )
}
