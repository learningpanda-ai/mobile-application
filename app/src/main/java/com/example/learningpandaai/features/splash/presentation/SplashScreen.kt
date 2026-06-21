package com.example.learningpandaai.features.splash.presentation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.learningpandaai.R
import com.example.learningpandaai.core.designsystem.theme.BorderDefault
import com.example.learningpandaai.core.designsystem.theme.BorderSubtle
import com.example.learningpandaai.core.designsystem.theme.BrandSecondary
import com.example.learningpandaai.core.designsystem.theme.LearningPandaAITheme
import com.example.learningpandaai.core.designsystem.theme.TextSecondary
import com.example.learningpandaai.core.designsystem.theme.TextTertiary
import com.example.learningpandaai.core.navigation.Screen

private const val SPLASH_FADE_DURATION_MS = 900
private const val SPLASH_MIN_DISPLAY_MS = 2_000
private const val SPLASH_PROGRESS_DURATION_MS = 1_800

@Composable
fun SplashScreen(
    viewModel: SplashViewModel,
    onNavigate: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var animationFinished by remember { mutableStateOf(false) }
    var pendingRoute by remember { mutableStateOf<String?>(null) }
    var hasNavigated by remember { mutableStateOf(false) }

    val contentAlpha = remember { Animatable(0f) }
    val contentScale = remember { Animatable(0.88f) }
    val loadingProgress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        contentAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = SPLASH_FADE_DURATION_MS,
                easing = FastOutSlowInEasing
            )
        )
    }

    LaunchedEffect(Unit) {
        contentScale.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = SPLASH_FADE_DURATION_MS,
                easing = FastOutSlowInEasing
            )
        )
    }

    LaunchedEffect(Unit) {
        loadingProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = SPLASH_PROGRESS_DURATION_MS,
                easing = FastOutSlowInEasing
            )
        )
    }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(SPLASH_MIN_DISPLAY_MS.toLong())
        animationFinished = true
    }

    LaunchedEffect(uiState) {
        pendingRoute = when (uiState) {
            SplashUiState.Loading -> null
            SplashUiState.NavigateToAuth -> Screen.Auth.route
            SplashUiState.NavigateToOnboarding -> Screen.Onboarding.route
            SplashUiState.NavigateToDashboard -> Screen.Dashboard.route
        }
    }

    LaunchedEffect(pendingRoute, animationFinished) {
        val route = pendingRoute
        if (animationFinished && route != null && !hasNavigated) {
            hasNavigated = true
            onNavigate(route)
        }
    }

    SplashContent(
        contentAlpha = contentAlpha.value,
        contentScale = contentScale.value,
        loadingProgress = loadingProgress.value
    )
}

@Composable
fun SplashContent(
    contentAlpha: Float,
    contentScale: Float,
    loadingProgress: Float
) {
    val colorScheme = MaterialTheme.colorScheme

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 40.dp)
                .graphicsLayer {
                    alpha = contentAlpha
                    scaleX = contentScale
                    scaleY = contentScale
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LogoCluster()

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = "YOUR AI STUDY COMPANION",
                style = MaterialTheme.typography.labelSmall,
                color = BrandSecondary,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 3.5.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Learning Panda AI",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(9.dp))

            Text(
                text = "Waking up your AI study buddy…",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(48.dp)
                .graphicsLayer { alpha = contentAlpha }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "PREPARING PERSONALIZED LEARNING",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextTertiary,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "${(loadingProgress.coerceIn(0f, 1f) * 100).toInt()}%",
                    style = MaterialTheme.typography.labelMedium,
                    color = BrandSecondary,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    softWrap = false
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            FlatProgressBar(
                progress = loadingProgress.coerceIn(0f, 1f),
                fill = BrandSecondary,
                track = BorderSubtle
            )
        }
    }
}

@Composable
private fun LogoCluster() {
    val colorScheme = MaterialTheme.colorScheme

    val transition = rememberInfiniteTransition(label = "breathe")
    val logoPulse by transition.animateFloat(
        initialValue = 0.97f,
        targetValue = 1.035f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1_900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logoPulse"
    )

    Box(contentAlignment = Alignment.Center) {
//        Surface(
//            modifier = Modifier
//                .size(168.dp)
//                .border(width = 1.5.dp, color = BorderDefault, shape = CircleShape),
//            shape = CircleShape,
//            color = Color.Transparent
//        ) {}
        Surface(
            modifier = Modifier
                .size(150.dp)
                .border(width = 1.dp, color = BorderDefault, shape = CircleShape),
            shape = CircleShape,
            color = Color.Transparent
        ) {}

        Surface(
            modifier = Modifier.size(132.dp),
            shape = CircleShape,
            color = colorScheme.surface,
            tonalElevation = 2.dp,
            shadowElevation = 14.dp
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = logoPulse
                        scaleY = logoPulse
                    },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_panda_logo),
                    contentDescription = "Learning Panda AI logo",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@Composable
private fun FlatProgressBar(
    progress: Float,
    fill: Color,
    track: Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(5.dp)
            .clip(CircleShape)
            .background(track)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress)
                .height(5.dp)
                .clip(CircleShape)
                .background(fill)
        )
    }
}

@Preview(showBackground = true, name = "Splash — Light")
@Composable
private fun SplashScreenLightPreview() {
    LearningPandaAITheme(darkTheme = false) {
        SplashContent(contentAlpha = 1f, contentScale = 1f, loadingProgress = 0.55f)
    }
}

@Preview(showBackground = true, name = "Splash — Dark")
@Composable
private fun SplashScreenDarkPreview() {
    LearningPandaAITheme(darkTheme = true) {
        SplashContent(contentAlpha = 1f, contentScale = 1f, loadingProgress = 0.55f)
    }
}
