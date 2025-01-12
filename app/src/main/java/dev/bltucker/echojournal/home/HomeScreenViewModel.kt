package dev.bltucker.echojournal.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bltucker.echojournal.common.JournalRepository
import dev.bltucker.echojournal.common.TopicsRepository
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



}