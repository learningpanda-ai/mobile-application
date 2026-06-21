package com.example.learningpandaai.features.onboarding.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.learningpandaai.features.onboarding.presentation.components.OnboardingDimens
import com.example.learningpandaai.features.onboarding.presentation.components.OnboardingScreenCardShape
import com.example.learningpandaai.features.onboarding.presentation.steps.Step1SelfInfo
import com.example.learningpandaai.features.onboarding.presentation.steps.Step2Academics
import com.example.learningpandaai.features.onboarding.presentation.steps.Step3Subjects
import com.example.learningpandaai.features.onboarding.presentation.steps.Step4Mindset
import com.example.learningpandaai.features.onboarding.presentation.steps.SuccessGateway

/**
 * The high-level orchestrator of the Personalization Wizard.
 * Bridges visual user events from our stateless step composables to our [OnboardingViewModel].
 */
@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel,
    onNavigateToDashboard: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val colorScheme = MaterialTheme.colorScheme

    // Listen for final completion to route to the dashboard home shell
    LaunchedEffect(key1 = uiState.isComplete) {
        if (uiState.isComplete) {
            onNavigateToDashboard()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = colorScheme.background,
        topBar = {
            if (uiState.currentStep <= 4) {
                WizardHeader(
                    currentStep = uiState.currentStep,
                    onBackClick = { viewModel.previousStep() }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = OnboardingScreenCardShape,
                    colors = CardDefaults.cardColors(
                        containerColor = colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = OnboardingDimens.ScreenCardElevation
                    )
                ) {
                    Column(modifier = Modifier.padding(OnboardingDimens.CardPadding)) {
                        // Dynamically loads and bridges the stateless step files to ViewModel methods
                        when (uiState.currentStep) {
                            1 -> Step1SelfInfo(
                                uiState = uiState,
                                viewModel = viewModel
                            )

                            2 -> Step2Academics(
                                uiState = uiState,
                                onClassSelected = { viewModel.onClassSelected(it) },
                                onBoardSelected = { viewModel.onBoardSelected(it) }
                            )

                            3 -> Step3Subjects(
                                uiState = uiState,
                                onSubjectToggled = { viewModel.onSubjectToggled(it) }
                            )

                            4 -> Step4Mindset(
                                uiState = uiState,
                                onFavoriteSubjectSelected = { viewModel.onFavoriteSubjectSelected(it) },
                                onStudiesFeelingSelected = { viewModel.onStudiesFeelingSelected(it) },
                                onCareerIdeaSelected = { viewModel.onCareerIdeaSelected(it) },
                                onDiscoverStrengthsSelected = { viewModel.onDiscoverStrengthsSelected(it) }
                            )

                            5 -> SuccessGateway(
                                uiState = uiState,
                                onGoToDashboard = { viewModel.goToDashboard() }
                            )
                        }
                    }
                }

                // Breathing padding to prevent card contents overlapping our bottom buttons
                Spacer(modifier = Modifier.height(100.dp))
            }

            // Bottom-aligned Floating Capsule Navigation Buttons
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 24.dp, start = 20.dp, end = 20.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                AnimatedVisibility(
                    visible = uiState.currentStep in 1..3,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Button(
                        onClick = { viewModel.nextStep() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .shadow(6.dp, CircleShape, clip = false),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorScheme.primary,
                            contentColor = colorScheme.onPrimary,
                            disabledContainerColor = colorScheme.surfaceVariant,
                            disabledContentColor = colorScheme.onSurfaceVariant
                        ),
                        shape = CircleShape,
                        enabled = !uiState.isLoading && uiState.canContinueCurrentStep
                    ) {
                        Text(
                            text = "Continue",
                            style = MaterialTheme.typography.titleSmall,
                            color = colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = colorScheme.onPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                AnimatedVisibility(
                    visible = uiState.currentStep == 4,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Button(
                        onClick = { viewModel.completeSetup() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .shadow(6.dp, CircleShape, clip = false),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorScheme.primary,
                            contentColor = colorScheme.onPrimary,
                            disabledContainerColor = colorScheme.surfaceVariant,
                            disabledContentColor = colorScheme.onSurfaceVariant
                        ),
                        shape = CircleShape,
                        enabled = !uiState.isLoading && uiState.canContinueCurrentStep
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = colorScheme.onPrimary
                            )
                        } else {
                            Text(
                                text = "Build My Learning Path",
                                style = MaterialTheme.typography.titleSmall,
                                color = colorScheme.onPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = colorScheme.onPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WizardHeader(
    currentStep: Int,
    onBackClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val animatedProgress by animateFloatAsState(
        targetValue = currentStep / 4f,
        animationSpec = tween(durationMillis = 520),
        label = "wizardProgress"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colorScheme.background)
            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (currentStep > 1) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        tint = colorScheme.onBackground
                    )
                }
            } else {
                Spacer(modifier = Modifier.width(48.dp)) // Alignment spacer when back is invisible
            }

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = when (currentStep) {
                        1 -> "Setup Profile"
                        2 -> "Select Academics"
                        3 -> "Choose Subjects"
                        else -> "Personalize Learning"
                    },
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Step $currentStep of 4",
                    style = MaterialTheme.typography.labelMedium,
                    color = colorScheme.secondary,
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.width(48.dp))
        }

        Spacer(modifier = Modifier.height(12.dp))

        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(CircleShape),
            color = colorScheme.secondary,
            trackColor = colorScheme.outline.copy(alpha = 0.45f),
            strokeCap = StrokeCap.Round
        )

        Spacer(modifier = Modifier.height(4.dp))
    }
}
