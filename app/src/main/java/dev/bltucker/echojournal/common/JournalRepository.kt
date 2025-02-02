package dev.bltucker.echojournal.common

import dev.bltucker.echojournal.common.room.JournalEntry
import dev.bltucker.echojournal.common.room.JournalEntryDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JournalRepository @Inject constructor(private val journalEntryDao: JournalEntryDao) {

    fun getAllJournalEntries() = journalEntryDao.getAllEntries()


    suspend fun getJournalById(entryId: String): JournalEntry? {
        return journalEntryDao.getEntryById(entryId)
    }

    suspend fun createJournalEntry(audioFilePath: String,
                           durationSeconds: Int,
                           defaultMood: Mood): String {
        val journalEntry = JournalEntry(
            audioFilePath = audioFilePath,
            durationSeconds = durationSeconds,
            transcription = null,
            mood = defaultMood,
            title = "",
            description = ""
        )

        journalEntryDao.insertEntry(journalEntry)

        return journalEntry.id
    }
}