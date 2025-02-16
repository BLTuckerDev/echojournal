package dev.bltucker.echojournal.createentry.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import dev.bltucker.echojournal.R
import dev.bltucker.echojournal.common.Mood
import dev.bltucker.echojournal.common.theme.MoodColors

@Composable
fun MoodIndicator(
    modifier: Modifier = Modifier,
    mood: Mood?,
    onClick: () -> Unit
) {
    val moodColor = when (mood) {
        Mood.STRESSED -> MoodColors.Stressed80
        Mood.SAD -> MoodColors.Sad80
        Mood.NEUTRAL -> MoodColors.Neutral90
        Mood.PEACEFUL -> MoodColors.Peaceful80
        Mood.EXCITED -> MoodColors.Excited80
        null -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        color = if (mood != null) moodColor.copy(alpha = 0.1f) else Color.Transparent,
        border = if (mood != null) null else ButtonDefaults.outlinedButtonBorder
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (mood != null) {
                Icon(
                    painter = painterResource(
                        when (mood) {
                            Mood.STRESSED -> R.drawable.mood_stressed
                            Mood.SAD -> R.drawable.mood_sad
                            Mood.NEUTRAL -> R.drawable.mood_neutral
                            Mood.PEACEFUL -> R.drawable.mood_peaceful
                            Mood.EXCITED -> R.drawable.mood_excited
                        }
                    ),
                    contentDescription = "Current mood: ${mood.name.lowercase()}",
                    tint = moodColor,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Icon(
                    painter = painterResource(R.drawable.icon_add),
                    contentDescription = "Add mood",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}