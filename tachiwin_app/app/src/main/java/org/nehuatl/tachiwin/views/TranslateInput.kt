package org.nehuatl.tachiwin.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.guru.fontawesomecomposelib.FaIcon
import com.guru.fontawesomecomposelib.FaIcons
import org.nehuatl.tachiwin.R
import org.nehuatl.tachiwin.core.Preferences
import org.nehuatl.tachiwin.viewmodels.MainViewModel

@Composable
fun TranslateInput(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    model: MainViewModel,
) {
    val focusManager = LocalFocusManager.current
    val modelState by model.modelState.collectAsState(MainViewModel.UIState.Idle)
    val translateQuery by model.translateQuery.collectAsState()
    val dictionary by model.dictionary.observeAsState()
    val inferenceMode by model.inferenceMode.collectAsState(Preferences.InferenceMode.LOCAL)
    val showDictionaryDialog = remember { mutableStateOf(false) }
    val showRecordDialog = remember { mutableStateOf(false) }
    val showInferenceModeDialog = remember { mutableStateOf(false) }
    val inferenceModes = listOf(
        localizedString(R.string.inference_mode_local),
        localizedString(R.string.inference_mode_remote)
    )

    if (showInferenceModeDialog.value) ListSelectorDialog(
        modifier = Modifier
            .fillMaxWidth(0.7f),
        showDialog = showInferenceModeDialog,
        title = R.string.inference_mode,
        options = inferenceModes,
        onOptionSelected = {
            model.setInferenceMode(Preferences.InferenceMode.entries[it])
        },
        selectedOption = inferenceModes[inferenceMode?.ordinal ?: 0]
    )

    if (showDictionaryDialog.value) DictionaryDialog(
        showDialog = showDictionaryDialog,
        viewModel = model
    )

    if (showRecordDialog.value) RecorderDialog(
        showDialog = showRecordDialog
    ) {
        showRecordDialog.value = false
        model.writeTranslate(it)
    }

    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.primary)
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Card(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primary)
                .fillMaxWidth(),
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            TextField(
                value = translateQuery.text,
                label = {
                    Text(
                        localizedString(R.string.translate_hint),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                },
                onValueChange = { model.writeTranslate(it) },
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onBackground,
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        focusManager.clearFocus()
                        model.translate()
                   },
                ),
                trailingIcon = {
                    when {
                        modelState != MainViewModel.UIState.Idle ->  IconButton(
                            modifier = Modifier,
                            onClick = { model.abort() }
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                        translateQuery.text.isNotEmpty() -> IconButton(
                            enabled = enabled,
                            modifier = Modifier.alpha(if (enabled) 1f else 0.35f),
                            onClick = { if (enabled) model.translate() }
                        ) {
                            FaIcon(
                                faIcon = FaIcons.ArrowRight,
                                tint = MaterialTheme.colorScheme.primary,
                            )
                        }
                        else -> IconButton(
                            modifier = Modifier,
                            onClick = {
                                showRecordDialog.value = true
                            }
                        ) {
                            FaIcon(
                                faIcon = FaIcons.Microphone,
                                tint = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RectangleShape,
                colors = TextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                )
            )
        }
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                modifier = Modifier.align(Alignment.CenterEnd),
                onClick = {
                    showInferenceModeDialog.value = true
                }
            ) {
                Icon(
                    painter = painterResource(
                        if (inferenceMode == Preferences.InferenceMode.LOCAL)
                            R.drawable.ic_offline
                        else
                            R.drawable.ic_online
                    ),
                    contentDescription = localizedString(R.string.select_inference_mode),
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .align(Alignment.Center)
                    .clickable(true) {
                        showDictionaryDialog.value = showDictionaryDialog.value.not()
                    }
            ) {
                Text(
                    text = if (dictionary != null) {
                        localizedText(dictionary!!.shortName)
                    } else {
                        localizedString(R.string.app_description)
                    },
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.titleLarge,
                )
                Spacer(modifier = Modifier.width(8.dp))
                FaIcon(
                    faIcon = FaIcons.Sync,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    size = 18.dp,
                )
            }
        }

    }
}