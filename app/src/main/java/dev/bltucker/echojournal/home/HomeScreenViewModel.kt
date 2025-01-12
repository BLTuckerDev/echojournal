package dev.bltucker.echojournal.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bltucker.echojournal.common.JournalRepository
import dev.bltucker.echojournal.common.TopicsRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val journalRepository: JournalRepository,
    private val topicRepository: TopicsRepository,) : ViewModel() {

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
        mutableModel.update {
            it.copy(showRecordingBottomSheet = true, recordingState = it.recordingState.copy(isRecording = true, hasStartedRecording = true, isPaused = false))
        }
        startTimeTracking()
    }

    fun onStartRecording() {
        mutableModel.update {
            it.copy(recordingState = it.recordingState.copy(hasStartedRecording = true, isRecording = true, isPaused = false))
        }
        startTimeTracking()
    }

    fun onPauseRecording() {
        mutableModel.update {
            it.copy(recordingState = it.recordingState.copy(isPaused = true, isRecording = false))
        }
        stopTimeTracking()
    }

    fun onResumeRecording() {
        mutableModel.update {
            it.copy(recordingState = it.recordingState.copy(isPaused = false, isRecording = true))
        }
        startTimeTracking()
    }

    fun onCancelRecording() {
        mutableModel.update {
            it.copy(showRecordingBottomSheet = false, recordingState = RecordingState())
        }
        stopTimeTracking()
    }

    fun onFinishRecording() {
        //TODO finish it
        stopTimeTracking()
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
    }

}