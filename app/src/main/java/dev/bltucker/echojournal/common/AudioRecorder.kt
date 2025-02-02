package dev.bltucker.echojournal.common

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioRecorder @Inject constructor() {
    private var audioRecord: AudioRecord? = null
    private var recordingJob: Job? = null
    private var wavFile: File? = null
    private var isPaused = false

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording

    private val audioRecordBufferSize = AudioRecord.getMinBufferSize(
        SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT
    )


    @SuppressLint("MissingPermission")
    fun startRecording(outputFile: File, scope: CoroutineScope) {
        if (_isRecording.value) return

        wavFile = outputFile
        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            SAMPLE_RATE,
            CHANNEL_CONFIG,
            AUDIO_FORMAT,
            audioRecordBufferSize
        )

        // Create WAV file with header
        initializeWavFile(outputFile)

        audioRecord?.startRecording()
        _isRecording.value = true
        isPaused = false

        recordingJob = scope.launch(Dispatchers.IO) {
            writeAudioDataToFile()
        }
    }

    fun pauseRecording() {
        isPaused = true
    }

    fun resumeRecording() {
        isPaused = false
    }

    fun stopRecording() {
        recordingJob?.cancel()
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null
        _isRecording.value = false
        isPaused = false

        // Update WAV header with final file size
        wavFile?.let { updateWavHeader(it) }
        wavFile = null
    }

    private suspend fun writeAudioDataToFile() {
        val buffer = ByteArray(audioRecordBufferSize)
        val outputStream = FileOutputStream(wavFile, true)

        try {
            while (_isRecording.value) {
                if (!isPaused) {
                    val readResult = audioRecord?.read(buffer, 0, audioRecordBufferSize) ?: -1
                    if (readResult > 0) {
                        outputStream.write(buffer, 0, readResult)
                    }
                }
            }
        } finally {
            outputStream.close()
        }
    }

    private fun initializeWavFile(outputFile: File) {
        FileOutputStream(outputFile).use { out ->
            // RIFF header
            writeWavHeader(out, 0) // We'll update the size later
        }
    }

    private fun writeWavHeader(out: FileOutputStream, totalAudioLen: Long) {
        val totalDataLen = totalAudioLen + 36
        val byteRate = (SAMPLE_RATE * 2).toLong()

        val header = ByteBuffer.allocate(44).apply {
            order(ByteOrder.LITTLE_ENDIAN)
            put("RIFF".toByteArray()) // RIFF header
            putInt(totalDataLen.toInt()) // Total file size - 8
            put("WAVE".toByteArray()) // WAVE header
            put("fmt ".toByteArray()) // fmt chunk
            putInt(16) // fmt chunk size
            putShort(1) // Audio format (1 = PCM)
            putShort(1) // Number of channels
            putInt(SAMPLE_RATE) // Sample rate
            putInt(SAMPLE_RATE * 2) // Byte rate
            putShort(2) // Block align
            putShort(16) // Bits per sample
            put("data".toByteArray()) // Data chunk header
            putInt(totalAudioLen.toInt()) // Data chunk size
        }

        out.write(header.array())
    }

    private fun updateWavHeader(wavFile: File) {
        val totalAudioLen = wavFile.length() - 44 // Subtract header size

        RandomAccessFile(wavFile, "rw").use { raf ->
            // Update RIFF chunk size
            raf.seek(4)
            raf.write(intToByteArray((totalAudioLen + 36).toInt()))

            // Update data chunk size
            raf.seek(40)
            raf.write(intToByteArray(totalAudioLen.toInt()))
        }
    }

    private fun intToByteArray(value: Int): ByteArray {
        return ByteBuffer.allocate(4)
            .order(ByteOrder.LITTLE_ENDIAN)
            .putInt(value)
            .array()
    }

    companion object {
        private const val SAMPLE_RATE = 44100
        private const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
        private const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
    }
}