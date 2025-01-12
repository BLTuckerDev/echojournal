package dev.bltucker.echojournal.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import dev.bltucker.echojournal.R
import dev.bltucker.echojournal.common.Mood
import dev.bltucker.echojournal.common.room.Topic
import dev.bltucker.echojournal.common.theme.EchoJournalTheme
import dev.bltucker.echojournal.settings.composables.DefaultMoodCard
import dev.bltucker.echojournal.settings.composables.MyTopicsCard

const val SETTINGS_SCREEN_ROUTE = "settings"

fun NavGraphBuilder.settingsScreen() {
    composable(route = SETTINGS_SCREEN_ROUTE) {
        val viewModel = hiltViewModel<SettingsViewModel>()
        val model by viewModel.observableModel.collectAsStateWithLifecycle()

        LifecycleStartEffect(Unit) {
            viewModel.onStart()
            onStopOrDispose { }
        }

        SettingsScreen(
            modifier = Modifier.fillMaxSize(),
            model = model,
            onDefaultMoodSelected = viewModel::onUpdateDefaultMood,
            onToggleDefaultTopic = viewModel::onToggleDefaultTopic,
            onUpdateEditModeText = viewModel::onUpdateEditTopicText,
            onClearEditTopicMode = viewModel::onClearEditTopicMode,
            onAddTopicClick = viewModel::onAddTopicClick,
            onCreateTopicClick = viewModel::onCreateTopicClick
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsScreen(
    modifier: Modifier = Modifier,
    model: SettingsModel,
    onDefaultMoodSelected: (Mood) -> Unit,
    onToggleDefaultTopic: (Topic) -> Unit,
    onUpdateEditModeText: (String) -> Unit = {},
    onClearEditTopicMode: () -> Unit = {},
    onAddTopicClick: () -> Unit = {},
    onCreateTopicClick: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    Icon(
                        painter = painterResource(R.drawable.icon_navigate_before),
                        contentDescription = "Back"
                    )
                }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            DefaultMoodCard(
                modifier = Modifier.fillMaxWidth(),
                selectedMood = model.defaultMood,
                onMoodSelected = onDefaultMoodSelected
            )

            MyTopicsCard(
                modifier = Modifier.fillMaxWidth(),
                isInEditMode = model.isInTopicEditMode,
                editModeText = model.editModeText,
                availableTopics = model.availableTopics,
                defaultTopics = model.defaultTopics,
                onTopicDefaultToggled = onToggleDefaultTopic,
                onUpdateEditModeText = onUpdateEditModeText,
                onDismissTopicDropDown = onClearEditTopicMode,
                onAddTopicClick = onAddTopicClick,
                onCreateTopic = onCreateTopicClick,
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    EchoJournalTheme {
        val previewModel = SettingsModel(
            defaultMood = Mood.PEACEFUL,
            availableTopics = listOf(
                Topic("1", "Work"),
                Topic("2", "Concentration", isDefault = true),
                Topic("3", "Family"),
                Topic("4", "Love", isDefault = true)
            ),
        )

        SettingsScreen(
            modifier = Modifier.fillMaxSize(),
            model = previewModel,
            onDefaultMoodSelected = {},
            onToggleDefaultTopic = {}
        )
    }
}

@Preview(showBackground = true, name = "Settings Screen - Empty State")
@Composable
private fun SettingsScreenEmptyPreview() {
    EchoJournalTheme {
        val emptyModel = SettingsModel()

        SettingsScreen(
            modifier = Modifier.fillMaxSize(),
            model = emptyModel,
            onDefaultMoodSelected = {},
            onToggleDefaultTopic = {}
        )
    }
}