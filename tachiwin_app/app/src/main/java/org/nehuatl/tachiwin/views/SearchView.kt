package org.nehuatl.tachiwin.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.guru.fontawesomecomposelib.FaIcon
import com.guru.fontawesomecomposelib.FaIcons
import com.ramcosta.composedestinations.utils.rememberDestinationsNavigator
import org.nehuatl.tachiwin.R
import org.nehuatl.tachiwin.destinations.TranscribeDestination
import org.nehuatl.tachiwin.viewmodels.MainViewModel

@Composable
fun SearchView(
    modifier: Modifier = Modifier,
    model: MainViewModel,
) {
    val focusManager = LocalFocusManager.current
    val state by model.status.collectAsState(MainViewModel.UpdateState.IDLE)
    val query by model.dictionaryQuery.collectAsState()
    val dictionary by model.dictionary.observeAsState()
    val showDialog = remember { mutableStateOf(false) }
    val showRecordDialog = remember { mutableStateOf(false) }

    if (showDialog.value) DictionaryDialog(
        showDialog = showDialog,
        viewModel = model
    )

    if (showRecordDialog.value) RecorderDialog(
        showDialog = showRecordDialog
    ) {
        showRecordDialog.value = false
        model.find(it)
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
                value = query.text,
                label = {
                    Text(
                        localizedString(R.string.search),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                },
                onValueChange = {
                    model.find(it)
                },
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onBackground,
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = { focusManager.clearFocus() },
                ),
                leadingIcon = {
                    when (state) {
                        MainViewModel.UpdateState.WORKING -> CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.primary,
                        )
                        else -> FaIcon(
                            faIcon = FaIcons.Search,
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                },
                trailingIcon = {
                    /*if (query.text.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                viewModel.find("")
                            }
                        ) {
                            FaIcon(
                                faIcon = FaIcons.Times,
                                tint = MaterialTheme.colorScheme.onPrimary,
                            )
                        }
                    }*/
                    IconButton(
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
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.clickable(true) {
                    showDialog.value = showDialog.value.not()
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