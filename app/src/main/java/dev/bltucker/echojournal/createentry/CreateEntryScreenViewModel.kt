package dev.bltucker.echojournal.createentry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bltucker.echojournal.common.JournalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateEntryScreenViewModel @Inject constructor(private val journalRepository: JournalRepository) : ViewModel(){

    private val mutableModel = MutableStateFlow(CreateEntryScreenModel())

    val observableModel: StateFlow<CreateEntryScreenModel> = mutableModel

    private var hasStarted = false


    fun onStart(entryId: String){
        if(hasStarted){
            return
        }

        hasStarted = true

        viewModelScope.launch {
            val journalEntry = journalRepository.getJournalById(entryId)
            mutableModel.update{
                it.copy(journalEntry = journalEntry)
            }
        }
    }

}