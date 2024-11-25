package org.nehuatl.tachiwin.models

data class DownloadProgress(
    val progress: Double,
    val size: Long,
    val downloaded: Long,
) {
    companion object {
        fun zero() = DownloadProgress(
            progress = 0.0,
            size = 0,
            downloaded = 0,
        )
    }
}
