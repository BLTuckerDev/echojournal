package dev.bltucker.echojournal.home.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.bltucker.echojournal.R
import dev.bltucker.echojournal.common.room.Topic
import dev.bltucker.echojournal.common.theme.EchoJournalTheme

@Composable
fun TopicFilterButton(
    modifier: Modifier = Modifier,
    selectedTopics: Set<Topic>,
    onClick: () -> Unit,
    onClickClearFilter: () -> Unit,
) {
    if (selectedTopics.isEmpty()) {
        OutlinedButton(
            modifier = modifier,
            onClick = onClick
        ) {
            Text(text = "All Topics", color = MaterialTheme.colorScheme.onSurface)
        }
    } else {
        OutlinedButton(
            modifier = modifier,
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
            onClick = onClick
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Show first two topics + count of remaining
                val displayTopics = selectedTopics.toList()
                val remaining = (selectedTopics.size - 2).coerceAtLeast(0)

                displayTopics.take(2).forEach { topic ->
                    Text(
                        text = topic.name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (remaining > 0) {
                    Text(
                        text = "+$remaining",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Icon(
                    modifier = Modifier
                        .size(16.dp)
                        .clickable { onClickClearFilter() },
                    painter = painterResource(R.drawable.icon_close),
                    contentDescription = "Clear topic filter",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun TopicFilterDropdown(
    modifier: Modifier = Modifier,
    topics: List<Topic>,
    selectedTopics: Set<Topic>,
    onTopicSelected: (Topic) -> Unit,
    expanded: Boolean,
    onDismiss: () -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
        modifier = modifier.background(MaterialTheme.colorScheme.surface)
    ) {
        topics.forEach { topic ->
            DropdownMenuItem(
                text = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "#",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = topic.name,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        if (topic in selectedTopics) {
                            Icon(
                                painter = painterResource(R.drawable.icon_check),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                onClick = { onTopicSelected(topic) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TopicFilterButtonPreview() {
    EchoJournalTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Empty state
            TopicFilterButton(
                selectedTopics = emptySet(),
                onClick = {},
                onClickClearFilter = {}
            )

            // Single topic selected
            TopicFilterButton(
                selectedTopics = setOf(Topic(name = "Work")),
                onClick = {},
                onClickClearFilter = {}
            )

            // Multiple topics selected
            TopicFilterButton(
                selectedTopics = setOf(
                    Topic(name = "Family"),
                    Topic(name = "Love"),
                    Topic(name = "Work")
                ),
                onClick = {},
                onClickClearFilter = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TopicFilterDropdownPreview() {
    EchoJournalTheme {
        Surface {
            TopicFilterDropdown(
                topics = listOf(
                    Topic(name = "Work"),
                    Topic(name = "Family"),
                    Topic(name = "Love"),
                    Topic(name = "Friends")
                ),
                selectedTopics = setOf(
                    Topic(name = "Family"),
                    Topic(name = "Love")
                ),
                onTopicSelected = {},
                expanded = true,
                onDismiss = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TopicFilterCompletePreview() {
    val availableTopics = listOf(
        Topic(name = "Work"),
        Topic(name = "Family"),
        Topic(name = "Love"),
        Topic(name = "Friends")
    )

    var showTopicFilter by remember { mutableStateOf(false) }
    var selectedTopics by remember {
        mutableStateOf(
                availableTopics.take(3).toSet()
        )
    }



    EchoJournalTheme {

        Box(modifier = Modifier.padding(16.dp)) {
            TopicFilterButton(
                selectedTopics = selectedTopics,
                onClick = { showTopicFilter = true },
                onClickClearFilter = { selectedTopics = emptySet() }
            )

            TopicFilterDropdown(
                topics = availableTopics,
                selectedTopics = selectedTopics,
                onTopicSelected = { topic ->
                    selectedTopics = if (topic in selectedTopics) {
                        selectedTopics - topic
                    } else {
                        selectedTopics + topic
                    }
                },
                expanded = showTopicFilter,
                onDismiss = { showTopicFilter = false }
            )
        }
    }
}