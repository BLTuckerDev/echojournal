package dev.bltucker.echojournal.home

import dev.bltucker.echojournal.common.AudioPlayer
import dev.bltucker.echojournal.common.Mood
import dev.bltucker.echojournal.common.room.JournalEntry
import dev.bltucker.echojournal.common.room.Topic
import java.io.File
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.concurrent.TimeUnit

data class HomeModel(val entries: List<JournalEntry> = emptyList(),
                     val topicsByEntry: Map<String, List<Topic>> = emptyMap(),
                     val topics: List<Topic> = emptyList(),
                     val selectedMoods: Set<Mood> = emptySet(),
                     val selectedTopics: Set<Topic> = emptySet(),
                     val showMoodFilterMenu: Boolean = false,
                     val showTopicFilterMenu: Boolean = false,
                     val showRecordingBottomSheet: Boolean = false,
                     val recordingState: RecordingState = RecordingState(),
                     val permissionState: PermissionState = PermissionState(),
                     val finishedRecordingId: String? = null,
                     val currentPlaybackId: String? = null,
                     val currentPlaybackState: AudioPlayer.PlaybackState = AudioPlayer.PlaybackState.Idle,
                     val playbackProgress: Float = 0f,
){

    val entriesByDay: Map<DaySection, List<JournalEntryCardState>> = groupEntriesByDay(entries)

    private fun groupEntriesByDay(entries: List<JournalEntry>): Map<DaySection, List<JournalEntryCardState>> {
        val today = LocalDate.now()
        val yesterday = today.minusDays(1)
        val formatter = DateTimeFormatter.ofPattern("HH:mm")

        return entries
            .filter { entry ->
                val moodFilterMatches = selectedMoods.isEmpty() || entry.mood in selectedMoods
                val topicFilterMatches = selectedTopics.isEmpty() || topicsByEntry.getOrDefault(entry.id, emptyList()).any { it in selectedTopics }
                moodFilterMatches && topicFilterMatches
            }
            .groupBy { entry ->
                val entryDate = entry.createdAt.atZone(ZoneId.systemDefault()).toLocalDate()
                when (entryDate) {
                    today -> DaySection.Today
                    yesterday -> DaySection.Yesterday
                    else -> DaySection.Date(entryDate)
                }
            }
            .mapValues { (_, entriesForDay) ->
                entriesForDay.map { entry ->
                    val isCurrentlyPlaying = entry.id == currentPlaybackId && currentPlaybackState is AudioPlayer.PlaybackState.Playing
                    val currentProgress = if (isCurrentlyPlaying) playbackProgress else 0f

                    JournalEntryCardState(
                        id = entry.id,
                        title = entry.title,
                        time = entry.createdAt.atZone(ZoneId.systemDefault()).toLocalTime().format(formatter),
                        description = entry.description,
                        topics = topicsByEntry.getOrDefault(entry.id, emptyList()),
                        audioDuration = formatDuration(entry.durationSeconds),
                        isPlaying = isCurrentlyPlaying,
                        isDescriptionExpanded = false,
                        mood = entry.mood,
                        audioProgress = currentProgress
                    )
                }
            }
            .toSortedMap(compareBy { it.sortOrder })
    }

    private fun formatDuration(totalSeconds: Int): String {
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format(Locale.getDefault(), "%d:%02d", minutes, seconds)
    }
}

data class PermissionState(
    val hasAudioPermission: Boolean = false,
    val shouldShowPermissionRequest: Boolean = false,
    val userHasRepeatedlyDenied: Boolean = false
)

sealed class DaySection(val sortOrder: Int) : Comparable<DaySection> {
    data object Today : DaySection(0)
    data object Yesterday : DaySection(1)
    data class Date(val date: LocalDate) : DaySection(2)

    override fun compareTo(other: DaySection): Int = sortOrder.compareTo(other.sortOrder)
}

data class RecordingState(
    val hasStartedRecording: Boolean = false,
    val isRecording: Boolean = false,
    val isPaused: Boolean = false,
    val elapsedSeconds: Int = 0,
    val currentRecordingFile: File? = null
){
    val recordingDuration: String
        get() = formatDuration(elapsedSeconds)

    private fun formatDuration(totalSeconds: Int): String {
        val hours = TimeUnit.SECONDS.toHours(totalSeconds.toLong())
        val minutes = TimeUnit.SECONDS.toMinutes(totalSeconds.toLong()) % 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
}


data class JournalEntryCardState(
    val id: String,
    val title: String,
    val time: String,
    val description: String? = null,
    val topics: List<Topic> = emptyList(),
    val audioProgress: Float = 0f,
    val audioDuration: String,
    val isPlaying: Boolean = false,
    val isDescriptionExpanded: Boolean = false,
    val mood: Mood
)