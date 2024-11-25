package org.nehuatl.tachiwin.views

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.guru.fontawesomecomposelib.FaIcon
import com.guru.fontawesomecomposelib.FaIcons
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import me.onebone.toolbar.CollapsingToolbarScaffold
import me.onebone.toolbar.ScrollStrategy
import me.onebone.toolbar.rememberCollapsingToolbarScaffoldState
import org.koin.androidx.compose.get
import org.nehuatl.tachiwin.Constants
import org.nehuatl.tachiwin.LlamaInstruct
import org.nehuatl.tachiwin.R
import org.nehuatl.tachiwin.core.Preferences
import org.nehuatl.tachiwin.destinations.TranscribeDestination
import org.nehuatl.tachiwin.ui.theme.TachiwinTheme
import org.nehuatl.tachiwin.viewmodels.DownloadViewModel
import org.nehuatl.tachiwin.viewmodels.MainViewModel
import org.nehuatl.tachiwin.models.Downloadable.Companion.NotDownloaded
import org.nehuatl.tachiwin.models.Downloadable.Companion.Downloaded

@Destination
@Composable
fun Translate(
    navigation: NavController,
    recipient: ResultRecipient<TranscribeDestination, String?>?= null,
) {

    val viewModel = get<MainViewModel>()
    val downloader = get<DownloadViewModel>()
    val context = LocalContext.current
    val scrollState = rememberCollapsingToolbarScaffoldState()
    val systemUiController = rememberSystemUiController()
    val collapsedFraction by remember { derivedStateOf { scrollState.toolbarState.progress } }
    val downloadStatus by downloader.downloadStatus.collectAsState(NotDownloaded)
    val modelState by viewModel.modelState.collectAsState(MainViewModel.UIState.Idle)
    val mode by viewModel.inferenceMode.collectAsState(Preferences.InferenceMode.REMOTE)
    val predictedText by viewModel.predictedText.collectAsState()
    var showConfirmReportCurrent by remember { mutableStateOf(false) }
    val reportFinished by viewModel.reportFinished.collectAsState(null)
    val dictionary by viewModel.dictionary.observeAsState()
    val text by remember(predictedText) {
        derivedStateOf {
            LlamaInstruct.untokenedText(predictedText)
        }
    }
    val inferenceReady by remember {
        derivedStateOf {
            (if (mode == Preferences.InferenceMode.LOCAL) (downloadStatus is Downloaded) else true)
                    && modelState == MainViewModel.UIState.Idle
        }
    }

    recipient?.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> Unit
            is NavResult.Value -> result.value?.let { viewModel.find(it) }
        }
    }

    fun clipboardCopy() {
        val dictionaryName = dictionary?.shortName ?: ""
        val clipboard = context
            .getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(dictionaryName, text)
        clipboard.setPrimaryClip(clip)
    }

    LaunchedEffect(Unit) {
        downloader.checkModelState()
    }

    LaunchedEffect(reportFinished) {
        if (reportFinished == true) {
            Toast.makeText(context, R.string.report_sent, Toast.LENGTH_LONG).show()
        }
    }

    TachiwinTheme {
        if (showConfirmReportCurrent) {
            AlertDialog(
                onDismissRequest = { showConfirmReportCurrent = false },
                title = { Text(stringResource(R.string.report)) },
                text = { Text(stringResource(R.string.report_rationale)) },
                confirmButton = {
                    Button(onClick = {
                        viewModel.reportTranslation()
                        showConfirmReportCurrent = false
                    }) {
                        Text(stringResource(R.string.report_btn))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showConfirmReportCurrent = false }) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            )
        }
        if (scrollState.offsetY > (scrollState.toolbarState.minHeight * -1)) {
            systemUiController.setStatusBarColor(MaterialTheme.colorScheme.primary)
        } else {
            systemUiController.setStatusBarColor(MaterialTheme.colorScheme.primary)
        }
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter,
        ) {
            CollapsingToolbarScaffold(
                modifier = Modifier.fillMaxWidth(),
                state = scrollState,
                scrollStrategy = ScrollStrategy.EnterAlways,
                toolbar = {
                    Header(
                        modifier = Modifier.parallax(0.5f),
                        navigation = navigation,
                        progress = collapsedFraction,
                        model = viewModel
                    ) {
                        TranslateInput(
                            modifier = Modifier.fillMaxWidth(),
                            model = viewModel
                        )
                    }
                }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                ) {
                    when {
                        // Request Local or Remote Inference Mode
                        (mode == null) -> {
                            val inferenceModes = listOf(
                                localizedString(R.string.inference_mode_local),
                                localizedString(R.string.inference_mode_remote)
                            )
                            val inferenceModeRational = listOf(
                                localizedString(R.string.inference_mode_local_rationale),
                                localizedString(R.string.inference_mode_remote_rationale)
                            )
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Text(
                                    text = localizedString(R.string.select_inference_mode),
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.onBackground,
                                )
                                Preferences.InferenceMode.entries.forEachIndexed { i, it ->
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.surfaceContainer,
                                            contentColor = MaterialTheme.colorScheme.onSurface,
                                        ),
                                        elevation = CardDefaults.cardElevation(
                                            defaultElevation = 4.dp
                                        ),
                                        onClick = { viewModel.setInferenceMode(it) },
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(12.dp),
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Text(
                                                text = inferenceModes[i],
                                                style = MaterialTheme.typography.titleMedium,
                                                color = MaterialTheme.colorScheme.onBackground
                                            )
                                            Text(
                                                text = inferenceModeRational[i],
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onBackground
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = localizedString(R.string.change_later),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.outline,
                                )
                            }
                        }
                        // Is Remote and Model Is not Downloaded
                        (
                                downloadStatus != null
                                        && downloadStatus !is Downloaded
                                        && mode == Preferences.InferenceMode.LOCAL
                                ) -> DownloadWidget(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 16.dp),
                            status = downloadStatus!!,
                            item = viewModel.model
                        )
                        // Inference is Local and Loading
                        (
                                modelState == MainViewModel.UIState.Loading
                                        && mode == Preferences.InferenceMode.LOCAL
                                ) -> LoadingModelItem()
                        // Any mode and is streaming or idle with content
                        (
                                modelState == MainViewModel.UIState.Streaming ||
                                        modelState == MainViewModel.UIState.Idle && text.isNotEmpty()
                                ) -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = text,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onBackground,
                                )
                                if (modelState == MainViewModel.UIState.Streaming) {
                                    Text(
                                        text = localizedString(R.string.translating),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.outline,
                                    )
                                }
                                if (
                                    text.isNotEmpty() && modelState == MainViewModel.UIState.Idle
                                ) Row(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                ) {
                                    IconButton(
                                        onClick = { viewModel.translate() }
                                    ) {
                                        FaIcon(
                                            faIcon = FaIcons.Sync,
                                            size = 20.dp,
                                            tint = MaterialTheme.colorScheme.onBackground,
                                            modifier = Modifier.alpha(0.5f)
                                        )
                                    }
                                    IconButton(
                                        onClick = { clipboardCopy() }
                                    ) {
                                        FaIcon(
                                            faIcon = FaIcons.Copy,
                                            size = 20.dp,
                                            tint = MaterialTheme.colorScheme.onBackground,
                                            modifier = Modifier.alpha(0.5f)
                                        )
                                    }
                                    IconButton(
                                        onClick = { showConfirmReportCurrent = true }
                                    ) {
                                        FaIcon(
                                            faIcon = FaIcons.ThumbsDown,
                                            size = 20.dp,
                                            tint = MaterialTheme.colorScheme.onBackground,
                                            modifier = Modifier.alpha(0.5f)
                                        )
                                    }
                                }
                            }
                        }
                        (inferenceReady) -> {
                            Text(
                                modifier = Modifier.padding(12.dp),
                                text = localizedString(
                                    if (mode == Preferences.InferenceMode.LOCAL) {
                                        R.string.inference_ready_local
                                    } else {
                                        R.string.inference_ready_remote
                                    }
                                ),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline,
                            )
                        }
                        else -> Unit
                    }
                }
            }
            if (Constants.SHOW_ADS) BannerAd()
        }
    }

}