package dev.bltucker.echojournal.home.composables

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.bltucker.echojournal.R
import dev.bltucker.echojournal.common.theme.EchoJournalTheme
import dev.bltucker.echojournal.common.theme.GradientColors
import dev.bltucker.echojournal.home.RecordingState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordingBottomSheet(
    modifier: Modifier = Modifier,
    state: RecordingState,
    onStartRecording: () -> Unit = {},
    onPauseRecording: () -> Unit = {},
    onResumeRecording: () -> Unit = {},
    onCancelRecording: () -> Unit = {},
    onFinishRecording: () -> Unit = {}
) {
    ModalBottomSheet(
        modifier = modifier,
        onDismissRequest = onCancelRecording,
        dragHandle = {BottomSheetDefaults.DragHandle()},
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = if (state.isPaused) "Recording paused" else "Recording your memories...",
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )

            Text(
                text = state.recordingDuration,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Cancel Button
                IconButton(
                    onClick = onCancelRecording,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.icon_close),
                        contentDescription = "Cancel recording",
                        tint = MaterialTheme.colorScheme.error
                    )
                }

                // Main Record/Pause Button
                FloatingActionButton(
                    onClick = if (state.isPaused) onResumeRecording else onPauseRecording,
                    modifier = Modifier.size(72.dp),
                    containerColor = MaterialTheme.colorScheme.primary,
                    shape = CircleShape
                ) {
                    if (state.showCheckmark) {
                        Icon(
                            painter = painterResource(R.drawable.icon_check),
                            contentDescription = "Finish recording",
                            modifier = Modifier.size(32.dp),
                            tint = Color.White
                        )
                    } else {
                        Icon(
                            painter = painterResource(
                                if (state.isPaused) R.drawable.icon_mic
                                else R.drawable.icon_pause
                            ),
                            contentDescription = if (state.isPaused) "Resume recording"
                            else "Pause recording",
                            modifier = Modifier.size(32.dp),
                            tint = Color.White
                        )
                    }
                }

                // Finish/Checkmark Button
                if (state.isRecording && !state.showCheckmark) {
                    IconButton(
                        onClick = onFinishRecording,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.icon_check),
                            contentDescription = "Finish recording",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.size(48.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}