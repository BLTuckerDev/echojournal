package dev.bltucker.echojournal.common.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.bltucker.echojournal.common.Mood
import java.time.Instant
import java.util.UUID

@Entity(tableName = "journal_entries")
data class JournalEntry(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),

    val title: String,
    val description: String,
    val createdAt: Instant = Instant.now(),

    val audioFilePath: String,
    val durationSeconds: Int,
    val transcription: String?,

    val mood: Mood,
)
