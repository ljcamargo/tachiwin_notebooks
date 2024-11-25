package org.nehuatl.tachiwin.viewmodels

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import androidx.core.content.getSystemService
import androidx.core.database.getLongOrNull
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.nehuatl.tachiwin.Constants
import org.nehuatl.tachiwin.core.Preferences
import org.nehuatl.tachiwin.models.DownloadProgress
import org.nehuatl.tachiwin.models.Downloadable
import org.nehuatl.tachiwin.models.Downloadable.Companion.Downloaded
import org.nehuatl.tachiwin.models.Downloadable.Companion.Downloading
import org.nehuatl.tachiwin.models.Downloadable.Companion.Corrupted
import org.nehuatl.tachiwin.models.Downloadable.Companion.Error
import org.nehuatl.tachiwin.models.Downloadable.Companion.NotDownloaded
import org.nehuatl.tachiwin.models.ModelFile
import org.nehuatl.tachiwin.network.HfClient
import org.nehuatl.tachiwin.v
import java.io.File

class DownloadViewModel(context: Context): ViewModel(), KoinComponent {

    private val preferences: Preferences by inject()
    private val viewModelJob = SupervisorJob()
    private val mainScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private val heavyScope = CoroutineScope(Dispatchers.IO + viewModelJob)
    private val downloadManager = context.getSystemService<DownloadManager>()!!
    private val extFilesDir = context.getExternalFilesDir(null)

    val modelFile = MutableStateFlow<ModelFile?>(null)
    val downloadStatus = MutableStateFlow<Downloadable.Companion.Status?>(null)
    val downloadError = MutableStateFlow<String?>(null)
    val downloadProgress = MutableStateFlow<DownloadProgress?>(null)

    fun checkModelState() = checkModelState(model)

    private fun checkModelState(item: Downloadable) = mainScope.launch {
        val state = if (item.destination.exists()) {
            val size = item.destination.length()
            val storedModelFile = preferences.modelFile
            if (storedModelFile != null && storedModelFile.size == size) {
                modelFile.emit(storedModelFile)
                Downloaded(item)
            } else {
                modelFile.emit(ModelFile.from(item.destination))
                Corrupted(item)
            }
        } else NotDownloaded
        downloadStatus.emit(state)
    }

    fun deleteModel() = mainScope.launch {
        model.destination.delete()
        downloadStatus.emit(NotDownloaded)
        modelFile.emit(null)
        preferences.modelFile = null
    }

    private suspend fun getModelMetadata(item: Downloadable): ModelFile {
        val url = item.source
        val metadata = HfClient.fetchMetadata(url.toString(), mainScope)
        return ModelFile(
            name = item.name,
            source = item.source.toString(),
            checksum = metadata["ETag"] ?: "",
            size = metadata["Content-Length"]?.toLongOrNull() ?: 0,
            version = metadata["Last-Modified"] ?: "",
        )
    }

    fun downloadModel(item: Downloadable, title: String, descr: String) = heavyScope.launch {
        deleteModel()
        downloadError.emit(null)
        val request = DownloadManager.Request(item.source).apply {
            setTitle(title)
            setDescription(descr)
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
            setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI)
            setDestinationUri(item.destination.toUri())
        }
        preferences.modelFile = getModelMetadata(item)
        val id = downloadManager.enqueue(request)
        val status = Downloading(id)
        downloadStatus.emit(status)
        delay(2000L)
        checkDownloadProgress(status, item)
    }

    private suspend fun checkDownloadProgress(result: Downloading, item: Downloadable) {
        while (downloadStatus.value is Downloading) {
            "checking progress".v()
            val cursor = downloadManager.query(DownloadManager.Query().setFilterById(result.id))
                ?: return run {
                    downloadStatus.emit(Error("Download query returned null"))
                }
            if (!cursor.moveToFirst() || cursor.count < 1) {
                cursor.close()
                downloadStatus.emit(Error("Cursor moveToFirst() failed or download canceled"))
            }
            val statusIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
            val downloadState = cursor.getInt(statusIndex)
            when (downloadState) {
                DownloadManager.STATUS_FAILED -> {
                    val reasonIndex = cursor.getColumnIndex(DownloadManager.COLUMN_REASON)
                    val reason = cursor.getInt(reasonIndex)
                    cursor.close()
                    downloadError.emit(reason.toString())
                    downloadProgress.emit(null)
                    downloadStatus.emit(Error("Download failed: reason code $reason"))
                    "error downloading $reason".v()
                }
                DownloadManager.STATUS_SUCCESSFUL -> {
                    cursor.close()
                    downloadProgress.emit(null)
                    downloadStatus.emit(Downloaded(item))
                    modelFile.emit(ModelFile.from(item.destination))
                    "download success".v()
                }
                DownloadManager.STATUS_RUNNING, DownloadManager.STATUS_PENDING -> {
                    val pix = cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)
                    val tix = cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)
                    val soFar = cursor.getLongOrNull(pix) ?: 0
                    val total = cursor.getLongOrNull(tix) ?: 1
                    cursor.close()
                    val progress = (soFar * 1.0) / total
                    downloadProgress.emit(DownloadProgress(progress, total, soFar))
                    delay(1000L)
                    "download progress $progress".v()
                }
            }
        }
    }

    val model = Downloadable(
        name = Constants.STT_MODEL_NAME,
        source = Uri.parse(Constants.STT_MODEL_URL),
        destination = File(extFilesDir, Constants.STT_MODEL_FILENAME)
    )

}