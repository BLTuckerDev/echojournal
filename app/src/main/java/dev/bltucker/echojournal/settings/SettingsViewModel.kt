package dev.bltucker.echojournal.settings

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bltucker.echojournal.common.Mood
import dev.bltucker.echojournal.common.MoodRepository
import dev.bltucker.echojournal.common.TopicsRepository
import dev.bltucker.echojournal.common.room.Topic
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val moodRepository: MoodRepository,
    private val topicsRepository: TopicsRepository
) : ViewModel() {

    private val mutableModel = MutableStateFlow(SettingsModel())
    val observableModel: StateFlow<SettingsModel> = mutableModel

    private var hasStarted = false

    fun onStart() {
        if (hasStarted) {
            return
        }

        hasStarted = true
        observeDefaultMood()
        observeTopics()
    }

    private fun observeDefaultMood() {
        viewModelScope.launch {
            moodRepository.defaultMood.collect { mood ->
                mutableModel.update {
                    it.copy(defaultMood = mood)
                }
            }
        }
    }

    private fun observeTopics() {
        viewModelScope.launch {
            topicsRepository.getAllTopics().collect { topics ->
                mutableModel.update {
                    it.copy(availableTopics = topics)
                }
            }
        }
    }

    fun onUpdateDefaultMood(mood: Mood) {
        viewModelScope.launch {
            moodRepository.setDefaultMood(mood)
        }
    }

    fun onToggleDefaultTopic(topic: Topic) {
        viewModelScope.launch {
            topicsRepository.toggleDefaultStatus(topic)
            mutableModel.update { it.copy(isInTopicEditMode = false, editModeText = "") }
        }
    }

    fun onAddTopicClick(){
        mutableModel.update {
            it.copy(isInTopicEditMode = true)
        }
    }

    fun onClearEditTopicMode(){
        mutableModel.update {
            it.copy(isInTopicEditMode = false, editModeText = "")
        }
    }
    fun onCreateTopicClick(){
        val name = mutableModel.value.editModeText

        if(name.isBlank()){
            return
        }

        viewModelScope.launch {
            val nameExists = topicsRepository.topicNameExists(name)

            if(nameExists){
                return@launch
            }

            try{
                topicsRepository.createTopic(name = name, isDefault = true)
            } catch(ex: Exception){
                Log.e("SettingsViewModel", "Error creating topic", ex)
            }

            mutableModel.update {
                it.copy(isInTopicEditMode = false, editModeText = "")
            }
        }
    }

    fun onUpdateEditTopicText(text: String){
        mutableModel.update {
            it.copy(editModeText = text)
        }
    }
}