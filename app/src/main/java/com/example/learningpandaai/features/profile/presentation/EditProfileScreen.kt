package com.example.learningpandaai.features.profile.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.learningpandaai.core.designsystem.components.EmailDomainChipRow
import com.example.learningpandaai.core.designsystem.components.appOutlinedTextFieldColors
import com.example.learningpandaai.core.designsystem.theme.StatusError
import com.example.learningpandaai.core.designsystem.theme.StatusErrorContainer
import com.example.learningpandaai.core.designsystem.components.ErrorStateContent
import com.example.learningpandaai.features.onboarding.presentation.steps.SelectablePillChip

private val EditFieldShape = RoundedCornerShape(14.dp)
private val EditCardShape = RoundedCornerShape(16.dp)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    viewModel: EditProfileViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val colorScheme = MaterialTheme.colorScheme

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                EditProfileUiEvent.Saved -> onSaved()
            }
        }
    }

    Scaffold(
        containerColor = colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Edit profile",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Update your details for a personalized experience",
                            style = MaterialTheme.typography.bodySmall,
                            color = colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorScheme.background
                )
            )
        },
        bottomBar = {
            if (!uiState.isLoading && uiState.loadError == null) {
                EditProfileBottomBar(
                    canSave = uiState.canSave,
                    isSaving = uiState.isSaving,
                    onSave = viewModel::saveProfile
                )
            }
        }
    ) { innerPadding ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = colorScheme.secondary)
                }
            }

            uiState.loadError != null -> {
                ErrorStateContent(
                    message = uiState.loadError.orEmpty(),
                    retryLabel = "Retry",
                    onRetry = viewModel::retryLoad,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }

            else -> {
                EditProfileForm(
                    uiState = uiState,
                    onFirstNameChanged = viewModel::onFirstNameChanged,
                    onLastNameChanged = viewModel::onLastNameChanged,
                    onGradeSelected = viewModel::onGradeSelected,
                    onBoardSelected = viewModel::onBoardSelected,
                    onParentMobileChanged = viewModel::onParentMobileChanged,
                    onParentEmailChanged = viewModel::onParentEmailChanged,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}

@Composable
private fun EditProfileBottomBar(
    canSave: Boolean,
    isSaving: Boolean,
    onSave: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Button(
            onClick = onSave,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            enabled = canSave && !isSaving,
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorScheme.primary,
                contentColor = colorScheme.onPrimary,
                disabledContainerColor = colorScheme.surfaceVariant,
                disabledContentColor = colorScheme.onSurfaceVariant
            )
        ) {
            if (isSaving) {
                CircularProgressIndicator(
                    modifier = Modifier.height(22.dp),
                    color = colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = "Save changes",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun EditProfileForm(
    uiState: EditProfileUiState,
    onFirstNameChanged: (String) -> Unit,
    onLastNameChanged: (String) -> Unit,
    onGradeSelected: (String) -> Unit,
    onBoardSelected: (String) -> Unit,
    onParentMobileChanged: (String) -> Unit,
    onParentEmailChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
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
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (uiState.saveError != null) {
            Text(
                text = uiState.saveError,
                style = MaterialTheme.typography.bodySmall,
                color = StatusError,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(StatusErrorContainer, RoundedCornerShape(12.dp))
                    .padding(12.dp)
            )
        }

        EditSection(title = "About you") {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = uiState.firstName,
                    onValueChange = onFirstNameChanged,
                    label = { Text("First name") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = EditFieldShape,
                    colors = fieldColors
                )
                OutlinedTextField(
                    value = uiState.lastName,
                    onValueChange = onLastNameChanged,
                    label = { Text("Last name") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = EditFieldShape,
                    colors = fieldColors
                )
            }
        }

        EditSection(title = "Academics") {
            Text(
                text = "Class",
                style = MaterialTheme.typography.labelLarge,
                color = colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
            EditChipGrid(
                chips = uiState.gradeOptions,
                onSelected = onGradeSelected,
                modifier = Modifier.padding(top = 8.dp)
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "Board",
                style = MaterialTheme.typography.labelLarge,
                color = colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
            EditChipGrid(
                chips = uiState.boardOptions,
                onSelected = onBoardSelected,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        EditSection(title = "Guardian contact") {
            OutlinedTextField(
                value = uiState.parentMobile,
                onValueChange = onParentMobileChanged,
                label = { Text("Mobile number") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = uiState.mobileError,
                supportingText = if (uiState.mobileError) {
                    {
                        Text(
                            text = "Enter exactly 10 digits",
                            color = StatusError,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                } else {
                    null
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                shape = EditFieldShape,
                colors = fieldColors
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = uiState.parentEmail,
                onValueChange = onParentEmailChanged,
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = uiState.emailError,
                supportingText = if (uiState.emailError) {
                    {
                        Text(
                            text = "Include @ in the email",
                            color = StatusError,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                } else {
                    null
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                shape = EditFieldShape,
                colors = fieldColors
            )
            EmailDomainChipRow(
                currentEmail = uiState.parentEmail,
                onEmailUpdated = onParentEmailChanged,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(72.dp))
    }
}

@Composable
private fun EditSection(
    title: String,
    content: @Composable () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colorScheme.surfaceVariant, EditCardShape)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold
        )
        content()
    }
}

@Composable
private fun EditChipGrid(
    chips: List<SelectableChip>,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val rows = chips.chunked(2)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        rows.forEach { rowChips ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                rowChips.forEach { chip ->
                    SelectablePillChip(
                        label = chip.label,
                        isSelected = chip.isSelected,
                        onClick = { onSelected(chip.id) },
                        modifier = Modifier.weight(1f)
                    )
                }
                if (rowChips.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}
