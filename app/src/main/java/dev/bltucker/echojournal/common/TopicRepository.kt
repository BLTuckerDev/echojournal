package dev.bltucker.echojournal.common

import dev.bltucker.echojournal.common.room.Topic
import dev.bltucker.echojournal.common.room.TopicDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TopicsRepository @Inject constructor(private val topicDao: TopicDao) {

    fun getTopicForEntry(entryId: String): Flow<List<Topic>> = topicDao.getTopicsForEntry(entryId)

    fun getAllTopics(): Flow<List<Topic>> = topicDao.getAllTopics()

    fun getAutoAppliedTopics(): Flow<List<Topic>> = topicDao.getAutoAppliedTopics()

    suspend fun topicNameExists(name: String): Boolean{
        return topicDao.isTopicNameTaken(name)
    }

    suspend fun createTopic(name: String, isDefault: Boolean = false): Topic {
        if (topicDao.isTopicNameTaken(name)) {
            throw IllegalArgumentException("Topic name already exists")
        }

        val topic = Topic(name = name, isDefault = isDefault)
        topicDao.insertTopic(topic)
        return topic
    }

    suspend fun deleteTopic(topic: Topic) {
        topicDao.deleteTopic(topic)
    }


    suspend fun toggleDefaultStatus(topic: Topic) {
        val updatedTopic = topic.copy(isDefault = !topic.isDefault)
        topicDao.updateTopic(updatedTopic)
    }

    suspend fun getTopicById(id: String): Topic? {
        return topicDao.getTopicById(id)
    }
}