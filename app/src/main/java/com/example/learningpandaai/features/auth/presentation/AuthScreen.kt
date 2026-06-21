package com.example.learningpandaai.features.auth.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.learningpandaai.BuildConfig
import com.example.learningpandaai.R
import com.example.learningpandaai.core.designsystem.components.EmailDomainChipRow
import com.example.learningpandaai.core.designsystem.components.appOutlinedTextFieldColors
import com.example.learningpandaai.core.designsystem.theme.BorderDefault
import com.example.learningpandaai.core.designsystem.theme.BorderSubtle
import com.example.learningpandaai.core.designsystem.theme.BrandPrimary
import com.example.learningpandaai.core.designsystem.theme.BrandSecondary
import com.example.learningpandaai.core.designsystem.theme.PureWhite
import com.example.learningpandaai.core.designsystem.theme.StatusError
import com.example.learningpandaai.core.designsystem.theme.StatusErrorContainer
import com.example.learningpandaai.core.designsystem.theme.SurfaceBackgroundDark
import com.example.learningpandaai.core.designsystem.theme.TextSecondary
import com.example.learningpandaai.core.designsystem.theme.TextTertiary
import com.example.learningpandaai.core.util.InputValidation
import com.example.learningpandaai.core.util.Logger
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch

private val PillShape = CircleShape
private val SheetShape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp)
private val StudentEmailDomains = listOf(
    "@gmail.com",
    "@outlook.com",
    "@university.edu",
    "@students.ac.in"
)

@Composable
fun AuthScreen(
    viewModel: AuthViewModel,
    onNavigateToOnboarding: () -> Unit,
    onNavigateToDashboard: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showEmailForm by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                AuthUiEvent.NavigateToDashboard -> onNavigateToDashboard()
                AuthUiEvent.NavigateToOnboarding -> onNavigateToOnboarding()
            }
        }
    }

    AuthContent(
        uiState = uiState,
        showEmailForm = showEmailForm,
        onShowEmailForm = {
            showEmailForm = true
            viewModel.onShowEmailForm()
        },
        onDismissEmailForm = {
            showEmailForm = false
            viewModel.onDismissEmailForm()
        },
        onEmailChange = viewModel::onEmailChanged,
        onOtpChange = viewModel::onOtpChanged,
        onChangeEmail = viewModel::onChangeEmail,
        onResendOtp = viewModel::resendOtpCode,
        onSubmit = {
            if (uiState.isOtpSent) {
                viewModel.verifyOtpAndLogin()
            } else {
                viewModel.sendOtpCode()
            }
        },
        onGoogleSubmit = {
            if (uiState.isLoading) return@AuthContent
            scope.launch {
                try {
                    val googleIdOption = GetGoogleIdOption.Builder()
                        .setFilterByAuthorizedAccounts(false)
                        .setServerClientId(BuildConfig.GOOGLE_WEB_CLIENT_ID)
                        .build()
                    val request = GetCredentialRequest.Builder()
                        .addCredentialOption(googleIdOption)
                        .build()
                    val result = CredentialManager.create(context).getCredential(context, request)
                    val credential = result.credential
                    if (credential is GoogleIdTokenCredential) {
                        viewModel.authenticateWithGoogle(credential.idToken)
                    } else {
                        viewModel.onGoogleError("Unsupported credential type.")
                    }
                } catch (_: GetCredentialCancellationException) {
                    Logger.d("Google sign-in: user cancelled")
                } catch (_: NoCredentialException) {
                    viewModel.onGoogleError("No Google account found on this device.")
                } catch (e: GetCredentialException) {
                    Logger.e("Google sign-in: credential error — ${e.message}", e)
                    viewModel.onGoogleError("Google sign-in failed. Please try again.")
                }
            }
        }
    )
}

