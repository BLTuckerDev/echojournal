package dev.bltucker.echojournal.settings.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.bltucker.echojournal.R
import dev.bltucker.echojournal.common.Mood
import dev.bltucker.echojournal.common.theme.EchoJournalTheme
import dev.bltucker.echojournal.common.theme.MoodColors

@Composable
fun DefaultMoodCard(
    modifier: Modifier = Modifier,
    selectedMood: Mood? = null,
    onMoodSelected: (Mood) -> Unit = {}
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "My Mood",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "Select default mood to apply to all new entries",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Mood.entries.forEach { mood ->
                    MoodButton(
                        mood = mood,
                        isSelected = mood == selectedMood,
                        onClick = { onMoodSelected(mood) }
                    )
                }
            }
        }
    }
}

@Composable
private fun MoodButton(
    modifier: Modifier = Modifier,
    mood: Mood,
    isSelected: Boolean = false,
    onClick: () -> Unit = {}
) {
    val moodColor = when (mood) {
        Mood.STRESSED -> MoodColors.Stressed80
        Mood.SAD -> MoodColors.Sad80
        Mood.NEUTRAL -> MoodColors.Neutral90
        Mood.PEACEFUL -> MoodColors.Peaceful80
        Mood.EXCITED -> MoodColors.Excited80
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        OutlinedButton(
            onClick = onClick,
            modifier = Modifier.size(40.dp),
            shape = CircleShape,
            contentPadding = PaddingValues(0.dp),
            border = if (isSelected) {
                BorderStroke(2.dp, moodColor)
            } else {
                BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            },
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = if (isSelected) moodColor.copy(alpha = 0.1f) else Color.Transparent
            )
        ) {
            Icon(
                painter = painterResource(id = getMoodIcon(mood)),
                contentDescription = mood.name.lowercase(),
                tint = if (isSelected) moodColor else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Text(
            text = mood.name.lowercase().replaceFirstChar { it.titlecase() },
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) moodColor else MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

private fun getMoodIcon(mood: Mood): Int {
    return when (mood) {
        Mood.STRESSED -> R.drawable.mood_stressed
        Mood.SAD -> R.drawable.mood_sad
        Mood.NEUTRAL -> R.drawable.mood_neutral
        Mood.PEACEFUL -> R.drawable.mood_peaceful
        Mood.EXCITED -> R.drawable.mood_excited
    }
}

@Preview
@Composable
fun DefaultMoodCardPreview() {
    var selectedMood by remember { mutableStateOf<Mood?>(Mood.NEUTRAL) }

    EchoJournalTheme {
        Surface {
            DefaultMoodCard(
                modifier = Modifier.padding(16.dp),
                selectedMood = selectedMood,
                onMoodSelected = { selectedMood = it }
            )
        }
    }
}