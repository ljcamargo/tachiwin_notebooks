@file:OptIn(ExperimentalMaterial3Api::class)

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.guru.fontawesomecomposelib.FaIcon
import com.guru.fontawesomecomposelib.FaIcons
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.result.ResultBackNavigator
import kotlinx.coroutines.launch
import org.nehuatl.tachiwin.R
import org.nehuatl.tachiwin.audio.AudioRecorder
import org.nehuatl.tachiwin.contextualViewModel
import org.nehuatl.tachiwin.ui.theme.TachiwinTheme
import org.nehuatl.tachiwin.v

@RootNavGraph
@Destination
@Composable
fun Transcribe(
    navigation: ResultBackNavigator<String?>?= null) {

    val viewModel = contextualViewModel()
    val systemUiController = rememberSystemUiController()

    TachiwinTheme {
        systemUiController.setStatusBarColor(MaterialTheme.colorScheme.background)
        RequestAudioPermission(
            noPermissionContent = { }
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {  },
                        navigationIcon = {
                            IconButton(onClick = { navigation?.navigateBack(null) }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                    )
                },
                content = { padding ->
                    TranscribeItem(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.background)
                            .fillMaxSize()
                            .padding(padding),
                        autoSearch = false,
                        onResult = {

                        },
                        onSearch = {
                            navigation?.navigateBack(it)
                        }
                    )
                }
            )
        }
    }
}

@Preview
@Composable
fun TranscribePreview() {
    TachiwinTheme {
        Transcribe()
    }
}