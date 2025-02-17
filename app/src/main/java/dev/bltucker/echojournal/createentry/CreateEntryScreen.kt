package dev.bltucker.echojournal.createentry

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import dev.bltucker.echojournal.R
import dev.bltucker.echojournal.common.Mood
import dev.bltucker.echojournal.common.composables.AudioPlayer
import dev.bltucker.echojournal.common.theme.MoodColors
import dev.bltucker.echojournal.createentry.composables.EntryTitleField
import dev.bltucker.echojournal.createentry.composables.MoodIndicator
import dev.bltucker.echojournal.createentry.composables.MoodSelector
import kotlinx.serialization.Serializable

@Serializable
data class CreateEntryScreenNavArgs(
    val entryId: String
)


fun NavGraphBuilder.createEntryScreen(onNavigateBack: () -> Unit){
    composable<CreateEntryScreenNavArgs>{ backStackEntry ->
        val args = backStackEntry.toRoute<CreateEntryScreenNavArgs>()
        val viewModel = hiltViewModel<CreateEntryScreenViewModel>()
        val model by viewModel.observableModel.collectAsStateWithLifecycle()


        LifecycleStartEffect(Unit) {
            viewModel.onStart(args.entryId)
            onStopOrDispose {  }
        }



        CreateEntryScreen(
            modifier = Modifier.fillMaxSize(),
            model = model,
            onNavigateBack = onNavigateBack,

            onConfirmMoodSelected = viewModel::onConfirmMoodSelection,
            onDismissMoodSelector = viewModel::onDismissMoodSelector,
            onShowMoodSelector = viewModel::onShowMoodSelector,
            onEntryTitleChange = viewModel::onEntryTitleChange,
            onSave = viewModel::onSave,
            onClearSnackBarMessage = viewModel::onClearSnackbarMessage,
            onPlayPauseClick = viewModel::onPlayPauseClick,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateEntryScreen(
    modifier: Modifier = Modifier,
    model: CreateEntryScreenModel,
    onNavigateBack: () -> Unit,


    onConfirmMoodSelected: (Mood) -> Unit,
    onDismissMoodSelector: () -> Unit,

    onShowMoodSelector: () -> Unit,
    onEntryTitleChange: (String) -> Unit,

    onSave: () -> Unit,
    onClearSnackBarMessage: () -> Unit,

    onPlayPauseClick: () -> Unit,
    ) {

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(model.snackbarMessage){
        if(model.snackbarMessage != null){
            snackbarHostState.showSnackbar(
                message = model.snackbarMessage,
                duration = SnackbarDuration.Short
            )
            onClearSnackBarMessage()
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Entry Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            painter = painterResource(R.drawable.icon_navigate_before),
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)){

            MoodAndTitleRow(modifier = Modifier.fillMaxWidth(),
                entryTitle = model.title,
                selectedMood = model.selectedMood,
                onClickMoodIndicator = onShowMoodSelector,
                onTitleChange = onEntryTitleChange
            )

            Spacer(modifier = Modifier.height(8.dp))

            AudioPlayer(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                isPlaying = model.isPlaying,
                progress = model.playbackProgress,
                duration = model.audioDuration,
                mood = model.journalEntry?.mood ?: Mood.NEUTRAL,
                onPlayPauseClick = onPlayPauseClick,
                )

            Spacer(modifier = Modifier.weight(1f))

            SaveAndCancelRow(modifier = Modifier.fillMaxWidth(),
                onCancel = onNavigateBack,
                onSave = onSave,
            )

            if(model.isShowingMoodSelector){
                ModalBottomSheet(onDismissRequest = onDismissMoodSelector,
                    containerColor = MaterialTheme.colorScheme.surface) {
                    MoodSelector(
                        entryMood = model.selectedMood,
                        onConfirm = onConfirmMoodSelected,
                        onCancel = onDismissMoodSelector
                    )
                }
            }
        }
    }
}

@Composable
private fun SaveAndCancelRow(modifier: Modifier = Modifier,
                             onCancel: () -> Unit,
                             onSave: () -> Unit){
    Row(modifier = modifier.padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)){

        OutlinedButton(
            onClick = onCancel,
            modifier = Modifier.weight(1f)
        ) {
            Text("Cancel")
        }

        FilledTonalButton(
            modifier = Modifier.weight(1f),
            onClick = onSave,
        ) {
            Text("Save")
        }

    }
}

@Composable
private fun MoodAndTitleRow(modifier: Modifier = Modifier,
                            entryTitle: String,
                            selectedMood: Mood?,
                            onClickMoodIndicator: () -> Unit,
                            onTitleChange: (String) -> Unit,
                            ){
    Row(modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)){

        MoodIndicator(
            modifier = Modifier.padding(horizontal = 8.dp),
            mood = selectedMood,
            onClick = onClickMoodIndicator
        )

        EntryTitleField(
            modifier = Modifier.weight(1f),
            title = entryTitle,
            onTitleChange = onTitleChange
        )

    }
}


