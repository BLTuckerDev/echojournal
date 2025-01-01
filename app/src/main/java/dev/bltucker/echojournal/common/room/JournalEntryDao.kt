package dev.bltucker.echojournal.common.room

import androidx.room.*
import dev.bltucker.echojournal.common.Mood
import kotlinx.coroutines.flow.Flow
import java.time.Instant

@Dao
interface JournalEntryDao {
    @Query("SELECT * FROM journal_entries ORDER BY createdAt DESC")
    fun getAllEntries(): Flow<List<JournalEntry>>

    @Transaction
    @Query("""
        SELECT DISTINCT je.* FROM journal_entries je
        LEFT JOIN entry_topics et ON je.id = et.entryId
        LEFT JOIN topics t ON et.topicId = t.id
        WHERE (:mood IS NULL OR je.mood = :mood)
        AND (:topicId IS NULL OR t.id = :topicId)
        ORDER BY je.createdAt DESC
    """)
    fun getFilteredEntries(
        mood: Mood? = null,
        topicId: String? = null
    ): Flow<List<JournalEntry>>

    @Query("""
        SELECT * FROM journal_entries 
        WHERE createdAt >= :startDate 
        AND createdAt < :endDate
        ORDER BY createdAt DESC
    """)
    fun getEntriesForDateRange(
        startDate: Instant,
        endDate: Instant
    ): Flow<List<JournalEntry>>

    @Query("SELECT * FROM journal_entries WHERE id = :id")
    suspend fun getEntryById(id: String): JournalEntry?

    @Insert
    suspend fun insertEntry(entry: JournalEntry): Long

    @Update
    suspend fun updateEntry(entry: JournalEntry)

    @Delete
    suspend fun deleteEntry(entry: JournalEntry)

    // Relationship queries
    data class EntryWithTopics(
        @Embedded val entry: JournalEntry,
        @Relation(
            parentColumn = "id",
            entityColumn = "id",
            associateBy = Junction(
                value = EntryTopicCrossRef::class,
                parentColumn = "entryId",
                entityColumn = "topicId"
            )
        )
        val topics: List<Topic>
    )

    @Transaction
    @Query("SELECT * FROM journal_entries WHERE id = :entryId")
    fun getEntryWithTopics(entryId: String): Flow<EntryWithTopics>

    @Transaction
    @Query("SELECT * FROM journal_entries ORDER BY createdAt DESC")
    fun getAllEntriesWithTopics(): Flow<List<EntryWithTopics>>
}