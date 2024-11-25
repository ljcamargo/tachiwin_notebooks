@file:OptIn(ExperimentalMaterial3Api::class)

package org.nehuatl.tachiwin.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.guru.fontawesomecomposelib.FaIcon
import com.guru.fontawesomecomposelib.FaIcons
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.utils.rememberDestinationsNavigator
import org.koin.androidx.compose.get
import org.nehuatl.tachiwin.BuildConfig
import org.nehuatl.tachiwin.R
import org.nehuatl.tachiwin.core.Preferences
import org.nehuatl.tachiwin.destinations.ReaderScreenDestination
import org.nehuatl.tachiwin.ui.theme.TachiwinTheme
import org.nehuatl.tachiwin.v
import org.nehuatl.tachiwin.viewmodels.MainViewModel

@RootNavGraph
@Destination
@Composable
fun Settings(
    navigation: NavController,
) {

    val preferences = get<Preferences>()
    val viewModel = get<MainViewModel>()
    val dialogLang = remember { mutableStateOf(false) }
    val dialogLightMode = remember { mutableStateOf(false) }
    val dialogInferenceMode = remember { mutableStateOf(false) }
    val navigator = navigation.rememberDestinationsNavigator()
    val inferenceMode by viewModel.inferenceMode.collectAsState(Preferences.InferenceMode.LOCAL)
    val language by viewModel.uiLanguage.collectAsState(preferences.currentLanguage())
    val lightMode by viewModel.lightMode.collectAsState(Preferences.LightMode.AUTO)
    val inferenceModes = listOf(
        localizedString(R.string.inference_mode_local),
        localizedString(R.string.inference_mode_remote)
    )
    val languages = preferences.availableLanguages.map { tokenizedLocalizedText(it) }
    val lightModes = listOf(
        localizedString(R.string.light_mode_auto),
        localizedString(R.string.light_mode_light),
        localizedString(R.string.light_mode_dark)
    )

    LaunchedEffect(Unit) {
        "languages $languages".v()
    }

    if (dialogLang.value) {
        ListSelectorDialog(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .fillMaxHeight(0.3f),
            showDialog = dialogLang,
            title = R.string.select_lang,
            options = languages,
            selectedOption = language,
            nullOption = localizedString(R.string.device),
            onOptionSelected = {
                "selected language $it".v()
                viewModel.setUILanguageIndex(it)
            }
        )
    }

    if (dialogLightMode.value) {
        ListSelectorDialog(
            modifier = Modifier.fillMaxWidth(0.7f),
            showDialog = dialogLightMode,
            title = R.string.light_mode,
            options = lightModes,
            onOptionSelected = {
                viewModel.setLightMode(Preferences.LightMode.entries[it])
            },
            selectedOption = lightModes[lightMode.ordinal]
        )
    }
    if (dialogInferenceMode.value) {
        ListSelectorDialog(
            modifier = Modifier.fillMaxWidth(0.7f),
            showDialog = dialogInferenceMode,
            title = R.string.inference_mode,
            options = inferenceModes,
            onOptionSelected = {
                viewModel.setInferenceMode(Preferences.InferenceMode.entries[it])
            },
            selectedOption = inferenceModes[inferenceMode?.ordinal ?: 0]
        )
    }

    TachiwinTheme {
        Scaffold(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .navigationBarsPadding()
                .fillMaxSize(),
            contentWindowInsets = WindowInsets(top = 0.dp),
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = localizedString(R.string.settings),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            navigator.navigateUp()
                        }) {
                            FaIcon(
                                faIcon = FaIcons.ArrowLeft,
                                tint = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                )
            }
        ) { paddings ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .consumeWindowInsets(paddings)
                    .padding(top = 80.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = localizedString(R.string.ui_language),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.clickable(onClick = { dialogLang.value = true })
                        )
                        TextButton(
                            onClick = { dialogLang.value = true },
                        ) {
                            Text(
                                text = language?.let { tokenizedLocalizedText(it) } ?: "",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                }
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = localizedString(R.string.inference_mode),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            /*inferenceMode?.ordinal?.let {
                                Text(
                                    text = inferenceModeRational[it],
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }*/
                        }
                        TextButton(
                            onClick = { dialogInferenceMode.value = true },
                        ) {
                            Text(
                                text = inferenceMode?.ordinal?.let {
                                    inferenceModes[it]
                                } ?: run {
                                    localizedString(R.string.select)
                                },
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = localizedString(R.string.light_mode),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                        TextButton(
                            onClick = { dialogLightMode.value = true },
                        ) {
                            Text(
                                text = lightModes[lightMode.ordinal],
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .clickable {
                                navigator.navigate(
                                    ReaderScreenDestination("about.md")
                                )
                            },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = localizedString(R.string.about_app),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier
                        )
                    }
                }
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = localizedString(R.string.app_version)
                                    + " "
                                    + BuildConfig.VERSION_NAME,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.outline,
                            modifier = Modifier
                        )
                    }
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_logo),
                    contentDescription = stringResource(R.string.app_name),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    Settings(
        navigation = rememberNavController()
    )
}