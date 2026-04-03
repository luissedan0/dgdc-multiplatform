package com.luissedan0.demetergarterdistanceclub

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.luissedan0.demetergarterdistanceclub.state.AppTab
import com.luissedan0.demetergarterdistanceclub.state.TrackerAppState
import com.luissedan0.demetergarterdistanceclub.storage.TrackerStorage
import com.luissedan0.demetergarterdistanceclub.ui.components.TrackerNavigationBar
import com.luissedan0.demetergarterdistanceclub.ui.screens.HomeScreen
import com.luissedan0.demetergarterdistanceclub.ui.screens.LoginScreen
import com.luissedan0.demetergarterdistanceclub.ui.screens.ProfileScreen
import com.luissedan0.demetergarterdistanceclub.ui.theme.DemeterTrackerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun App() {
    DemeterTrackerTheme {
        val appState = remember { TrackerAppState(TrackerStorage()) }

        if (appState.showDeleteDataConfirmation) {
            AlertDialog(
                onDismissRequest = appState::dismissDeleteDataConfirmation,
                title = { Text("Delete saved data") },
                text = { Text("Are you sure you want to delete all saved mileage, tracked days, and goal progress?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            appState.confirmDeleteAllData()
                        }
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(onClick = appState::dismissDeleteDataConfirmation) {
                        Text("Cancel")
                    }
                }
            )
        }

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            if (!appState.isLoggedIn) {
                LoginScreen(
                    howlerName = appState.loginDraft,
                    goalDraft = appState.loginGoalDraft,
                    onHowlerNameChange = appState::updateLoginDraft,
                    onGoalDraftChange = appState::updateLoginGoalDraft,
                    onLoginClick = appState::login
                )
            } else {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            title = {
                                Text(
                                    when (appState.currentTab) {
                                        AppTab.HOME -> "Mileage Tracker"
                                        AppTab.PROFILE -> "Profile"
                                    }
                                )
                            }
                        )
                    },
                    bottomBar = {
                        TrackerNavigationBar(
                            currentTab = appState.currentTab,
                            onTabSelected = appState::selectTab
                        )
                    }
                ) { paddingValues ->
                    when (appState.currentTab) {
                        AppTab.HOME -> HomeScreen(
                            paddingValues = paddingValues,
                            weeks = appState.weekBlocks,
                            onMilesChange = appState::updateMiles
                        )

                        AppTab.PROFILE -> ProfileScreen(
                            paddingValues = paddingValues,
                            howlerName = appState.activeHowlerName.orEmpty(),
                            goalDraft = appState.goalDraft,
                            goalMiles = appState.goalMiles,
                            remainingMiles = appState.remainingMiles,
                            completionPercent = appState.completionPercent,
                            totalLoggedMiles = appState.totalMileage,
                            showTestCrashButton = supportsTestCrash(),
                            onGoalDraftChange = appState::updateGoalDraft,
                            onSetGoalClick = appState::saveGoal,
                            onTestCrashClick = ::triggerTestCrash,
                            onDeleteSavedDataClick = appState::requestDeleteAllData,
                            onLogoutClick = appState::logout
                        )
                    }
                }
            }
        }
    }
}
