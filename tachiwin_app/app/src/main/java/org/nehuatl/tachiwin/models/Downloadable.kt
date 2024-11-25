package org.nehuatl.tachiwin.models

import android.net.Uri
import java.io.File

data class Downloadable(val name: String, val source: Uri, val destination: File) {
    companion object {
        sealed interface Status
        data object NotDownloaded: Status
        data class Downloading(val id: Long): Status
        data class Downloaded(val downloadable: Downloadable): Status
        data class Corrupted(val downloadable: Downloadable): Status
        data class Error(val message: String): Status
    }
}
