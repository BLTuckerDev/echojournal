package dev.bltucker.echojournal.common

import android.content.Context
import android.media.MediaPlayer
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioPlayer @Inject constructor() {
    private var mediaPlayer: MediaPlayer? = null
    private var progressUpdateJob: Job? = null

    private val _currentPlaybackState = MutableStateFlow<PlaybackState>(PlaybackState.Idle)
    val currentPlaybackState: StateFlow<PlaybackState> = _currentPlaybackState

    fun playAudio(coroutineScope: CoroutineScope, file: File, entryId: String) {
        stopPlayback()

        try {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(file.path)
                prepare()
                start()
            }

            _currentPlaybackState.value = PlaybackState.Playing(
                entryId = entryId,
                duration = mediaPlayer?.duration ?: 0
            )

            startProgressUpdates(coroutineScope)

            mediaPlayer?.setOnCompletionListener {
                stopPlayback()
            }
        } catch (e: Exception) {
            _currentPlaybackState.value = PlaybackState.Error(e.message ?: "Unknown error")
            stopPlayback()
        }
    }

    fun pausePlayback() {
        mediaPlayer?.pause()
        progressUpdateJob?.cancel()
        _currentPlaybackState.value = PlaybackState.Paused(
            entryId = (_currentPlaybackState.value as? PlaybackState.Playing)?.entryId ?: "",
            position = mediaPlayer?.currentPosition ?: 0,
            duration = mediaPlayer?.duration ?: 0
        )
    }

    fun resumePlayback(coroutineScope: CoroutineScope,) {
        if (_currentPlaybackState.value is PlaybackState.Paused) {
            mediaPlayer?.start()
            startProgressUpdates(coroutineScope)
            _currentPlaybackState.value = PlaybackState.Playing(
                entryId = (_currentPlaybackState.value as PlaybackState.Paused).entryId,
                duration = mediaPlayer?.duration ?: 0
            )
        }
    }

    fun stopPlayback() {
        progressUpdateJob?.cancel()
        mediaPlayer?.apply {
            stop()
            reset()
            release()
        }
        mediaPlayer = null
        _currentPlaybackState.value = PlaybackState.Idle
    }

    private fun startProgressUpdates(coroutineScope: CoroutineScope,) {
        progressUpdateJob?.cancel()
        progressUpdateJob = coroutineScope.launch {
            while (true) {
                mediaPlayer?.let { player ->
                    if (player.isPlaying) {
                        (_currentPlaybackState.value as? PlaybackState.Playing)?.let { playingState ->
                            _currentPlaybackState.value = playingState.copy(
                                progress = player.currentPosition.toFloat() / player.duration
                            )
                        }
                    }
                }
                delay(100)
            }
        }
    }

    sealed class PlaybackState {
        data object Idle : PlaybackState()
        data class Playing(
            val entryId: String,
            val duration: Int,
            val progress: Float = 0f
        ) : PlaybackState()
        data class Paused(
            val entryId: String,
            val position: Int,
            val duration: Int
        ) : PlaybackState()
        data class Error(val message: String) : PlaybackState()
    }
}