package dev.bltucker.echojournal.settings.composables

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import dev.bltucker.echojournal.common.composables.TopicItem
import dev.bltucker.echojournal.common.room.Topic
import dev.bltucker.echojournal.common.theme.EchoJournalTheme

@Preview(name = "Topic Item - Selected")
@Composable
private fun TopicItemSelectedPreview() {
    EchoJournalTheme {
        TopicItem(
            topic = Topic(
                id = "1",
                name = "Work",
                isDefault = true
            ),
            isDefaulted = true,
            onToggleDefault = {}
        )
    }
}

@Preview(name = "Topic Item - Not Selected")
@Composable
private fun TopicItemNotSelectedPreview() {
    EchoJournalTheme {
        TopicItem(
            topic = Topic(
                id = "2",
                name = "Concentration",
                isDefault = false
            ),
            isDefaulted = false,
            onToggleDefault = {}
        )
    }
}