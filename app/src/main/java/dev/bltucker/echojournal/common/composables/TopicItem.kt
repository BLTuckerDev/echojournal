package dev.bltucker.echojournal.common.composables

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import dev.bltucker.echojournal.R
import dev.bltucker.echojournal.common.room.Topic
import dev.bltucker.echojournal.common.theme.EchoJournalColors

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
        border = null,
        colors = FilterChipDefaults.filterChipColors(
            containerColor = EchoJournalColors.Gray6,
            selectedContainerColor = EchoJournalColors.Gray6,
        ),

        label = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "#",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = topic.name,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        },
        trailingIcon = {
            Icon(
                modifier = Modifier.size(16.dp),
                painter = painterResource(R.drawable.icon_close),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                contentDescription = "Remove ${topic.name} topic"
            )
        }
    )
}