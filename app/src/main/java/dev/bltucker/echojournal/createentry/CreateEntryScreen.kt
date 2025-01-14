package dev.bltucker.echojournal.createentry

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable

@Serializable
data class CreateEntryScreenNavArgs(
    val entryId: String
)


fun NavGraphBuilder.createEntryScreen(onNavigateBack: () -> Unit){
    composable<CreateEntryScreenNavArgs>{ backStackEntry ->
        val args = backStackEntry.toRoute<CreateEntryScreenNavArgs>()
        val viewModel = hiltViewModel<CreateEntryScreenViewModel>()

        val model = viewModel.observableModel.collectAsStateWithLifecycle()

        LifecycleStartEffect(Unit) {
            viewModel.onStart(args.entryId)
            onStopOrDispose {  }
        }

    }
}