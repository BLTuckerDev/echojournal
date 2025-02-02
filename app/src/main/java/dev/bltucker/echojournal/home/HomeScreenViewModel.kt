package dev.bltucker.echojournal.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bltucker.echojournal.common.AudioRecorder
import dev.bltucker.echojournal.common.JournalRepository
import dev.bltucker.echojournal.common.TopicsRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val journalRepository: JournalRepository,
    private val topicRepository: TopicsRepository,
    @Named("AudioDirectory") private val audioDirectory: File,
    private val audioRecorder: AudioRecorder,) : ViewModel() {

    private val mutableModel = MutableStateFlow(HomeModel())
    private var currentRecordingFile: File? = null //TODO move into the model


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
        if (!mutableModel.value.permissionState.hasAudioPermission) {
            mutableModel.update { currentModel ->
                currentModel.copy(
                    permissionState = currentModel.permissionState.copy(
                        shouldShowPermissionRequest = true
                    )
                )
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
        if (currentRecordingFile == null) {
            val fileName = "audio_${System.currentTimeMillis()}.wav"
            currentRecordingFile = File(audioDirectory, fileName)
            currentRecordingFile?.parentFile?.mkdirs()
        }

        currentRecordingFile?.let { file ->
            audioRecorder.startRecording(file, viewModelScope)
        }

        mutableModel.update {
            it.copy(recordingState = it.recordingState.copy(
                hasStartedRecording = true,
                isRecording = true,
                isPaused = false
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
        currentRecordingFile?.delete()
        currentRecordingFile = null

        mutableModel.update {
            it.copy(
                showRecordingBottomSheet = false,
                recordingState = RecordingState()
            )
        }
        stopTimeTracking()
    }

    fun onFinishRecording() {
        audioRecorder.stopRecording()
        // Keep currentRecordingFile for saving the entry
        // It will be used when creating the JournalEntry

        stopTimeTracking()
        // Navigate to create entry screen with audio file path
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

    fun updatePermissionState(hasPermission: Boolean) {
        mutableModel.update { currentModel ->
            currentModel.copy(
                permissionState = currentModel.permissionState.copy(
                    hasAudioPermission = hasPermission,
                    // Only show the banner when we don't have permission
                    shouldShowPermissionRequest = !hasPermission
                )
            )
        }

        // If we just got permission and the recording sheet was trying to show,
        // we can now actually show it
        if (hasPermission && !mutableModel.value.showRecordingBottomSheet) {
            onClickCreateEntry()
        }
    }

}