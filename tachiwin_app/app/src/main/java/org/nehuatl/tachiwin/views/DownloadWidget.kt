package org.nehuatl.tachiwin.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.get
import org.nehuatl.tachiwin.Constants
import org.nehuatl.tachiwin.R
import org.nehuatl.tachiwin.models.Downloadable
import org.nehuatl.tachiwin.models.Downloadable.Companion.Corrupted
import org.nehuatl.tachiwin.models.Downloadable.Companion.Downloaded
import org.nehuatl.tachiwin.models.Downloadable.Companion.Downloading
import org.nehuatl.tachiwin.models.Downloadable.Companion.Error
import org.nehuatl.tachiwin.models.Downloadable.Companion.NotDownloaded
import org.nehuatl.tachiwin.toGigs
import org.nehuatl.tachiwin.viewmodels.DownloadViewModel


@Composable
fun DownloadWidget(
    modifier: Modifier = Modifier,
    status: Downloadable.Companion.Status,
    item: Downloadable
) {
    val downloader = get<DownloadViewModel>()
    val downloadProgress by downloader.downloadProgress.collectAsState(null)
    val downloadError by downloader.downloadError.collectAsState(null)
    val downloadingModel = localizedString(R.string.downloading_model)
    val downloadingModelDescr = localizedString(R.string.downloading_model_descr) + " ${item.name}"
    val downloadErrorRationales = mapOf(
        "1006" to R.string.no_space
    )

    @Composable
    fun downloadButton() = Button(
        onClick = {
            downloader.downloadModel(item, downloadingModel, downloadingModelDescr)
        },
    ) {
        Text(localizedString(R.string.download) + " ${item.name}")
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = localizedString(R.string.download_the_model),
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            style = MaterialTheme.typography.titleLarge,
        )
        Text(
            text = localizedString(R.string.download_rationale),
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            style = MaterialTheme.typography.bodyMedium,
        )
        when (status) {
            is Downloading -> {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(36.dp)
                    )
                    downloadProgress?.let {
                        Text(
                            text = "${(it.progress * 100.0).toInt()}%",
                            style = MaterialTheme.typography.titleLarge,
                        )
                    }
                }
                downloadProgress?.let {
                    Text(
                        text = localizedString(R.string.downloading),
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text = "${it.downloaded.toGigs()}/${it.size.toGigs()}",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
            is NotDownloaded -> downloadButton()
            is Error -> {
                downloadButton()
                Text(
                    text = localizedString(R.string.error_downloading),
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    text = localizedString(R.string.error_retry),
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            is Corrupted -> {
                Text(
                    text = localizedString(R.string.corrupted_model),
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    text = localizedString(R.string.corrupted_model_rationale),
                    style = MaterialTheme.typography.bodyMedium,
                )
                downloadButton()
            }
            is Downloaded -> Unit
        }
        if (downloadError != null) {
            Text(
                modifier = Modifier.padding(vertical = 8.dp),
                text = localizedString(
                    downloadErrorRationales.getOrDefault(
                        downloadError!!,
                        R.string.error_downloading
                    ),
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
            )
        }
        if (Constants.SHOW_ADS) BannerAd()
    }


}