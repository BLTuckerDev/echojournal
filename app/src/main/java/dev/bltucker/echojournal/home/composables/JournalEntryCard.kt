package dev.bltucker.echojournal.home.composables

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.bltucker.echojournal.R
import dev.bltucker.echojournal.common.Mood
import dev.bltucker.echojournal.common.room.Topic
import dev.bltucker.echojournal.common.theme.EchoJournalTheme
import dev.bltucker.echojournal.common.theme.MoodColors
import dev.bltucker.echojournal.home.JournalEntryCardState
import kotlinx.coroutines.delay
import kotlin.random.Random


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
private fun AudioPlayer(
    modifier: Modifier = Modifier,
    isPlaying: Boolean,
    progress: Float,
    duration: String,
    mood: Mood,
    onPlayPauseClick: () -> Unit
) {
    val moodColor = when (mood) {
        Mood.STRESSED -> MoodColors.Stressed80
        Mood.SAD -> MoodColors.Sad80
        Mood.NEUTRAL -> MoodColors.Neutral90
        Mood.PEACEFUL -> MoodColors.Peaceful80
        Mood.EXCITED -> MoodColors.Excited80
    }

    Row(
        modifier = modifier
            .background(
                color = moodColor.copy(alpha = 0.2f),
                shape = RoundedCornerShape(100.dp)
            )
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Surface(
            modifier = Modifier.size(32.dp),
            shape = CircleShape,
            color = Color.White,
            tonalElevation = 4.dp,
            onClick = onPlayPauseClick
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(
                        if (isPlaying) R.drawable.icon_pause
                        else R.drawable.icon_play_arrow_filled
                    ),
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = moodColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .height(24.dp)
        ) {
            AudioWaveform(
                isPlaying = isPlaying,
                moodColor = moodColor
            )
        }

        Text(
            text = duration,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}


@Composable
private fun AudioWaveform(
    modifier: Modifier = Modifier,
    isPlaying: Boolean,
    moodColor: Color,
    barCount: Int = 20
) {
    val baseHeights = remember {
        List(barCount) { 0.3f + (Random.nextFloat() * 0.4f) }
    }

    val animatedHeights = List(barCount) { index ->
        val baseHeight = baseHeights[index]
        val animatedHeight by animateFloatAsState(
            targetValue = if (isPlaying) {
                baseHeight + (Random.nextFloat() * 0.3f)
            } else {
                baseHeight
            },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
        animatedHeight
    }

    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            while (true) {
                delay(150)
                baseHeights.forEach { _ -> Random.nextFloat() }
            }
        }
    }

    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        animatedHeights.forEach { height ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(height)
                    .padding(horizontal = 1.dp)
                    .background(
                        color = moodColor.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(4.dp)
                    )
            )
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