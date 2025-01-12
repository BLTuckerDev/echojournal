package dev.bltucker.echojournal.settings

import dev.bltucker.echojournal.common.Mood
import dev.bltucker.echojournal.common.room.Topic

data class SettingsModel(
    val defaultMood: Mood? = null,
    val availableTopics: List<Topic> = emptyList(),
    val defaultTopics: List<Topic> = emptyList(),
    val isInTopicEditMode: Boolean = false,
    val editModeText: String = "",
)