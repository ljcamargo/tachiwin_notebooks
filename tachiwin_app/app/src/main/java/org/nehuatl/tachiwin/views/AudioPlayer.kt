package org.nehuatl.tachiwin.views

import android.media.MediaPlayer
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.guru.fontawesomecomposelib.FaIcon
import com.guru.fontawesomecomposelib.FaIcons

@Composable
fun AudioPlayer(
    modifier: Modifier = Modifier,
    audioFilePath: String,
) {

    var isPlaying by remember { mutableStateOf(false) }
    val mediaPlayer = remember { MediaPlayer() }

    LaunchedEffect(Unit) {
        mediaPlayer.setOnCompletionListener {
            isPlaying = false
        }
    }

    FaIcon(
        faIcon = if (isPlaying) FaIcons.Stop else FaIcons.Play,
        size = 24.dp,
        tint = MaterialTheme.colorScheme.onBackground,
        modifier = modifier
            .padding(16.dp)
            .height(48.dp)
            .alpha(0.5f)
            .clickable {
                if (isPlaying) {
                    // Stop playback
                    mediaPlayer.stop()
                    mediaPlayer.reset()
                } else {
                    // Start playback
                    mediaPlayer.setDataSource(audioFilePath)
                    mediaPlayer.prepare()
                    mediaPlayer.start()
                }
                isPlaying = !isPlaying
            },
    )

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer.release()
        }
    }
}
