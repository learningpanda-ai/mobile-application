package com.example.learningpandaai.features.home.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.learningpandaai.core.designsystem.components.DashboardErrorFallback
import com.example.learningpandaai.core.designsystem.components.DashboardPullToRefresh
import com.example.learningpandaai.core.designsystem.components.PandaButton
import com.example.learningpandaai.core.designsystem.components.PandaButtonVariant
import com.example.learningpandaai.core.designsystem.components.PandaCard
import com.example.learningpandaai.core.designsystem.components.UserAvatar
import com.example.learningpandaai.core.designsystem.components.shimmerPlaceholder
import com.example.learningpandaai.core.designsystem.theme.AppTheme
import com.example.learningpandaai.core.designsystem.theme.BrandPrimary
import com.example.learningpandaai.core.designsystem.theme.BrandPrimaryGradientEnd
import com.example.learningpandaai.core.designsystem.theme.BrandSecondary
import com.example.learningpandaai.core.designsystem.theme.LearningPandaAITheme
import com.example.learningpandaai.core.designsystem.theme.PureWhite
import com.example.learningpandaai.core.designsystem.theme.TextOnSecondaryContainer
import com.example.learningpandaai.core.designsystem.theme.ShapeCard
import com.example.learningpandaai.core.designsystem.theme.ShapePill
import com.example.learningpandaai.core.designsystem.theme.ShapeWell
import com.example.learningpandaai.core.designsystem.theme.Spacing

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onSeeAllClick: () -> Unit = {},
    onSubjectClick: (ActiveSubject) -> Unit = {},
    onResumeModuleClick: (ActiveSubject) -> Unit = {},
    onStartLessonClick: () -> Unit = {},
    onAskDoubtClick: () -> Unit = {},
    onProgressClick: () -> Unit = {},
    onPlayZoneClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()

    HomeScreenContent(
        uiState = uiState,
        isRefreshing = isRefreshing,
        onRefresh = viewModel::refresh,
        onStartLessonClick = onStartLessonClick,
        onAskDoubtClick = onAskDoubtClick,
        onRetry = viewModel::retry
    )
}

@Composable
private fun HomeScreenContent(
    uiState: HomeUiState,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onStartLessonClick: () -> Unit,
    onRetry: () -> Unit,
    onAskDoubtClick: () -> Unit = {}
) {
    when (uiState) {
        is HomeUiState.Loading -> HomeLoadingSkeleton()
        is HomeUiState.Error -> HomeErrorState(
            title = uiState.title,
            message = uiState.message,
            retryLabel = uiState.retryLabel,
            onRetry = onRetry
        )
        is HomeUiState.Empty -> HomeEmptyState(
            title = uiState.title,
            subtitle = uiState.subtitle
        )
        is HomeUiState.Success -> DashboardPullToRefresh(
            isRefreshing = isRefreshing,
            onRefresh = onRefresh
        ) {
            HomeSuccessState(
                state = uiState,
                onStartLessonClick = onStartLessonClick,
                onAskDoubtClick = onAskDoubtClick
            )
        }
    }
}

private val HomeContentModifier = Modifier.padding(
    start = Spacing.screen,
    end = Spacing.screen,
    top = Spacing.md,
    bottom = Spacing.xxl
)

@Composable
private fun HomeSuccessState(
    state: HomeUiState.Success,
    onStartLessonClick: () -> Unit,
    onAskDoubtClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .verticalScroll(rememberScrollState())
            .then(HomeContentModifier)
    ) {
        HomeHeader(labels = state.labels, userStats = state.userStats)

        Spacer(modifier = Modifier.height(Spacing.section))

        TodaysFocusSection(
            sectionEyebrow = state.labels.todaysFocusSectionTitle,
            focus = state.todaysFocus,
            startLessonLabel = state.labels.startLessonAction,
            onStartLessonClick = onStartLessonClick
        )

        Spacer(modifier = Modifier.height(Spacing.section))

        AskADoubtCard(onClick = onAskDoubtClick)

        Spacer(modifier = Modifier.height(Spacing.section))

        WeeklyGoalCard(
            goal = state.weeklyGoal,
            progressLabel = state.labels.weeklyGoalProgressLabel
        )
    }
}

@Composable
private fun HomeHeader(labels: HomeScreenLabels, userStats: UserStats) {
    val colorScheme = MaterialTheme.colorScheme

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = labels.greetingPrefix,
                style = MaterialTheme.typography.labelLarge,
                color = colorScheme.secondary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = userStats.name,
                style = MaterialTheme.typography.headlineMedium,
                color = colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Spacer(modifier = Modifier.width(Spacing.md))
        HomeAvatar(userStats = userStats)
    }
}

