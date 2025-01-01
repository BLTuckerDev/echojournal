package dev.bltucker.echojournal.common.room

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "topics",
    indices = [Index(value = ["name"], unique = true)]
)
data class Topic(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val isDefault: Boolean = false
)