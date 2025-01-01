package dev.bltucker.echojournal.settings.composables

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.bltucker.echojournal.R
import dev.bltucker.echojournal.common.room.Topic
import dev.bltucker.echojournal.common.theme.EchoJournalTheme

@Composable
fun TopicItem(
    modifier: Modifier = Modifier,
    topic: Topic,
    isDefaulted: Boolean,
    onToggleDefault: () -> Unit
) {
    FilterChip(
        modifier = modifier,
        shape = RoundedCornerShape(100.dp),
        selected = isDefaulted,
        onClick = onToggleDefault,
        label = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "#",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = topic.name,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        },
        trailingIcon = {
            Icon(
                modifier = Modifier.size(16.dp),
                painter = painterResource(R.drawable.icon_close),
                contentDescription = "Remove ${topic.name} topic"
            )
        }
    )
}

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