@Composable
private fun HomeAvatar(userStats: UserStats) {
    UserAvatar(
        imageUrl = userStats.avatarUrl,
        displayName = userStats.name,
        size = 52.dp,
        plan = userStats.plan,
        contentDescription = "Your profile photo"
    )
}

@Composable
private fun AskADoubtCard(onClick: () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    val gradient = remember {
        Brush.linearGradient(colors = listOf(BrandPrimary, BrandPrimaryGradientEnd))
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(ShapeCard)
            .background(gradient)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .padding(Spacing.lg)
    ) {
        SectionEyebrow(
            text = "Instant help",
            color = PureWhite.copy(alpha = 0.88f)
        )
        Spacer(modifier = Modifier.height(Spacing.md))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.lg)
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(ShapeWell)
                    .background(PureWhite.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.AutoAwesome,
                    contentDescription = null,
                    tint = PureWhite,
                    modifier = Modifier.size(26.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Ask a Doubt",
                    style = MaterialTheme.typography.titleMedium,
                    color = PureWhite,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Stuck? Panda explains it step by step.",
                    style = MaterialTheme.typography.bodySmall,
                    color = PureWhite.copy(alpha = 0.92f),
                    lineHeight = 16.sp
                )
            }
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(PureWhite.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = PureWhite,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun SectionEyebrow(
    text: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = color,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.8.sp,
        modifier = modifier
    )
}

@Composable
private fun TodaysFocusSection(
    sectionEyebrow: String,
    focus: FocusTask,
    startLessonLabel: String,
    onStartLessonClick: () -> Unit
) {
    TodaysFocusCard(
        sectionEyebrow = sectionEyebrow,
        focus = focus,
        startLessonLabel = startLessonLabel,
        onStartLessonClick = onStartLessonClick
    )
}

@Composable
private fun TodaysFocusCard(
    sectionEyebrow: String,
    focus: FocusTask,
    startLessonLabel: String,
    onStartLessonClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    PandaCard(
        modifier = Modifier.fillMaxWidth(),
        containerColor = colorScheme.secondaryContainer,
        border = null,
        contentPadding = PaddingValues(Spacing.lg)
    ) {
        SectionEyebrow(
            text = sectionEyebrow,
            color = colorScheme.secondary
        )
        Spacer(modifier = Modifier.height(Spacing.md))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .clip(ShapePill)
                    .background(PureWhite.copy(alpha = 0.55f))
                    .padding(horizontal = Spacing.md, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.AutoAwesome,
                    contentDescription = null,
                    tint = BrandSecondary,
                    modifier = Modifier.size(13.dp)
                )
                Text(
                    text = focus.badgeText,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextOnSecondaryContainer,
                    fontWeight = FontWeight.Bold
                )
            }
            Icon(
                imageVector = Icons.Filled.BookmarkBorder,
                contentDescription = null,
                tint = colorScheme.onSecondaryContainer.copy(alpha = 0.55f),
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(Spacing.md))
        Text(
            text = focus.title,
            style = MaterialTheme.typography.titleMedium,
            color = colorScheme.onSecondaryContainer,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = focus.description,
            style = MaterialTheme.typography.bodyMedium,
            color = colorScheme.onSecondaryContainer.copy(alpha = 0.78f),
            lineHeight = 20.sp
        )
        Spacer(modifier = Modifier.height(Spacing.lg))
        PandaButton(
            text = startLessonLabel,
            onClick = onStartLessonClick,
            leadingIcon = Icons.Filled.PlayArrow,
            variant = PandaButtonVariant.Dark
        )
    }
}

