package dev.bltucker.echojournal.settings.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import dev.bltucker.echojournal.common.room.Topic
import dev.bltucker.echojournal.common.theme.EchoJournalTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopicsCard(
    modifier: Modifier = Modifier,
    isInEditMode: Boolean = false,
    editModeText: String = "",
    availableTopics: List<Topic> = emptyList(),
    defaultTopics: List<Topic> = emptyList(),
    onTopicDefaultToggled: (Topic) -> Unit = {},
    onAddTopicClick: () -> Unit = {},
    onUpdateEditModeText: (String) -> Unit = {},
) {

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "My Topics",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Select default topics to apply to all new entries",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))


            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(defaultTopics) { topic ->
                    TopicItem(
                        modifier = Modifier.height(32.dp),
                        topic = topic,
                        isDefaulted = topic in defaultTopics,
                        onToggleDefault = { onTopicDefaultToggled(topic) }
                    )
                }

                if (isInEditMode) {
                    item {
                        ExposedDropdownMenuBox(
                            expanded = editModeText.isNotEmpty(),
                            onExpandedChange = { },
                        ) {
                            BasicTextField(
                                value = editModeText,
                                onValueChange = { updatedValue: String ->
                                    onUpdateEditModeText(updatedValue)
                                },
                                modifier = Modifier
                                    .menuAnchor(type = MenuAnchorType.PrimaryEditable, true)
                                    .background(
                                        color = Color.White,
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .padding(horizontal = 4.dp, vertical = 0.dp),
                                textStyle = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurface
                                ),
                                singleLine = true,
                            )

                            ExposedDropdownMenu(
                                expanded = editModeText.isNotEmpty(),
                                onDismissRequest = { },
                            ) {
                                availableTopics
                                    .filter { it.name.startsWith(editModeText, ignoreCase = true) }
                                    .forEach { topic ->
                                        DropdownMenuItem(
                                            text = { Text(topic.name, maxLines = 1) },
                                            onClick = {
                                                onUpdateEditModeText(topic.name)
                                            }
                                        )
                                    }
                            }
                        }
                    }
                } else {
                    item {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    shape = CircleShape
                                )
                                .clickable(
                                    onClick = onAddTopicClick,
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add topic",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MyTopicsCardPreview() {
    EchoJournalTheme {
        MyTopicsCard(
            availableTopics = listOf(
                Topic(name = "Work"),
                Topic(name = "Family"),
                Topic(name = "Health")
            ),
            defaultTopics = listOf(
                Topic(name = "Work"),
                Topic(name = "Family")
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EmptyMyTopicsCardPreview() {
    EchoJournalTheme {
        MyTopicsCard()
    }
}

@Preview(showBackground = true)
@Composable
fun EditModeMyTopicsCardPreview() {
    EchoJournalTheme {
        Column(modifier = Modifier.fillMaxSize()){
            MyTopicsCard(
                isInEditMode = true,
                editModeText = "St",
                availableTopics = listOf(Topic(name = "Stuff"), Topic(name = "Stuff 2"), Topic(name = "Stuff 3"))
            )
        }
    }
}