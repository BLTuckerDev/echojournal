package dev.bltucker.echojournal.common

import dev.bltucker.echojournal.common.room.JournalEntryDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JournalRepository @Inject constructor(private val journalEntryDao: JournalEntryDao) {

    fun getAllJournalEntries() = journalEntryDao.getAllEntries()
}