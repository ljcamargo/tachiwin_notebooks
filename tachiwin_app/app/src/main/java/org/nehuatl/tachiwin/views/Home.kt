package org.nehuatl.tachiwin.views

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.ramcosta.composedestinations.DestinationsNavHost
import org.koin.androidx.compose.get
import org.nehuatl.tachiwin.Constants
import org.nehuatl.tachiwin.NavGraphs
import org.nehuatl.tachiwin.appCurrentDestinationAsState
import org.nehuatl.tachiwin.core.Preferences
import org.nehuatl.tachiwin.startAppDestination
import org.nehuatl.tachiwin.ui.theme.TachiwinTheme
import org.nehuatl.tachiwin.updateAppLocale
import org.nehuatl.tachiwin.viewmodels.MainViewModel

@Suppress("KotlinConstantConditions")
@Composable
fun Home() {
    val context = LocalContext.current
    val viewModel = get<MainViewModel>()
    val preferences = get<Preferences>()
    val language by viewModel.uiLanguage.collectAsState(preferences.currentLanguage())
    var updatedContext by remember { mutableStateOf(context) }
    val navController = rememberNavController()
    val destination = navController.appCurrentDestinationAsState().value
        ?: if (Constants.STARTING_TAB_INDEX >= 0) {
            (homeTabs.getOrNull(Constants.STARTING_TAB_INDEX) ?: homeTabs[0]).destination
        } else {    
            NavGraphs.root.startAppDestination
        }

    LaunchedEffect(language) {
        language?.let {
            if (!preferences.languageIsExogenous(it)) {
                updatedContext = context.updateAppLocale(it)
            }
        }
    }

    CompositionLocalProvider(LocalContext provides updatedContext) {
        TachiwinTheme {
            Scaffold(
                modifier = Modifier
                    .navigationBarsPadding()
                    .fillMaxSize(),
                contentWindowInsets = WindowInsets(top = 0.dp),
                bottomBar = {
                    if (
                        destination.shouldShowScaffoldElements
                        && Constants.SHOW_BOTTOM_BAR
                    ) BottomBar(navController)
                },
            ) { innerPadding ->
                DestinationsNavHost(
                    navGraph = NavGraphs.root,
                    navController = navController,
                    startRoute = destination,
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
                    dependenciesContainerBuilder = {}
                )
            }
        }
    }

}