package org.nehuatl.tachiwin.views

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat


@Composable
fun RequestAudioPermission(
    noPermissionContent: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val permission = Manifest.permission.RECORD_AUDIO
    val check = ContextCompat.checkSelfPermission(context, permission)
    var granted = remember(check) { check == PackageManager.PERMISSION_GRANTED }
    var checked: Boolean? = remember() { null }
    val request = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        granted = isGranted
        checked = isGranted
    }
    if (!granted) SideEffect {
        request.launch(Manifest.permission.RECORD_AUDIO)
    } else {
        content()
    }
    if (checked == false) {
        noPermissionContent()
    }
}