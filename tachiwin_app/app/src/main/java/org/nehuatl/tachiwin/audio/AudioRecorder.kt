@file:OptIn(DelicateCoroutinesApi::class)

package org.nehuatl.tachiwin.audio

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import org.nehuatl.tachiwin.Constants.HF_API_KEY
import org.nehuatl.tachiwin.Constants.STT_API_ENDPOINT
import org.nehuatl.tachiwin.v
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object AudioRecorder {

    private const val STT_REMOTE = true
    private const val STT_MODEL = "wav2vec2_to.ptl"
    private const val SAMPLE_RATE = 16000
    private const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
    private const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
    private const val BUFFER_SIZE = 4096
    private const val MAX_LENGTH = 11
    private const val BYTE_RECORDING_LENGTH = SAMPLE_RATE * MAX_LENGTH

    private var outputStream: FileOutputStream? = null
    private var wavFile: File? = null
    private var audioRecord: AudioRecord? = null
    private var mediaRecorder: MediaRecorder? = null
    private var isRecording = false
    private var job: Job? = null
    private var shortsRead = 0L

    val recording = MutableStateFlow(false)
    val processing = MutableStateFlow(false)
    val error = MutableStateFlow<STTError?>(null)
    val wavExport = MutableStateFlow<String?>(null)

    enum class STTError {
        ERROR_RECORDING,
        ERROR_PROCESSING,
        ERROR_BUSY,
        ERROR_LOADING,
    }

    suspend fun prepare(context: Context) {
        if (STT_REMOTE) return
        /*System.loadLibrary("torchvision_ops")
        System.loadLibrary("pytorch_jni")
        System.loadLibrary("fbjni")*/
        withContext(Dispatchers.IO) {
            val path = copyAssetToLocalFile(context, STT_MODEL)
            withContext(Dispatchers.Main) {
                "path: $path".v()
                //module = Module.load(path)
                //module = LiteModuleLoader.load(path)
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun startRecording(context: Context) {
        try {
            error.tryEmit(null)
            recording.tryEmit(true)
            mediaRecorder = MediaRecorder()
            wavFile = null
            wavExport.tryEmit(null)
            wavFile = File.createTempFile("temp", ".wav", context.cacheDir)
            mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
            mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB)
            mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            mediaRecorder?.setOutputFile(wavFile!!.absolutePath)
            mediaRecorder?.prepare()
            mediaRecorder?.start()
            isRecording = true
        } catch (e: IOException) {
            e.printStackTrace()
            error.tryEmit(STTError.ERROR_RECORDING)
            recording.tryEmit(false)
        }
    }

    suspend fun stopRecording(): String? {
        if (!isRecording) return let {
            recording.tryEmit(false)
            null
        }
        mediaRecorder?.stop()
        mediaRecorder?.release()
        mediaRecorder = null
        isRecording = false
        recording.tryEmit(false)
        val file = wavFile ?: return null
        wavExport.tryEmit(file.absolutePath)
        return withContext(Dispatchers.IO) {
            recognizeRemote(file)?.textFromJson()
        }
    }

    suspend fun retryRemoteRecognize(): String? {
        recording.tryEmit(false)
        val file = wavFile ?: return null
        processing.tryEmit(true)
        error.tryEmit(null)
        "will process audio again $file".v()
        return withContext(Dispatchers.IO) {
            recognizeRemote(file)?.textFromJson()
        }
    }

    @SuppressLint("MissingPermission")
    fun startRecording2(context: Context) {
        if (isRecording) return
        val bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT)
        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            SAMPLE_RATE,
            CHANNEL_CONFIG,
            AUDIO_FORMAT,
            BUFFER_SIZE
        )
        wavFile = File.createTempFile("temp", ".wav", context.cacheDir)
        "tempFile: ${wavFile?.absoluteFile}".v()
        outputStream = FileOutputStream(wavFile!!)
        val audioData = ByteArray(bufferSize)
        audioRecord?.startRecording()
        isRecording = true
        job = GlobalScope.launch(Dispatchers.IO) {
            while (isRecording) {
                val bytes = audioRecord?.read(audioData, 0, bufferSize) ?: 0
                shortsRead += bytes
                outputStream?.write(audioData, 0, bytes)
                withContext(Dispatchers.Main) {
                    if (bytes > 0) "Audio data read: $bytes bytes".v()
                }
                if (shortsRead >= BYTE_RECORDING_LENGTH) {
                    isRecording = false
                    audioRecord?.stop()
                    audioRecord?.release()
                }
            }
        }
    }

    private suspend fun copyAssetToLocalFile(context: Context, filename: String): String? {
        val targetFile = File(context.filesDir, filename)
        if (!targetFile.exists()) {
            try {
                withContext(Dispatchers.IO) {
                    context.assets.open(filename).use { inputStream ->
                        FileOutputStream(targetFile).use { outputStream ->
                            val buffer = ByteArray(4096)
                            var bytesRead: Int
                            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                                outputStream.write(buffer, 0, bytesRead)
                            }
                            outputStream.flush()
                        }
                    }
                }
            } catch (e: Exception) {
                return null
            }
        }
        return targetFile.absolutePath
    }

    /*private suspend fun recognizeLocal(buffer: FloatArray): String? {
        val tensorBuffer = Tensor.allocateFloatBuffer(BYTE_RECORDING_LENGTH)
        //val doubles = DoubleArray(BYTE_RECORDING_LENGTH) { buffer[it].toDouble() }
        //doubles.forEach { tensorBuffer.put(it.toFloat()) }
        buffer.forEach { tensorBuffer.put(it) }
        val tensor = Tensor.fromBlob(tensorBuffer, longArrayOf(1, BYTE_RECORDING_LENGTH.toLong()))
        return module?.forward(IValue.from(tensor))?.toStr()
    }*/

    private fun recognizeRemote(wavFile: File): String? {
        processing.tryEmit(true)
        val client = OkHttpClient()
        val mediaType = "audio/wav".toMediaTypeOrNull()
        val requestBody = wavFile.readBytes().toRequestBody(mediaType)
        val request = Request.Builder()
            .url(STT_API_ENDPOINT)
            .addHeader("Authorization", "Bearer $HF_API_KEY")
            .post(requestBody)
            .build()
        "Inference Request -> $request".v()
        "Inference BODY -> ${request.body}".v()
        return try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                processing.tryEmit(false)
                response.body?.string() ?: ""
            } else {
                "Inference Error -> Failed to make API request: ${response.code}".v()
                "Inference Error -> ${response.body?.string()}".v()
                processing.tryEmit(false)
                if (response.code == 503) {
                    error.tryEmit(STTError.ERROR_BUSY)
                } else {
                    error.tryEmit(STTError.ERROR_RECORDING)
                }
                error.tryEmit(STTError.ERROR_PROCESSING)
                null
            }
        } catch (e: IOException) {
            error.tryEmit(STTError.ERROR_PROCESSING)
            processing.tryEmit(false)
            e.printStackTrace()
            null
        }
    }

    private fun String.textFromJson(): String? {
        "textFromJson: $this".v()
        return try {
            val json = JSONObject(this)
            json.getString("text")
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
