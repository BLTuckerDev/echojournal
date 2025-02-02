package dev.bltucker.echojournal.home

import dev.bltucker.echojournal.common.Mood
import dev.bltucker.echojournal.common.room.JournalEntry
import dev.bltucker.echojournal.common.room.Topic
import java.time.LocalDate
import java.time.ZoneId
import java.util.concurrent.TimeUnit

data class HomeModel(val entries: List<JournalEntry> = emptyList(),
                     val topics: List<Topic> = emptyList(),
                     val showRecordingBottomSheet: Boolean = false,
                     val recordingState: RecordingState = RecordingState(),
                     val permissionState: PermissionState = PermissionState()
){

    val entriesByDay: Map<DaySection, List<JournalEntryCardState>> = groupEntriesByDay(entries)

    private fun groupEntriesByDay(entries: List<JournalEntry>): Map<DaySection, List<JournalEntryCardState>> {
        val today = LocalDate.now()
        val yesterday = today.minusDays(1)

        return entries
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
                    JournalEntryCardState(
                        title = entry.title,
                        time = entry.createdAt.atZone(ZoneId.systemDefault()).toLocalTime().toString(),
                        description = entry.description,
                        topics = emptyList(), //TODO need topics
                        audioDuration = "${entry.durationSeconds / 60}:${String.format("%02d", entry.durationSeconds % 60)}/${entry.durationSeconds / 60}:${String.format("%02d", entry.durationSeconds % 60)}",
                        isPlaying = false,
                        isDescriptionExpanded = false,
                        mood = entry.mood,
                        audioProgress = 0f
                    )
                }
            }
            .toSortedMap(compareBy { it.sortOrder })
    }
}

data class PermissionState(
    val hasAudioPermission: Boolean = false,
    val shouldShowPermissionRequest: Boolean = false
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