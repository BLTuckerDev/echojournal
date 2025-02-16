package dev.bltucker.echojournal.createentry.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.bltucker.echojournal.R
import dev.bltucker.echojournal.common.Mood
import dev.bltucker.echojournal.common.theme.EchoJournalTheme
import dev.bltucker.echojournal.common.theme.MoodColors

@Composable
fun MoodSelector(
    modifier: Modifier = Modifier,
    entryMood: Mood? = null,
    onConfirm: (Mood) -> Unit = {},
    onCancel: () -> Unit = {}
) {

    var selectedMood by remember { mutableStateOf(entryMood) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "How are you doing?",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Mood.entries.forEach { mood ->
                MoodOption(
                    mood = mood,
                    isSelected = mood == selectedMood,
                    onSelect = { selectedMood = it}
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancel")
            }

            FilledTonalButton(
                onClick = {
                    val safeMood = selectedMood
                    safeMood?.let {
                        onConfirm(safeMood)
                    }
                },
                modifier = Modifier.weight(1f),
                enabled = selectedMood != null
            ) {
                Text("Confirm")
            }
        }
    }
}

@Composable
private fun MoodOption(
    modifier: Modifier = Modifier,
    mood: Mood,
    isSelected: Boolean,
    onSelect: (Mood) -> Unit
) {
    val moodColor = when (mood) {
        Mood.STRESSED -> MoodColors.Stressed80
        Mood.SAD -> MoodColors.Sad80
        Mood.NEUTRAL -> MoodColors.Neutral90
        Mood.PEACEFUL -> MoodColors.Peaceful80
        Mood.EXCITED -> MoodColors.Excited80
    }

    Column(
        modifier = modifier.clickable { onSelect(mood) },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            modifier = Modifier.size(32.dp),
            painter = painterResource(
                when (mood) {
                    Mood.STRESSED -> R.drawable.mood_stressed
                    Mood.SAD -> R.drawable.mood_sad
                    Mood.NEUTRAL -> R.drawable.mood_neutral
                    Mood.PEACEFUL -> R.drawable.mood_peaceful
                    Mood.EXCITED -> R.drawable.mood_excited
                }
            ),
            contentDescription = mood.name.lowercase(),
            tint = if (isSelected) moodColor else MaterialTheme.colorScheme.onSurfaceVariant,
        )


        Text(
            text = mood.name.lowercase(),
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) moodColor
            else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MoodSelectorPreview() {
    EchoJournalTheme {
        MoodSelector(
            entryMood = Mood.PEACEFUL,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MoodSelectorEmptyPreview() {
    EchoJournalTheme {
        MoodSelector()
    }
}