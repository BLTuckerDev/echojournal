package dev.bltucker.echojournal.common.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        JournalEntry::class,
        Topic::class,
        EntryTopicCrossRef::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(EchoJournalTypeConverters::class)
abstract class EchoJournalDatabase : RoomDatabase() {
    abstract fun journalEntryDao(): JournalEntryDao
    abstract fun topicDao(): TopicDao

    companion object {
        const val DATABASE_NAME = "echo_journal.db"
    }
}