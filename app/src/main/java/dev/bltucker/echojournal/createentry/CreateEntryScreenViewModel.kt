package dev.bltucker.echojournal.createentry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bltucker.echojournal.common.AudioPlayer
import dev.bltucker.echojournal.common.JournalRepository
import dev.bltucker.echojournal.common.Mood
import dev.bltucker.echojournal.common.MoodRepository
import dev.bltucker.echojournal.common.TopicsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toSet
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class CreateEntryScreenViewModel @Inject constructor(
    private val journalRepository: JournalRepository,
    private val topicsRepository: TopicsRepository,
    private val moodRepository: MoodRepository,
    private val audioPlayer: AudioPlayer,
    @Named("AudioDirectory") private val audioDirectory: File
) : ViewModel(){

    private val mutableModel = MutableStateFlow(CreateEntryScreenModel())

    val observableModel: StateFlow<CreateEntryScreenModel> = mutableModel

    private var hasStarted = false


    fun onStart(entryId: String){
        if(hasStarted){
            return
        }

        hasStarted = true

        observeAudioPlaybackState()

        loadInitialData(entryId)
    }

    private fun loadInitialData(entryId: String) {
        viewModelScope.launch {
            val journalEntry = journalRepository.getJournalById(entryId)

            val defaultMood = moodRepository.defaultMood.firstOrNull()

            val availableTopics = topicsRepository.getAllTopics().firstOrNull() ?: emptyList()
            val entryTopics = if (journalEntry != null) {
                topicsRepository.getTopicForEntry(entryId).firstOrNull() ?: emptyList()
            } else {
                topicsRepository.getAutoAppliedTopics().firstOrNull() ?: emptyList()
            }


            mutableModel.update { currentModel ->
                currentModel.copy(
                    journalEntry = journalEntry,
                    title = journalEntry?.title ?: "",
                    description = journalEntry?.description ?: "",
                    selectedMood = journalEntry?.mood ?: defaultMood,
                    selectedTopics = entryTopics.toSet(),
                    availableTopics = availableTopics,
                    filteredTopics = availableTopics,
                    audioDuration = formatDuration(journalEntry?.durationSeconds ?: 0),
                )
            }
        }
    }

    private fun observeAudioPlaybackState() {
        viewModelScope.launch {
            audioPlayer.currentPlaybackState.collect { playbackState ->
                mutableModel.update { currentModel ->
                    currentModel.copy(
                        isPlaying = playbackState is AudioPlayer.PlaybackState.Playing,
                        playbackProgress = when (playbackState) {
                            is AudioPlayer.PlaybackState.Playing -> playbackState.progress
                            is AudioPlayer.PlaybackState.Paused ->
                                playbackState.position.toFloat() / playbackState.duration
                            else -> 0f
                        }
                    )
                }
            }
        }
    }

    private fun formatDuration(totalSeconds: Int): String {
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%d:%02d", minutes, seconds)
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayer.stopPlayback()
    }

    fun onConfirmMoodSelection(selectedMood: Mood) {
        viewModelScope.launch {
            val updatedEntry = mutableModel.value.journalEntry?.copy(mood = selectedMood) ?: return@launch
            journalRepository.updateJournalEntry(updatedEntry)

            mutableModel.update {
                it.copy(selectedMood = updatedEntry.mood, journalEntry = updatedEntry, isShowingMoodSelector = false)
            }
        }
    }

    fun onDismissMoodSelector() {
        mutableModel.update {
            it.copy(isShowingMoodSelector = false)
        }
    }

    fun onShowMoodSelector() {
        mutableModel.update {
            it.copy(isShowingMoodSelector = true)
        }
    }

    fun onEntryTitleChange(updatedTitle: String){
        mutableModel.update {
            it.copy(title = updatedTitle)
        }
    }

    fun onSave(){
        viewModelScope.launch {
            val latestModel = mutableModel.value
            val entry = latestModel.journalEntry ?: return@launch
            val mood = latestModel.selectedMood ?: entry.mood

            val topics = latestModel.selectedTopics.toList()

            val updatedEntry = entry.copy(
                title = mutableModel.value.title,
                description = mutableModel.value.description,
                mood = mood,
            )

            journalRepository.updateJournalEntry(updatedEntry)
            topicsRepository.updateTopicsForEntry(updatedEntry.id, topics)

            mutableModel.update {
                it.copy(journalEntry = updatedEntry,
                    title = updatedEntry.title,
                    description = updatedEntry.description,
                    selectedMood = updatedEntry.mood,
                    snackbarMessage = "Save Complete"
                )
            }
        }
    }

    fun onClearSnackbarMessage() {
        mutableModel.update {
            it.copy(snackbarMessage = null)
        }
    }
}