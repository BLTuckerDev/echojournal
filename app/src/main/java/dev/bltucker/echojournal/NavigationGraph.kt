package dev.bltucker.echojournal

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import dev.bltucker.echojournal.home.HOME_SCREEN_ROUTE
import dev.bltucker.echojournal.home.homeScreen
import dev.bltucker.echojournal.settings.SETTINGS_SCREEN_ROUTE
import dev.bltucker.echojournal.settings.settingsScreen

@Composable
fun EchoJournalNavigationGraph(navController: NavHostController) {
    NavHost(navController = navController,
        startDestination = HOME_SCREEN_ROUTE){

        homeScreen(
            onNavigateToSettings = {
                navController.navigate(SETTINGS_SCREEN_ROUTE)
            }
        )

        settingsScreen(onNavigateBack = {
            navController.popBackStack()
        })
    }
}