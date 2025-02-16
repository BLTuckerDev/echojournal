package dev.bltucker.echojournal.createentry

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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

    ) {
    Scaffold(
        modifier = modifier,
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


