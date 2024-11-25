package org.nehuatl.tachiwin.views

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.guru.fontawesomecomposelib.FaIcon
import com.guru.fontawesomecomposelib.FaIcons
import kotlinx.coroutines.launch
import org.nehuatl.tachiwin.R
import org.nehuatl.tachiwin.audio.AudioRecorder
import org.nehuatl.tachiwin.contextualViewModel
import org.nehuatl.tachiwin.v

@Composable
fun TranscribeItem(
    modifier: Modifier,
    autoSearch: Boolean = true,
    displayWords: Boolean = true,
    onResult: (String) -> Unit,
    onSearch: (String) -> Unit,
) {
    // Strings
    val recordHint = localizedString(R.string.record_to_transcript)
    val recodingHint = localizedString(R.string.recording)
    val errorHint = localizedString(R.string.error_hint)
    val errorBusy = localizedString(R.string.error_busy)
    val processingHint = localizedString(R.string.processing)

    val recorder = AudioRecorder
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val viewModel = contextualViewModel()
    val interaction = remember { MutableInteractionSource() }
    val isPressed by interaction.collectIsPressedAsState()
    val recording = recorder.recording.collectAsState(false)
    val processing = recorder.processing.collectAsState(false)
    val errorMessage = recorder.error.collectAsState(false)
    val enableRetry = remember { mutableStateOf(false) }
    var words by remember { mutableStateOf<String?>(null) }
    val wav = recorder.wavExport.collectAsState(null)
    val entries by viewModel.results.collectAsState(listOf())
    val entry by remember(entries) {
        derivedStateOf {
            if (errorMessage.value == null && words != null) {
                entries.firstOrNull { it.likelihood > 0.5f }
            } else null
        }
    }
    val hint by remember(errorMessage.value, processing.value, recording.value, words) {
        derivedStateOf {
            enableRetry.value = errorMessage.value != null
            if (words != null && autoSearch) viewModel.find(words!!)
            when {
                errorMessage.value != null -> {
                    when (errorMessage.value) {
                        AudioRecorder.STTError.ERROR_BUSY -> errorBusy
                        else -> errorHint
                    }
                }
                processing.value -> processingHint
                recording.value -> recodingHint
                words != null -> {
                    onResult(words!!)
                    words
                }
                else -> recordHint
            }
        }
    }
    var ready by remember { mutableStateOf(false) }

    LaunchedEffect(isPressed) {
        if (isPressed && ready) {
            "startRecording".v()
            recorder.startRecording(context)
        } else {
            "stopRecording".v()
            recorder.stopRecording()?.let {
                words = it
            }
        }
    }

    LaunchedEffect(Unit) {
        recorder.prepare(context)
        ready = true
        "ready to transcribe".v()
    }

    RequestAudioPermission(
        noPermissionContent = {}
    ) {
        Column(
            modifier = modifier
                .background(MaterialTheme.colorScheme.background)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .weight(1f)) {
                FloatingActionButton(
                    onClick = {},
                    shape = CircleShape,
                    modifier = Modifier
                        .size(120.dp)
                        .align(Alignment.Center),
                    containerColor = if (isPressed) {
                        MaterialTheme.colorScheme.secondary
                    } else {
                        MaterialTheme.colorScheme.primary
                    },
                    contentColor = if (isPressed) {
                        MaterialTheme.colorScheme.onSecondary
                    } else {
                        MaterialTheme.colorScheme.onPrimary
                    },
                    elevation = FloatingActionButtonDefaults.elevation(),
                    interactionSource = interaction,
                ) {
                    FaIcon(
                        faIcon = FaIcons.Microphone,
                        size = 72.dp,
                    )
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = hint ?: "",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(32.dp)
                        .height(180.dp)
                        .alpha(if (words == hint) 1.0f else 0.5f),
                )
                if (entry != null) Text(
                    text = entry!!.presentableText ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(8.dp)
                )
                Row(
                    modifier = Modifier
                        .defaultMinSize(minHeight = 48.dp)
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    // Player
                    if (wav.value != null) {
                        AudioPlayer(audioFilePath = wav.value!!)
                    }
                    // Retry Button
                    if (enableRetry.value) {
                        FaIcon(
                            faIcon = FaIcons.Sync,
                            size = 24.dp,
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier
                                .padding(16.dp)
                                .height(48.dp)
                                .alpha(0.5f)
                                .clickable {
                                    scope.launch {
                                        recorder.retryRemoteRecognize()
                                    }
                                },
                        )
                    }
                    if (words != null && words == hint) {
                        FaIcon(
                            faIcon = FaIcons.Copy,
                            size = 24.dp,
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier
                                .padding(16.dp)
                                .height(48.dp)
                                .alpha(0.5f)
                                .clickable {
                                    words?.let {
                                        val clipboard = context
                                            .getSystemService(Context.CLIPBOARD_SERVICE)
                                                as ClipboardManager
                                        val clip = ClipData.newPlainText("tutunaku", it)
                                        clipboard.setPrimaryClip(clip)
                                    }
                                },
                        )
                        FaIcon(
                            faIcon = FaIcons.Search,
                            size = 24.dp,
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier
                                .padding(16.dp)
                                .height(48.dp)
                                .alpha(0.5f)
                                .clickable {
                                    words?.let { onSearch(it) }
                                },
                        )
                    }
                }
            }
        }
    }
}