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
import dev.bltucker.echojournal.common.theme.EchoJournalColors.Error95
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
        SheetContent(
            state = state,
            onStartRecording = onStartRecording,
            onPauseRecording = onPauseRecording,
            onResumeRecording = onResumeRecording,
            onCancelRecording = onCancelRecording,
            onFinishRecording = onFinishRecording,
        )
    }
}

@Composable
private fun SheetContent(modifier: Modifier = Modifier,
                         state: RecordingState,
                         onStartRecording: () -> Unit = {},
                         onPauseRecording: () -> Unit = {},
                         onResumeRecording: () -> Unit = {},
                         onCancelRecording: () -> Unit = {},
                         onFinishRecording: () -> Unit = {}){
    Column(
        modifier = modifier
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
            IconButton(
                onClick = onCancelRecording,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(color = Error95)
            ) {
                Icon(
                    painter = painterResource(R.drawable.icon_close),
                    contentDescription = "Cancel recording",
                    tint = MaterialTheme.colorScheme.error
                )
            }

            FloatingActionButton(
                onClick = if (state.isPaused) onResumeRecording else onPauseRecording,
                modifier = Modifier.size(72.dp),
                containerColor = MaterialTheme.colorScheme.primary,
                shape = CircleShape
            ) {
                if (state.isRecording) {
                    IconButton(onClick = onFinishRecording) {
                        Icon(
                            painter = painterResource(R.drawable.icon_check),
                            contentDescription = "Finish recording",
                            modifier = Modifier.size(32.dp),
                            tint = Color.White
                        )
                    }
                } else {
                    IconButton(onClick = {
                        if(state.hasStartedRecording){
                            onResumeRecording()
                        } else {
                            onStartRecording()
                        }
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.icon_mic),
                            contentDescription = "start recording",
                            modifier = Modifier.size(32.dp),
                            tint = Color.White
                        )
                    }
                }
            }

            if (state.isRecording) {
                IconButton(
                    onClick = onPauseRecording,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(color = Color(0xFFEEF0FF))
                ) {
                    Icon(
                        painter = painterResource(R.drawable.icon_pause),
                        contentDescription = "pause recording",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            } else if(state.isPaused){
                IconButton(
                    onClick = onFinishRecording,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(color = Color(0xFFEEF0FF))
                ) {
                    Icon(
                        painter = painterResource(R.drawable.icon_check),
                        contentDescription = "Finish recording",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            } else {
                Spacer(modifier = Modifier.width(48.dp))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Preview(showBackground = true)
@Composable
private fun SheetContentPreview(){
    EchoJournalTheme {
        SheetContent(
            state = RecordingState(
                hasStartedRecording = true,
                isRecording = true,
                isPaused = false,
                elapsedSeconds = 0),
            onStartRecording = {},
            onPauseRecording = {},
            onResumeRecording = {},
            onCancelRecording = {},
            onFinishRecording = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SheetContentBeforeStartingPreview(){
    EchoJournalTheme {
        SheetContent(
            state = RecordingState(
                hasStartedRecording = false,
                isRecording = false,
                isPaused = false,
                elapsedSeconds = 0),
            onStartRecording = {},
            onPauseRecording = {},
            onResumeRecording = {},
            onCancelRecording = {},
            onFinishRecording = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SheetContentPausedPreview(){
    EchoJournalTheme {
        SheetContent(
            state = RecordingState(
                hasStartedRecording = true,
                isRecording = false,
                isPaused = true,
                elapsedSeconds = 0),
            onStartRecording = {},
            onPauseRecording = {},
            onResumeRecording = {},
            onCancelRecording = {},
            onFinishRecording = {}
        )
    }
}