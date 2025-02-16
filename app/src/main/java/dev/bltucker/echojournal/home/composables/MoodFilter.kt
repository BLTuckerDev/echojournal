package dev.bltucker.echojournal.home.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.bltucker.echojournal.R
import dev.bltucker.echojournal.common.Mood
import dev.bltucker.echojournal.common.theme.EchoJournalTheme
import dev.bltucker.echojournal.common.theme.MoodColors

@Composable
fun MoodFilterButton(
    modifier: Modifier = Modifier,
    selectedMoods: Set<Mood>,
    onClick: () -> Unit,
    onClickClearFilter: () -> Unit,
) {
    if (selectedMoods.isEmpty()) {
        OutlinedButton(modifier = modifier, onClick = onClick) {
            Text(text = "All Moods", color = Color.Black)
        }
    } else {
        OutlinedButton(modifier = modifier,
            contentPadding = PaddingValues(0.dp),
            onClick = onClick) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                selectedMoods.forEach { mood ->
                    Icon(
                        painter = painterResource(getMoodIcon(mood)),
                        contentDescription = mood.name.lowercase(),
                        modifier = Modifier.size(16.dp),
                        tint = getMoodColor(mood)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Icon(
                    modifier = Modifier.size(16.dp).clickable { onClickClearFilter() },
                    painter = painterResource(R.drawable.icon_close),
                    contentDescription = "Clear mood filter",
                    tint = Color.Black,
                )
            }
        }

    }
}

@Composable
fun MoodFilterDropdown(
    modifier: Modifier = Modifier,
    selectedMoods: Set<Mood>,
    onMoodSelected: (Mood) -> Unit,
    expanded: Boolean,
    onDismiss: () -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
        modifier = modifier.background(MaterialTheme.colorScheme.surface)
    ) {
        Mood.entries.forEach { mood ->
            DropdownMenuItem(
                text = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(getMoodIcon(mood)),
                                contentDescription = null,
                                tint = getMoodColor(mood)
                            )
                            Text(
                                text = mood.name.lowercase().replaceFirstChar { it.titlecase() },
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                        if (mood in selectedMoods) {
                            Icon(
                                painter = painterResource(R.drawable.icon_check),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                onClick = { onMoodSelected(mood) },
                modifier = Modifier.fillMaxWidth()
            )
        }
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

private fun getMoodColor(mood: Mood): Color {
    return when (mood) {
        Mood.STRESSED -> MoodColors.Stressed80
        Mood.SAD -> MoodColors.Sad80
        Mood.NEUTRAL -> MoodColors.Neutral90
        Mood.PEACEFUL -> MoodColors.Peaceful80
        Mood.EXCITED -> MoodColors.Excited80
    }
}

@Preview(showBackground = true)
@Composable
private fun MoodFilterButtonPreview() {
    EchoJournalTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Empty state
            MoodFilterButton(
                modifier = Modifier.wrapContentWidth(),
                selectedMoods = emptySet(),
                onClick = {},
                onClickClearFilter = {},
            )

            // Single mood selected
            MoodFilterButton(
                modifier = Modifier.wrapContentWidth(),
                selectedMoods = setOf(Mood.PEACEFUL),
                onClick = {},
                onClickClearFilter = {},
            )

            // Multiple moods selected
            MoodFilterButton(
                selectedMoods = setOf(Mood.SAD, Mood.NEUTRAL),
                onClick = {},
                onClickClearFilter = {},
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MoodFilterDropdownPreview() {
    EchoJournalTheme {
        Surface {
            MoodFilterDropdown(
                selectedMoods = setOf(Mood.SAD, Mood.NEUTRAL),
                onMoodSelected = {},
                expanded = true,
                onDismiss = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MoodFilterCompletePreview() {
    EchoJournalTheme {
        var showMoodFilter by remember { mutableStateOf(false) }
        var selectedMoods by remember {
            mutableStateOf<Set<Mood>>(
                setOf(
                    Mood.SAD,
                    Mood.NEUTRAL,
                    Mood.EXCITED
                )
            )
        }

        Box(modifier = Modifier.padding(16.dp)) {
            MoodFilterButton(
                selectedMoods = selectedMoods,
                onClick = { showMoodFilter = true },
                onClickClearFilter = { selectedMoods = emptySet() }
            )

            MoodFilterDropdown(
                selectedMoods = selectedMoods,
                onMoodSelected = { mood ->
                    selectedMoods = if (mood in selectedMoods) {
                        selectedMoods - mood
                    } else {
                        selectedMoods + mood
                    }
                },
                expanded = showMoodFilter,
                onDismiss = { showMoodFilter = false }
            )
        }
    }
}