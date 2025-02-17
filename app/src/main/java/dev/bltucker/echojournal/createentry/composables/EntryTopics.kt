package dev.bltucker.echojournal.createentry.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.bltucker.echojournal.common.room.Topic
import dev.bltucker.echojournal.common.theme.EchoJournalTheme
import dev.bltucker.echojournal.common.composables.TopicItem

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EntryTopics(
    modifier: Modifier = Modifier,
    isInEditMode: Boolean = false,
    editModeText: String = "",
    availableTopics: List<Topic> = emptyList(),
    selectedTopics: List<Topic> = emptyList(),

    onTopicDefaultToggled: (Topic) -> Unit = {},
    onAddTopicClick: () -> Unit = {},
    onUpdateTopicSearchQuery: (String) -> Unit = {},
    onCreateTopic: () -> Unit = {},
) {

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(isInEditMode) {
        if (isInEditMode) {
            focusRequester.requestFocus()
        }
    }
    ElevatedCard(
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
                text = "Entry Topics",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Select topics to apply to this entry",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))


            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                selectedTopics.forEach { topic ->
                    TopicItem(
                        modifier = Modifier.height(32.dp),
                        topic = topic,
                        isDefaulted = topic in selectedTopics,
                        onToggleDefault = { onTopicDefaultToggled(topic) }
                    )
                }

                if (isInEditMode) {
                    ExposedDropdownMenuBox(
                        expanded = isInEditMode,
                        onExpandedChange = { },
                    ) {
                        BasicTextField(
                            value = editModeText,
                            onValueChange = { updatedValue: String ->
                                onUpdateTopicSearchQuery(updatedValue)
                            },
                            modifier = Modifier
                                .height(32.dp)
                                .menuAnchor(type = MenuAnchorType.PrimaryEditable, true)
                                .background(
                                    color = Color.White,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .padding(horizontal = 12.dp)
                                .focusRequester(focusRequester),
                            textStyle = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            singleLine = true,
                            decorationBox = { innerTextField ->
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.fillMaxHeight()
                                ) {
                                    innerTextField()
                                }
                            },
                        )

                        ExposedDropdownMenu(
                            modifier = Modifier.fillMaxWidth(),
                            expanded = isInEditMode,
                            onDismissRequest = { },
                        ) {
                            val menuTopics = if (editModeText == "") {
                                availableTopics.filter { it !in selectedTopics }
                            } else {
                                availableTopics.filter {
                                    it.name.startsWith(
                                        editModeText,
                                        ignoreCase = true
                                    )
                                }
                            }

                            menuTopics.forEach { topic ->
                                TopicDropdownItem(text = topic.name,
                                    isCreateOption = false,
                                    onClick = {
                                        onTopicDefaultToggled(topic)
                                    })
                            }

                            TopicDropdownItem(
                                text = "Create '${editModeText}'",
                                isCreateOption = true,
                                onClick = onCreateTopic
                            )
                        }
                    }
                } else {
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

@Composable
private fun TopicDropdownItem(
    modifier: Modifier = Modifier,
    text: String,
    isCreateOption: Boolean = false,
    onClick: () -> Unit
) {
    DropdownMenuItem(
        text = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!isCreateOption) {
                    Text(
                        text = "#",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(16.dp)
                            .padding(end = 4.dp)
                    )
                }

                Text(
                    text = text,
                    maxLines = 1,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isCreateOption) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurface
                )
            }
        },
        onClick = onClick,
        colors = MenuDefaults.itemColors(
            textColor = if (isCreateOption) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurface
        ),
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun MyTopicsCardPreview() {
    EchoJournalTheme {
        EntryTopics(
            availableTopics = listOf(
                Topic(name = "Work"),
                Topic(name = "Family"),
                Topic(name = "Health")
            ),
            selectedTopics = listOf(
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
        EntryTopics()
    }
}

@Preview(showBackground = true)
@Composable
fun EditModeMyTopicsCardPreview() {
    EchoJournalTheme {
        Column(modifier = Modifier.fillMaxSize()) {
            EntryTopics(
                isInEditMode = true,
                editModeText = "St",
                availableTopics = listOf(
                    Topic(name = "Stuff"),
                    Topic(name = "Stuff 2"),
                    Topic(name = "Stuff 3")
                )
            )
        }
    }
}