@Composable
private fun WeeklyGoalCard(goal: WeeklyGoal, progressLabel: String) {
    val appColors = AppTheme.colors
    val progress = if (goal.target > 0) {
        (goal.current.toFloat() / goal.target.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }

    PandaCard(
        modifier = Modifier.fillMaxWidth(),
        containerColor = appColors.amberContainer,
        border = null,
        contentPadding = PaddingValues(Spacing.lg)
    ) {
        SectionEyebrow(
            text = "Weekly challenge",
            color = appColors.amber
        )
        Spacer(modifier = Modifier.height(Spacing.md))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(ShapeWell)
                    .background(PureWhite.copy(alpha = 0.55f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.EmojiEvents,
                    contentDescription = null,
                    tint = appColors.amber,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(Spacing.md))
            Text(
                text = goal.title,
                style = MaterialTheme.typography.titleMedium,
                color = appColors.onAmberContainer,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(Spacing.md))
        Text(
            text = goal.description,
            style = MaterialTheme.typography.bodySmall,
            color = appColors.onAmberContainer.copy(alpha = 0.78f),
            lineHeight = 18.sp
        )
        Spacer(modifier = Modifier.height(Spacing.lg))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = progressLabel,
                style = MaterialTheme.typography.labelMedium,
                color = appColors.onAmberContainer.copy(alpha = 0.78f)
            )
            Text(
                text = "${goal.current}/${goal.target}",
                style = MaterialTheme.typography.labelMedium,
                color = appColors.mint,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(Spacing.sm))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(CircleShape),
            color = appColors.mint,
            trackColor = PureWhite.copy(alpha = 0.55f),
            strokeCap = StrokeCap.Round
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Loading / Error / Empty states
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun HomeLoadingSkeleton() {
    val colorScheme = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .verticalScroll(rememberScrollState())
            .then(HomeContentModifier)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .width(110.dp)
                        .height(14.dp)
                        .shimmerPlaceholder(RoundedCornerShape(6.dp))
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .width(160.dp)
                        .height(28.dp)
                        .shimmerPlaceholder(RoundedCornerShape(8.dp))
                )
            }
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .shimmerPlaceholder(CircleShape)
            )
        }
        Spacer(modifier = Modifier.height(Spacing.section))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            repeat(3) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(104.dp)
                        .shimmerPlaceholder(ShapeCard)
                )
            }
        }
        Spacer(modifier = Modifier.height(Spacing.section))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(190.dp)
                .shimmerPlaceholder(ShapeCard)
        )
        Spacer(modifier = Modifier.height(Spacing.section))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(88.dp)
                .shimmerPlaceholder(ShapeCard)
        )
        Spacer(modifier = Modifier.height(Spacing.section))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .shimmerPlaceholder(ShapeCard)
        )
    }
}

@Composable
private fun HomeErrorState(
    title: String,
    message: String,
    retryLabel: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        DashboardErrorFallback(
            title = title,
            message = message,
            retryLabel = retryLabel,
            onRetry = onRetry
        )
    }
}

@Composable
private fun HomeEmptyState(title: String, subtitle: String) {
    val colorScheme = MaterialTheme.colorScheme

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .padding(Spacing.xxxl),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(CircleShape)
                    .background(colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.AutoAwesome,
                    contentDescription = null,
                    tint = colorScheme.primary,
                    modifier = Modifier.size(40.dp)
                )
            }
            Spacer(modifier = Modifier.height(Spacing.xl))
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(Spacing.sm))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HomeScreenSuccessPreview() {
    LearningPandaAITheme {
        HomeScreenContent(
            uiState = previewSuccessState(),
            isRefreshing = false,
            onRefresh = {},
            onStartLessonClick = {},
            onRetry = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Home — Dark")
@Composable
private fun HomeScreenSuccessDarkPreview() {
    LearningPandaAITheme(darkTheme = true) {
        HomeScreenContent(
            uiState = previewSuccessState(),
            isRefreshing = false,
            onRefresh = {},
            onStartLessonClick = {},
            onRetry = {}
        )
    }
}

private fun previewSuccessState(): HomeUiState.Success = HomeUiState.Success(
    labels = HomeScreenLabels(
        greetingPrefix = "Good morning",
        activeSubjectsSectionTitle = "Continue Learning",
        seeAllAction = "See all",
        todaysFocusSectionTitle = "Today's Focus",
        resumeModuleAction = "Resume",
        startLessonAction = "Start Lesson",
        weeklyGoalProgressLabel = "Weekly progress",
        streakLabel = "12 Day Streak",
        xpLabel = "8450 XP",
        emptyTitle = "",
        emptySubtitle = "",
        loadingMessage = "",
        retryAction = "Retry"
    ),
    userStats = UserStats(
        name = "Alok",
        streakDays = 12,
        xp = 8450,
        avatarUrl = null,
        isPro = true
    ),
    activeSubjects = emptyList(),
    todaysFocus = FocusTask(
        title = "Mastering Quadratic Equations",
        description = "Finish Module 2 to keep your streak alive.",
        badgeText = "AI Recommended"
    ),
    weeklyGoal = WeeklyGoal(
        title = "Weekly Goal",
        description = "Complete 5 modules to unlock the Scholar Badge.",
        current = 3,
        target = 5
    )
)
