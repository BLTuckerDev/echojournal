package dev.bltucker.echojournal.common.composables

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import dev.bltucker.echojournal.R
import dev.bltucker.echojournal.common.Mood
import dev.bltucker.echojournal.common.theme.MoodColors
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun AudioPlayer(
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
                moodColor = moodColor,
                progress = progress
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
    progress: Float,
    moodColor: Color,
    barCount: Int = 30
) {
    val baseHeights = remember {
        List(barCount) { 0.3f + (Random.nextFloat() * 0.4f) }
    }

    val animatedHeights = List(barCount) { index ->
        val baseHeight = baseHeights[index]
        val animatedHeight by animateFloatAsState(
            targetValue = if (isPlaying) {
                baseHeight + (Random.nextFloat() * 0.2f)
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
                delay(200)
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
        animatedHeights.forEachIndexed { index, height ->
            val barProgress = index.toFloat() / barCount
            val isInPlayedSection = barProgress <= progress

            val barColor = if (isInPlayedSection) {
                moodColor.copy(alpha = 0.7f)
            } else {
                moodColor.copy(alpha = 0.3f)
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(height)
                    .padding(horizontal = 0.5.dp)
                    .background(
                        color = barColor,
                        shape = RoundedCornerShape(2.dp)
                    )
            )
        }
    }
}