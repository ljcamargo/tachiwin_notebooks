package org.nehuatl.tachiwin.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalInspectionMode
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import org.nehuatl.tachiwin.contextualViewModel

@Composable
fun TachiwinTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val viewModel = contextualViewModel()
    val scheme by viewModel.schemeP.collectAsState(ThemeSpecs.Default)
    val preview: Boolean = LocalInspectionMode.current
    if (!preview) {
        val systemUiController = rememberSystemUiController()
        systemUiController.setStatusBarColor(scheme.background)
    }

    LaunchedEffect(Unit) { viewModel.refreshTheme(darkTheme) }

    MaterialTheme(
        colorScheme = scheme,
        typography = Typography,
        content = content
    )
}