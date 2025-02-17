package dev.bltucker.echojournal.createentry

import dev.bltucker.echojournal.common.Mood
import dev.bltucker.echojournal.common.room.JournalEntry
import dev.bltucker.echojournal.common.room.Topic

data class CreateEntryScreenModel(
    val journalEntry: JournalEntry? = null,

    val title: String = "",
    val description: String =  "",
    val selectedMood: Mood? = null,
    val entryTopics: Set<Topic> = emptySet(),
    val selectedTopics: Set<Topic> = emptySet(),

    val isShowingMoodSelector: Boolean = false,
    val isShowingTopicSelector: Boolean = false,

    val topicSearchQuery: String = "",
    val availableTopics: List<Topic> = emptyList(),
    val filteredTopics: List<Topic> = emptyList(),

    val isPlaying: Boolean = false,
    val playbackProgress: Float = 0f,
    val audioDuration: String = "0:00/0:00",

    val showDiscardChangesDialog: Boolean = false,

    val snackbarMessage: String? = null,

    ) {
    val hasMadeChanges: Boolean
        get() = title != journalEntry?.title ||
                description != journalEntry.description ||
                selectedMood != journalEntry.mood ||
                selectedTopics != entryTopics
}
