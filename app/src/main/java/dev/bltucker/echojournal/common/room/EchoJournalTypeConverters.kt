package dev.bltucker.echojournal.common.room

import androidx.room.TypeConverter
import androidx.room.TypeConverters
import dev.bltucker.echojournal.common.Mood
import java.time.Instant

@TypeConverters
class EchoJournalTypeConverters {

    @TypeConverter
    fun fromTimestamp(value: Long?): Instant? {
        return value?.let { Instant.ofEpochMilli(it) }
    }

    @TypeConverter
    fun instantToTimestamp(instant: Instant?): Long? {
        return instant?.toEpochMilli()
    }

    @TypeConverter
    fun fromMood(mood: Mood): String {
        return mood.name
    }

    @TypeConverter
    fun toMood(value: String): Mood {
        return Mood.fromString(value)
    }
}