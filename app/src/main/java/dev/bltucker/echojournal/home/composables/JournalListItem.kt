package dev.bltucker.echojournal.home.composables

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import dev.bltucker.echojournal.R
import dev.bltucker.echojournal.common.Mood
import dev.bltucker.echojournal.common.room.Topic
import dev.bltucker.echojournal.common.theme.EchoJournalTheme
import dev.bltucker.echojournal.common.theme.MoodColors
import dev.bltucker.echojournal.home.JournalEntryCardState

@Composable
fun JournalListSection(
    modifier: Modifier = Modifier,
    headerText: String,
    items: List<JournalEntryCardState>,
    onPlayPauseClick: (String) -> Unit = {},
    onShowMoreClick: (String) -> Unit = {},
    onTopicClick: (String) -> Unit = {}
) {
    Column(modifier = modifier) {
        JournalListHeader(text = headerText)

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.Transparent
        ) {
            Box {
                // Timeline line
                val lineColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)
                Canvas(
                    modifier = Modifier
                        .width(1.dp)
                        .fillMaxHeight()
                        .align(Alignment.CenterStart)
                        .offset(x = 31.dp) // Centers line with mood icons
                ) {
                    drawLine(
                        color = lineColor,
                        start = Offset(0f, 0f),
                        end = Offset(0f, size.height),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                // List items
                Column {
                    items.forEachIndexed { index, item ->
                        JournalListItem(
                            entry = item,
                            isFirstInSection = index == 0,
                            isLastInSection = index == items.size - 1,
                            onPlayPauseClick = {  },
                            onShowMoreClick = {  },
                            onTopicClick = onTopicClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun JournalListItem(
    modifier: Modifier = Modifier,
    entry: JournalEntryCardState,
    isFirstInSection: Boolean = false,
    isLastInSection: Boolean = false,
    onPlayPauseClick: () -> Unit = {},
    onShowMoreClick: () -> Unit = {},
    onTopicClick: (String) -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        val moodColor = when (entry.mood) {
            Mood.STRESSED -> MoodColors.Stressed80
            Mood.SAD -> MoodColors.Sad80
            Mood.NEUTRAL -> MoodColors.Neutral90
            Mood.PEACEFUL -> MoodColors.Peaceful80
            Mood.EXCITED -> MoodColors.Excited80
        }

        Surface(
            modifier = Modifier
                .size(32.dp)
                .zIndex(1f), // Ensures icon appears above the line
            shape = CircleShape,
            color = moodColor.copy(alpha = 0.2f)
        ) {
            Icon(
                painter = painterResource(
                    when (entry.mood) {
                        Mood.STRESSED -> R.drawable.mood_stressed
                        Mood.SAD -> R.drawable.mood_sad
                        Mood.NEUTRAL -> R.drawable.mood_neutral
                        Mood.PEACEFUL -> R.drawable.mood_peaceful
                        Mood.EXCITED -> R.drawable.mood_excited
                    }
                ),
                contentDescription = "Mood: ${entry.mood.name.lowercase()}",
                tint = moodColor,
                modifier = Modifier
                    .padding(8.dp)
                    .size(24.dp)
            )
        }

        JournalEntryCard(
            modifier = Modifier.weight(1f),
            state = entry,
            onPlayPauseClick = onPlayPauseClick,
            onShowMoreClick = onShowMoreClick,
            onTopicClick = onTopicClick
        )
    }
}

@Composable
fun JournalListHeader(
    modifier: Modifier = Modifier,
    text: String
) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 24.dp, bottom = 16.dp)
    )
}

@Preview(showBackground = true)
@Composable
private fun JournalListSectionPreview() {
    EchoJournalTheme {
        JournalListSection(
            headerText = "TODAY",
            items = listOf(
                JournalEntryCardState(
                    title = "My Entry",
                    time = "17:30",
                    mood = Mood.PEACEFUL,
                    audioDuration = "7:05/12:30",
                    isPlaying = true,
                    description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
                    topics = listOf(
                        Topic(name = "Work"),
                        Topic(name = "Conundrums")
                    )
                ),
                JournalEntryCardState(
                    title = "Another Entry",
                    time = "15:45",
                    mood = Mood.EXCITED,
                    audioDuration = "0:00/3:45",
                    isPlaying = false
                ),
                JournalEntryCardState(
                    title = "Last Entry",
                    time = "12:30",
                    mood = Mood.SAD,
                    audioDuration = "0:00/5:20",
                    isPlaying = false
                )
            )
        )
    }
}