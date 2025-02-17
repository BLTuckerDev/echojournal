package dev.bltucker.echojournal.home.composables

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.bltucker.echojournal.common.Mood
import dev.bltucker.echojournal.common.composables.AudioPlayer
import dev.bltucker.echojournal.common.room.Topic
import dev.bltucker.echojournal.common.theme.EchoJournalTheme
import dev.bltucker.echojournal.home.JournalEntryCardState


@Composable
fun JournalEntryCard(
    modifier: Modifier = Modifier,
    state: JournalEntryCardState,
    onPlayPauseClick: (String) -> Unit = {},
    onShowMoreClick: () -> Unit = {},
    onTopicClick: (String) -> Unit = {}
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = state.title,
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = state.time,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }


            AudioPlayer(
                isPlaying = state.isPlaying,
                progress = state.audioProgress,
                duration = state.audioDuration,
                mood = state.mood,
                onPlayPauseClick = { onPlayPauseClick(state.id) }
            )

            if (!state.description.isNullOrEmpty()) {
                Text(
                    text = state.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = if (state.isDescriptionExpanded) Int.MAX_VALUE else 3,
                    overflow = TextOverflow.Ellipsis
                )

                if (!state.isDescriptionExpanded) {
                    TextButton(
                        onClick = onShowMoreClick,
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = "Show more",
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            if (state.topics.isNotEmpty()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    state.topics.forEach { topic ->
                        TopicChip(
                            name = topic.name,
                            onClick = { onTopicClick(topic.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TopicChip(
    modifier: Modifier = Modifier,
    name: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(100.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "#",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = name,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun JournalEntryCardBasicPreview() {
    EchoJournalTheme {
        JournalEntryCard(
            modifier = Modifier.padding(16.dp),
            state = JournalEntryCardState(
                id = "fake",
                title = "My Entry",
                time = "17:30",
                mood = Mood.NEUTRAL,
                audioDuration = "0:00/12:30",
                isPlaying = false
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun JournalEntryCardWithDescriptionPreview() {
    EchoJournalTheme {
        JournalEntryCard(
            modifier = Modifier.padding(16.dp),
            state = JournalEntryCardState(
                id = "fake",
                title = "My Entry",
                time = "17:30",
                mood = Mood.PEACEFUL,
                audioDuration = "7:05/12:30",
                isPlaying = true,
                description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod ut amet, consectetur adipiscing elit."
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun JournalEntryCardWithTopicsPreview() {
    EchoJournalTheme {
        JournalEntryCard(
            modifier = Modifier.padding(16.dp),
            state = JournalEntryCardState(
                id = "fake",
                title = "My Entry",
                time = "17:30",
                mood = Mood.SAD,
                audioDuration = "0:00/12:30",
                isPlaying = false,
                description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
                topics = listOf(
                    Topic(name = "Work"),
                    Topic(name = "Conundrums")
                )
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun JournalEntryCardExpandedDescriptionPreview() {
    EchoJournalTheme {
        JournalEntryCard(
            modifier = Modifier.padding(16.dp),
            state = JournalEntryCardState(
                id = "fake",
                title = "My Entry",
                time = "17:30",
                mood = Mood.EXCITED,
                audioDuration = "0:00/12:30",
                isPlaying = false,
                description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, " +
                        "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. " +
                        "Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris " +
                        "nisi ut aliquip ex ea commodo consequat.",
                isDescriptionExpanded = true,
                topics = listOf(
                    Topic(name = "Work"),
                    Topic(name = "Conundrums")
                )
            )
        )
    }
}