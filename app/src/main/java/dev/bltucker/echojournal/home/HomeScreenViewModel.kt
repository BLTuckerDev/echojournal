package dev.bltucker.echojournal.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bltucker.echojournal.common.AudioPlayer
import dev.bltucker.echojournal.common.AudioRecorder
import dev.bltucker.echojournal.common.JournalRepository
import dev.bltucker.echojournal.common.Mood
import dev.bltucker.echojournal.common.MoodRepository
import dev.bltucker.echojournal.common.TopicsRepository
import dev.bltucker.echojournal.common.room.JournalEntry
import dev.bltucker.echojournal.common.room.Topic
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.time.Instant
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val journalRepository: JournalRepository,
    private val topicRepository: TopicsRepository,
    private val moodRepository: MoodRepository,
    @Named("AudioDirectory") private val audioDirectory: File,
    private val audioRecorder: AudioRecorder,
    private val audioPlayer: AudioPlayer,
    ) : ViewModel() {

    private val mutableModel = MutableStateFlow(HomeModel())
    val observableModel: StateFlow<HomeModel> = mutableModel

    private var recordingTimeJob: Job? = null
    private var hasStarted = false

    fun onStart(){
        if(hasStarted){
            return
        }
        hasStarted = true

        loadAndObserveEntries()
        observeAudioPlayerState()
    }

    fun observeAudioPlayerState(){
        viewModelScope.launch {
            audioPlayer.currentPlaybackState.collect { playbackState ->
                updatePlaybackState(playbackState)
            }
        }
    }

    fun dismissPermissionRequest() {
        mutableModel.update { currentModel ->
            currentModel.copy(
                permissionState = currentModel.permissionState.copy(
                    shouldShowPermissionRequest = false
                )
            )
        }
    }

    private fun loadAndObserveEntries(){
        viewModelScope.launch {
            journalRepository.getAllJournalEntries().collect{ entries ->

                val topicsForEntriesMap = entries.map { it.id }
                    .associateWith { entryId ->
                        topicRepository.getTopicForEntry(entryId).firstOrNull() ?: emptyList()
                    }

                mutableModel.update {
                    it.copy(entries = entries, topicsByEntry = topicsForEntriesMap)
                }
            }
        }

        viewModelScope.launch {
            topicRepository.getAllTopics().collect { topics ->
                mutableModel.update { it.copy(topics = topics) }
            }
        }
    }

    fun onClickCreateEntry() {
        val hasAudioPermission = mutableModel.value.permissionState.hasAudioPermission

        if (!hasAudioPermission) {
            mutableModel.update {
                it.copy(permissionState = it.permissionState.copy(shouldShowPermissionRequest = true))
            }
            return
        }


        mutableModel.update {
            it.copy(showRecordingBottomSheet = true, recordingState = it.recordingState.copy(isRecording = true, hasStartedRecording = true, isPaused = false))
        }
        startTimeTracking()
        onStartRecording()
    }

    fun onStartRecording() {
        val fileName = "audio_${System.currentTimeMillis()}.wav"
        val currentRecordingFile = File(audioDirectory, fileName)
        currentRecordingFile.parentFile?.mkdirs()

        currentRecordingFile.let { file ->
            audioRecorder.startRecording(file, viewModelScope)
        }

        mutableModel.update {
            it.copy(recordingState = it.recordingState.copy(
                hasStartedRecording = true,
                isRecording = true,
                isPaused = false,
                currentRecordingFile = currentRecordingFile
            ))
        }
        startTimeTracking()
    }

    fun onPauseRecording() {
        audioRecorder.pauseRecording()
        mutableModel.update {
            it.copy(recordingState = it.recordingState.copy(
                isPaused = true,
                isRecording = false
            ))
        }
        stopTimeTracking()
    }

    fun onResumeRecording() {
        audioRecorder.resumeRecording()
        mutableModel.update {
            it.copy(recordingState = it.recordingState.copy(
                isPaused = false,
                isRecording = true
            ))
        }
        startTimeTracking()
    }

    fun onCancelRecording() {
        audioRecorder.stopRecording()
        val currentRecordingFile = mutableModel.value.recordingState.currentRecordingFile
        currentRecordingFile?.delete()

        mutableModel.update {
            it.copy(
                showRecordingBottomSheet = false,
                recordingState = RecordingState()
            )
        }
        stopTimeTracking()
    }

    fun onFinishRecording() {
        viewModelScope.launch {
            audioRecorder.stopRecording()
            stopTimeTracking()

            val defaultMood = moodRepository.defaultMood.firstOrNull() ?: Mood.NEUTRAL

            val entryId = journalRepository.createJournalEntry(
                audioFilePath = mutableModel.value.recordingState.currentRecordingFile!!.absolutePath,
                durationSeconds = mutableModel.value.recordingState.elapsedSeconds,
                defaultMood = defaultMood
            )


            mutableModel.update {
                it.copy(finishedRecordingId = entryId,
                    showRecordingBottomSheet = false,
                    recordingState = RecordingState())
            }
        }
    }

    private fun startTimeTracking() {
        stopTimeTracking()

        recordingTimeJob = viewModelScope.launch {
            while (true) {
                delay(1000) // Wait for 1 second
                mutableModel.update { currentModel ->
                    currentModel.copy(
                        recordingState = currentModel.recordingState.copy(
                            elapsedSeconds = currentModel.recordingState.elapsedSeconds + 1
                        )
                    )
                }
            }
        }
    }

    private fun stopTimeTracking() {
        recordingTimeJob?.cancel()
        recordingTimeJob = null
    }

    override fun onCleared() {
        super.onCleared()
        stopTimeTracking()
        audioRecorder.stopRecording()
    }

    fun updatePermissionState(
        hasPermission: Boolean,
        canShowRationale: Boolean
    ) {
        mutableModel.update { currentModel ->
            currentModel.copy(
                permissionState = currentModel.permissionState.copy(
                    hasAudioPermission = hasPermission,
                    shouldShowPermissionRequest = false,
                    userHasRepeatedlyDenied = !hasPermission && !canShowRationale
                )
            )
        }
    }

    fun initializePermissionState(hasPermissionState: Boolean){
        mutableModel.update { currentModel ->
            currentModel.copy(
                permissionState = currentModel.permissionState.copy(
                    hasAudioPermission = hasPermissionState,
                    userHasRepeatedlyDenied = false
                )
            )
        }
    }

    fun onShowRequestHandled() {
        mutableModel.update { it.copy(permissionState = it.permissionState.copy(shouldShowPermissionRequest = false)) }
    }

    fun onHandledFinishedRecording() {
        mutableModel.update { it.copy(finishedRecordingId = null) }
    }

    fun onPlayPauseClick(entryId: String) {
        val currentState = audioPlayer.currentPlaybackState.value
        val entry = mutableModel.value.entries.find { it.id == entryId } ?: return

        when (currentState) {
            is AudioPlayer.PlaybackState.Playing -> {
                if (currentState.entryId == entryId) {
                    audioPlayer.pausePlayback()
                } else {
                    // Start playing new entry
                    playEntry(entry)
                }
            }
            is AudioPlayer.PlaybackState.Paused -> {
                if (currentState.entryId == entryId) {
                    audioPlayer.resumePlayback(viewModelScope)
                } else {
                    playEntry(entry)
                }
            }
            is AudioPlayer.PlaybackState.Idle, is AudioPlayer.PlaybackState.Error -> {
                playEntry(entry)
            }
        }
    }

    private fun playEntry(entry: JournalEntry) {
        val audioFile = File(entry.audioFilePath)
        if (audioFile.exists()) {
            audioPlayer.playAudio(viewModelScope, audioFile, entry.id)
        }
    }

    private fun updatePlaybackState(playbackState: AudioPlayer.PlaybackState) {
        mutableModel.update { currentModel ->
            currentModel.copy(
                entries = currentModel.entries,
                currentPlaybackId = when (playbackState) {
                    is AudioPlayer.PlaybackState.Playing -> playbackState.entryId
                    is AudioPlayer.PlaybackState.Paused -> playbackState.entryId
                    AudioPlayer.PlaybackState.Idle, is AudioPlayer.PlaybackState.Error -> null
                },
                currentPlaybackState = playbackState,
                playbackProgress = when (playbackState) {
                    is AudioPlayer.PlaybackState.Playing -> playbackState.progress
                    is AudioPlayer.PlaybackState.Paused -> playbackState.position.toFloat() / playbackState.duration
                    AudioPlayer.PlaybackState.Idle, is AudioPlayer.PlaybackState.Error -> 0f
                }
            )
        }
    }

    fun onMoodFilterClick() {
        mutableModel.update { it.copy(showMoodFilterMenu = true) }
    }

    fun onTopicFilterClick() {
        mutableModel.update { it.copy(showTopicFilterMenu = true) }
    }

    fun onDismissMoodFilter() {
        mutableModel.update { it.copy(showMoodFilterMenu = false) }
    }

    fun onDismissTopicFilter() {
        mutableModel.update { it.copy(showTopicFilterMenu = false) }
    }

    fun onMoodSelected(mood: Mood) {
        mutableModel.update { currentModel ->
            val updatedMoods = currentModel.selectedMoods.toMutableSet()
            if (mood in updatedMoods) {
                updatedMoods.remove(mood)
            } else {
                updatedMoods.add(mood)
            }
            currentModel.copy(selectedMoods = updatedMoods)
        }
    }

    fun onTopicSelected(topic: Topic) {
        mutableModel.update { currentModel ->
            val updatedTopics = currentModel.selectedTopics.toMutableSet()
            if (topic in updatedTopics) {
                updatedTopics.remove(topic)
            } else {
                updatedTopics.add(topic)
            }
            currentModel.copy(selectedTopics = updatedTopics)
        }
    }

    fun onClearMoodFilter() {
        mutableModel.update { it.copy(selectedMoods = emptySet()) }
    }

    fun onClearTopicFilter() {
        mutableModel.update { it.copy(selectedTopics = emptySet()) }
    }

}