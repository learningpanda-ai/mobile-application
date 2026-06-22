package com.example.learningpandaai.features.dashboard.presentation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.learningpandaai.R
import com.example.learningpandaai.core.navigation.Screen
import com.example.learningpandaai.features.home.presentation.HomeScreen
import com.example.learningpandaai.features.progress.presentation.ProgressScreen
import com.example.learningpandaai.features.askpanda.presentation.AskPandaScreen
import com.example.learningpandaai.features.playzone.presentation.PlayZoneScreen
import com.example.learningpandaai.features.profile.presentation.ProfileScreen

/**
 * Shell for the main bottom-navigation experience.
 *
 * The Home tab hosts [HomeScreen], which loads profile-backed data via [HomeViewModel].
 */

/**
 * Nested Tab Item structure holding route indicators and dynamic visual resource IDs.
 */
sealed class TabItem(
    val screen: Screen,
    val outlinedIconResID: Int,
    val filledIconResId: Int,
    val title: String
) {
    object Home :
        TabItem(Screen.Home, R.drawable.ic_home_outlined, R.drawable.ic_home_filled, "Home")

    object Progress : TabItem(
        Screen.Progress,
        R.drawable.ic_progress_outlined,
        R.drawable.ic_progress_filled,
        "Progress"
    )

    object AskPanda : TabItem(
        Screen.AskPanda,
        R.drawable.ic_askpanda_outlined,
        R.drawable.ic_askpanda_filled,
        "Ask Panda"
    )

    object PlayZone : TabItem(
        Screen.PlayZone,
        R.drawable.ic_gamified_outlined,
        R.drawable.ic_gamified_filled,
        "Play Zone"
    )

    object Profile : TabItem(
        Screen.Profile,
        R.drawable.ic_profile_outlined,
        R.drawable.ic_profile_filled,
        "Profile"
    )
}

@Composable
fun DashboardScreen(
    parentNavController: NavHostController,
    dashboardViewModel: DashboardViewModel,
    firstName: String,
    selectedSubjects: Set<String>
) {
    val childNavController = rememberNavController()
    val navBackStackEntry by childNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    var profileRefreshTrigger by rememberSaveable { mutableStateOf(false) }
    val colorScheme = MaterialTheme.colorScheme

    LaunchedEffect(parentNavController) {
        parentNavController.currentBackStackEntry
            ?.savedStateHandle
            ?.getStateFlow("profile_updated", false)
            ?.collect { updated ->
                if (updated) {
                    profileRefreshTrigger = !profileRefreshTrigger
                    parentNavController.currentBackStackEntry
                        ?.savedStateHandle
                        ?.set("profile_updated", false)
                }
            }
    }

    val tabs = listOf(
        TabItem.Home,
        TabItem.Progress,
        TabItem.AskPanda,
        TabItem.PlayZone,
        TabItem.Profile
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = colorScheme.background,
        bottomBar = {
            PandaBottomBar(
                tabs = tabs,
                currentRoute = currentRoute,
                onTabSelected = { tab ->
                    if (tab.screen == Screen.Profile) {
                        profileRefreshTrigger = !profileRefreshTrigger
                    }
                    if (currentRoute != tab.screen.route) {
                        childNavController.navigate(tab.screen.route) {
                            popUpTo(childNavController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = childNavController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = Screen.Home.route) {
                HomeScreen(
                    viewModel = hiltViewModel(),
                    onStartLessonClick = {
                        navigateDashboardTab(childNavController, Screen.AskPanda.route)
                    },
                    onAskDoubtClick = {
                        navigateDashboardTab(childNavController, Screen.AskPanda.route)
                    },
                    onProgressClick = {
                        navigateDashboardTab(childNavController, Screen.Progress.route)
                    },
                    onPlayZoneClick = {
                        navigateDashboardTab(childNavController, Screen.PlayZone.route)
                    }
                )
            }
            composable(route = Screen.Progress.route) {
                ProgressScreen(viewModel = hiltViewModel())
            }

            composable(route = Screen.AskPanda.route) {
                AskPandaScreen(viewModel = hiltViewModel())
            }

            composable(route = Screen.PlayZone.route) {
                PlayZoneScreen(viewModel = hiltViewModel())
            }

            composable(route = Screen.Profile.route) {
                ProfileScreen(
                    viewModel = hiltViewModel(),
                    onNavigateToEdit = {
                        parentNavController.navigate(Screen.EditProfile.route)
                    },
                    onLogout = {
                        parentNavController.navigate(Screen.Auth.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onProfileDataSynced = dashboardViewModel::refreshShellFromCache,
                    refreshTrigger = profileRefreshTrigger
                )
            }
        }
    }
}

/**
 * Premium, flat bottom navigation.
 *  - A single hairline top border instead of tonal elevation / drop shadow.
 *  - The selected destination expands into a soft pill (CircleShape) that reveals
 *    its label; everything animates liquid-smooth via spring color transitions.
 */
@Composable
private fun PandaBottomBar(
    tabs: List<TabItem>,
    currentRoute: String?,
    onTabSelected: (TabItem) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Surface(color = colorScheme.surface) {
        Column(modifier = Modifier.fillMaxWidth()) {
            HorizontalDivider(
                thickness = 1.dp,
                color = colorScheme.outlineVariant.copy(alpha = 0.4f)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                tabs.forEach { tab ->
                    val isSelected = currentRoute == tab.screen.route
                    val weight by animateFloatAsState(
                        targetValue = if (isSelected) 1.5f else 1f,
                        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                        label = "navWeight"
                    )
                    PandaNavItem(
                        tab = tab,
                        selected = isSelected,
                        onClick = { onTabSelected(tab) },
                        modifier = Modifier.weight(weight)
                    )
                }
            }
        }
    }
}

@Composable
private fun PandaNavItem(
    tab: TabItem,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme

    val pillColor by animateColorAsState(
        targetValue = if (selected) colorScheme.primary.copy(alpha = 0.12f) else Color.Transparent,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "navPill"
    )
    val contentColor by animateColorAsState(
        targetValue = if (selected) colorScheme.primary else colorScheme.onSurfaceVariant,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "navContent"
    )
    val interaction = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .animateContentSize(
                animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
            )
            .clip(CircleShape)
            .background(pillColor)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 9.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.animateContentSize(
                animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val iconRes = if (selected) tab.filledIconResId else tab.outlinedIconResID
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = tab.title,
                modifier = Modifier.size(22.dp),
                tint = contentColor
            )
            if (selected) {
                Text(
                    text = tab.title,
                    style = MaterialTheme.typography.labelMedium,
                    color = contentColor,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
            }
        }
    }
}

private fun navigateDashboardTab(
    navController: NavHostController,
    route: String
) {
    navController.navigate(route) {
        popUpTo(navController.graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
fun PlaceholderTab(title: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$title Screen\n(Fetching live API records in sequence...)",
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
