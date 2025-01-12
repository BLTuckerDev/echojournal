package dev.bltucker.echojournal.common.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TopicDao {
    @Insert
    suspend fun insertTopic(topic: Topic)

    @Update
    suspend fun updateTopic(topic: Topic)

    @Delete
    suspend fun deleteTopic(topic: Topic)

    @Query("SELECT * FROM topics WHERE id = :id")
    suspend fun getTopicById(id: String): Topic?

    @Query("SELECT * FROM topics ORDER BY name ASC")
    fun getAllTopics(): Flow<List<Topic>>

    @Query("SELECT * FROM topics WHERE isDefault = 1 ORDER BY name ASC")
    fun getAutoAppliedTopics(): Flow<List<Topic>>

    @Insert
    suspend fun addTopicToEntry(crossRef: EntryTopicCrossRef)

    @Delete
    suspend fun removeTopicFromEntry(crossRef: EntryTopicCrossRef)

    @Query("DELETE FROM entry_topics WHERE entryId = :entryId")
    suspend fun removeAllTopicsFromEntry(entryId: String)

    @Query("""
        SELECT t.* FROM topics t
        INNER JOIN entry_topics et ON t.id = et.topicId
        WHERE et.entryId = :entryId
        ORDER BY t.name ASC
    """)
    fun getTopicsForEntry(entryId: String): Flow<List<Topic>>

    @Query("SELECT EXISTS(SELECT 1 FROM topics WHERE name = :name)")
    suspend fun isTopicNameTaken(name: String): Boolean

    @Transaction
    suspend fun updateEntryTopics(entryId: String, topicIds: List<String>) {
        removeAllTopicsFromEntry(entryId)
        topicIds.forEach { topicId ->
            addTopicToEntry(EntryTopicCrossRef(entryId, topicId))
        }
    }
}