@Composable
fun AuthContent(
    uiState: AuthUiState,
    showEmailForm: Boolean,
    onShowEmailForm: () -> Unit,
    onDismissEmailForm: () -> Unit,
    onEmailChange: (String) -> Unit,
    onOtpChange: (String) -> Unit,
    onChangeEmail: () -> Unit,
    onResendOtp: () -> Unit,
    onSubmit: () -> Unit,
    onGoogleSubmit: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceBackgroundDark)
    ) {
        AuthHeroSection(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.35f)
                .clip(SheetShape)
                .background(colorScheme.surface)
        ) {
            Box(
                modifier = Modifier
                    .padding(top = 14.dp)
                    .align(Alignment.TopCenter)
                    .width(42.dp)
                    .height(5.dp)
                    .clip(PillShape)
                    .background(BorderSubtle)
            )

            AnimatedContent(
                targetState = showEmailForm,
                transitionSpec = {
                    if (targetState) {
                        slideInHorizontally(tween(320)) { it } + fadeIn(tween(280)) togetherWith
                            slideOutHorizontally(tween(280)) { -it / 3 } + fadeOut(tween(200))
                    } else {
                        slideInHorizontally(tween(320)) { -it } + fadeIn(tween(280)) togetherWith
                            slideOutHorizontally(tween(280)) { it / 3 } + fadeOut(tween(200))
                    }
                },
                label = "auth_sheet_content",
                modifier = Modifier.fillMaxSize()
            ) { isEmailForm ->
                if (isEmailForm) {
                    AuthEmailForm(
                        uiState = uiState,
                        onDismiss = {
                            focusManager.clearFocus()
                            onDismissEmailForm()
                        },
                        onEmailChange = onEmailChange,
                        onOtpChange = onOtpChange,
                        onChangeEmail = onChangeEmail,
                        onResendOtp = onResendOtp,
                        onSubmit = {
                            focusManager.clearFocus()
                            onSubmit()
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    AuthEntryPortal(
                        uiState = uiState,
                        onGoogleSubmit = onGoogleSubmit,
                        onShowEmailForm = onShowEmailForm,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            if (uiState.isLoading && !showEmailForm) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(colorScheme.surface.copy(alpha = 0.65f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = BrandPrimary,
                        strokeWidth = 2.5.dp,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AuthHeroSection(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier.size(108.dp),
            shape = CircleShape,
            color = PureWhite,
            shadowElevation = 12.dp
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_panda_logo),
                contentDescription = "Learning Panda logo",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }

        Spacer(modifier = Modifier.height(22.dp))

        Text(
            text = "Learn smarter with Panda.",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 24.sp,
                lineHeight = 30.sp
            ),
            color = PureWhite,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Instant help for homework, exams, and tough concepts — just ask.",
            style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
            color = TextTertiary,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun AuthEntryPortal(
    uiState: AuthUiState,
    onGoogleSubmit: () -> Unit,
    onShowEmailForm: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Welcome",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.SemiBold),
                color = colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Pick how you'd like to sign in — it only takes a moment.",
                style = MaterialTheme.typography.bodyMedium,
                color = colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }

        AnimatedVisibility(
            visible = uiState.errorMessage != null,
            enter = fadeIn(tween(250)),
            exit = fadeOut(tween(200))
        ) {
            uiState.errorMessage?.let { message ->
                AuthErrorBanner(message = message)
            }
        }

        AuthGoogleButton(
            enabled = !uiState.isLoading,
            isLoading = uiState.isLoading,
            onClick = onGoogleSubmit
        )

        AuthOrDivider()

        AuthEmailEntryButton(onClick = onShowEmailForm)

        Text(
            text = "By continuing, you agree to our Terms of Service and Privacy Policy.",
            style = MaterialTheme.typography.labelSmall,
            color = colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
private fun AuthEmailForm(
    uiState: AuthUiState,
    onDismiss: () -> Unit,
    onEmailChange: (String) -> Unit,
    onOtpChange: (String) -> Unit,
    onChangeEmail: () -> Unit,
    onResendOtp: () -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TextButton(
            onClick = onDismiss,
            modifier = Modifier.align(Alignment.Start)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back to sign-in options",
                tint = TextSecondary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Back",
                color = TextSecondary,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = if (uiState.isOtpSent) "Check your inbox" else "Sign in with email",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                color = colorScheme.onSurface
            )
            Text(
                text = if (uiState.isOtpSent) {
                    "We sent a 6-digit code to ${maskEmailForDisplay(uiState.emailInput)}"
                } else {
                    "No password needed — we'll email you a one-time code."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = colorScheme.onSurfaceVariant
            )
        }

        AnimatedVisibility(
            visible = uiState.errorMessage != null,
            enter = fadeIn(tween(250)),
            exit = fadeOut(tween(200))
        ) {
            uiState.errorMessage?.let { message ->
                AuthErrorBanner(message = message)
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            OutlinedTextField(
                value = uiState.emailInput,
                onValueChange = onEmailChange,
                label = {
                    Text(
                        text = "School or personal email",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isOtpSent && !uiState.isLoading,
                isError = uiState.emailError,
                supportingText = if (uiState.emailError) {
                    {
                        Text(
                            text = "Enter a valid email (include @ and domain)",
                            style = MaterialTheme.typography.bodySmall,
                            color = StatusError
                        )
                    }
                } else {
                    null
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = if (uiState.isOtpSent) ImeAction.Done else ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.clearFocus() },
                    onDone = { focusManager.clearFocus() }
                ),
                shape = PillShape,
                singleLine = true,
                colors = appOutlinedTextFieldColors()
            )

            if (!uiState.isOtpSent) {
                EmailDomainChipRow(
                    currentEmail = uiState.emailInput,
                    domains = StudentEmailDomains,
                    onEmailUpdated = onEmailChange,
                    modifier = Modifier.padding(top = 8.dp)
                )
            } else {
                TextButton(
                    onClick = onChangeEmail,
                    enabled = !uiState.isLoading,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .offset(y = (-6).dp),
                    contentPadding = PaddingValues(horizontal = 0.dp, vertical = 0.dp)
                ) {
                    Text(
                        text = "Use a different email",
                        style = MaterialTheme.typography.labelMedium,
                        color = BrandPrimary
                    )
                }
            }
        }

        AnimatedVisibility(visible = uiState.isOtpSent) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = uiState.otpInput,
                    onValueChange = onOtpChange,
                    label = {
                        Text(
                            text = "6-digit code",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    placeholder = {
                        Text(
                            text = "000000",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextTertiary
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isLoading,
                    isError = uiState.otpError,
                    supportingText = if (uiState.otpError) {
                        {
                            Text(
                                text = "Enter all ${InputValidation.MIN_OTP_LENGTH} digits",
                                style = MaterialTheme.typography.bodySmall,
                                color = StatusError
                            )
                        }
                    } else {
                        null
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.NumberPassword,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            if (uiState.canSubmit && !uiState.isLoading) onSubmit()
                        }
                    ),
                    shape = PillShape,
                    singleLine = true,
                    colors = appOutlinedTextFieldColors()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    val resendEnabled = uiState.canResendOtp
                    TextButton(
                        onClick = onResendOtp,
                        enabled = resendEnabled,
                        contentPadding = PaddingValues(horizontal = 0.dp, vertical = 0.dp)
                    ) {
                        Text(
                            text = when {
                                uiState.isLoading -> "Sending…"
                                uiState.resendCooldownSeconds > 0 ->
                                    "Resend in ${uiState.resendCooldownSeconds}s"
                                else -> "Resend code"
                            },
                            style = MaterialTheme.typography.labelLarge,
                            color = if (resendEnabled) BrandPrimary else colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Button(
            onClick = onSubmit,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = PillShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = BrandPrimary,
                contentColor = PureWhite,
                disabledContainerColor = colorScheme.surfaceVariant,
                disabledContentColor = colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            ),
            enabled = !uiState.isLoading && uiState.canSubmit
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    color = PureWhite,
                    strokeWidth = 2.4.dp
                )
            } else {
                Text(
                    text = if (uiState.isOtpSent) "Verify & continue" else "Send code",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = PureWhite
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun AuthGoogleButton(
    enabled: Boolean,
    isLoading: Boolean,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Surface(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = PillShape,
        color = colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.5.dp, BorderDefault),
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = colorScheme.onSurface
                )
            } else {
                GoogleGlyph(tint = colorScheme.onSurface, modifier = Modifier.size(19.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Continue with Google",
                    style = MaterialTheme.typography.titleSmall,
                    color = colorScheme.onSurface,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun AuthEmailEntryButton(onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = PillShape,
        color = BrandPrimary
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Email,
                contentDescription = null,
                tint = PureWhite
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Continue with email",
                style = MaterialTheme.typography.titleSmall,
                color = PureWhite,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun AuthOrDivider() {
    val colorScheme = MaterialTheme.colorScheme
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f), color = BorderSubtle)
        Text(
            text = "or",
            style = MaterialTheme.typography.labelMedium,
            color = colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 12.dp)
        )
        HorizontalDivider(modifier = Modifier.weight(1f), color = BorderSubtle)
    }
}

@Composable
private fun AuthErrorBanner(message: String) {
    Text(
        text = message,
        color = StatusError,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(StatusErrorContainer)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        maxLines = 3,
        overflow = TextOverflow.Ellipsis
    )
}

private fun maskEmailForDisplay(email: String): String {
    val trimmed = email.trim()
    val at = trimmed.indexOf('@')
    if (at <= 1) return trimmed
    val local = trimmed.substring(0, at)
    val domain = trimmed.substring(at)
    val visible = local.take(2)
    return "$visible•••$domain"
}

@Composable
private fun GoogleGlyph(tint: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val sw = size.minDimension * 0.13f
        val inset = sw / 2f
        drawArc(
            color = tint,
            startAngle = -25f,
            sweepAngle = 295f,
            useCenter = false,
            topLeft = Offset(inset, inset),
            size = Size(size.width - sw, size.height - sw),
            style = Stroke(width = sw, cap = StrokeCap.Round)
        )
        drawLine(
            color = tint,
            start = Offset(size.width / 2f, size.height / 2f),
            end = Offset(size.width - inset, size.height / 2f),
            strokeWidth = sw,
            cap = StrokeCap.Round
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AuthEntryPortalPreview() {
    com.example.learningpandaai.core.designsystem.theme.LearningPandaAITheme {
        AuthContent(
            uiState = AuthUiState(),
            showEmailForm = false,
            onShowEmailForm = {},
            onDismissEmailForm = {},
            onEmailChange = {},
            onOtpChange = {},
            onChangeEmail = {},
            onResendOtp = {},
            onSubmit = {},
            onGoogleSubmit = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AuthOtpVerifyFormPreview() {
    com.example.learningpandaai.core.designsystem.theme.LearningPandaAITheme {
        AuthContent(
            uiState = AuthUiState(
                emailInput = "student@learningpanda.com",
                otpInput = "12",
                isOtpSent = true,
                otpError = true,
                resendCooldownSeconds = 18,
                errorMessage = "That code didn't work. Check your email and try again."
            ),
            showEmailForm = true,
            onShowEmailForm = {},
            onDismissEmailForm = {},
            onEmailChange = {},
            onOtpChange = {},
            onChangeEmail = {},
            onResendOtp = {},
            onSubmit = {},
            onGoogleSubmit = {}
        )
    }
}
