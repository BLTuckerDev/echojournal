package dev.bltucker.echojournal

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import dev.bltucker.echojournal.settings.SETTINGS_SCREEN_ROUTE
import dev.bltucker.echojournal.settings.settingsScreen

@Composable
fun EchoJournalNavigationGraph(navController: NavHostController) {
    NavHost(navController = navController,
        startDestination = SETTINGS_SCREEN_ROUTE){


        settingsScreen()
    }
}