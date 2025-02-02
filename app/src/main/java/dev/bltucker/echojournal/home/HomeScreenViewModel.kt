package dev.bltucker.echojournal.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bltucker.echojournal.common.AudioRecorder
import dev.bltucker.echojournal.common.JournalRepository
import dev.bltucker.echojournal.common.Mood
import dev.bltucker.echojournal.common.MoodRepository
import dev.bltucker.echojournal.common.TopicsRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val journalRepository: JournalRepository,
    private val topicRepository: TopicsRepository,
    private val moodRepository: MoodRepository,
    @Named("AudioDirectory") private val audioDirectory: File,
    private val audioRecorder: AudioRecorder,) : ViewModel() {

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
                mutableModel.update {
                    it.copy(entries = entries)
                }
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

}