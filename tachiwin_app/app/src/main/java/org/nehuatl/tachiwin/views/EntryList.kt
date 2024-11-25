package org.nehuatl.tachiwin.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.ramcosta.composedestinations.utils.rememberDestinationsNavigator
import kotlinx.coroutines.launch
import org.nehuatl.tachiwin.models.Query
import org.nehuatl.tachiwin.destinations.DetailDestination
import org.nehuatl.tachiwin.ui.extensions.drawVerticalScrollbar
import org.nehuatl.tachiwin.ui.theme.TachiwinTheme
import org.nehuatl.tachiwin.v
import org.nehuatl.tachiwin.viewmodels.MainViewModel


@Composable
fun EntryList(
    modifier: Modifier = Modifier,
    navigation: NavController,
    listState: LazyListState,
    viewModel: MainViewModel
) {
    val query = viewModel.dictionaryQuery.collectAsState(Query.empty())
    val dictionary by viewModel.dictionary.observeAsState()
    val entries by viewModel.results.collectAsState(listOf())
    val loading by viewModel.status.collectAsState(MainViewModel.UpdateState.IDLE)
    val coroutineScope = rememberCoroutineScope()
    val navigator = navigation.rememberDestinationsNavigator()

    LaunchedEffect(loading) {
        coroutineScope.launch {
            when (loading) {
                MainViewModel.UpdateState.RESET -> {
                    listState.scrollToItem(0,0)
                    viewModel.didScroll()
                }
                MainViewModel.UpdateState.WORKING -> "WORKING".v()
                MainViewModel.UpdateState.IDLE -> "IDLE".v()
            }
        }
    }

    Box(
        modifier= modifier,
        contentAlignment = Alignment.Center
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .drawVerticalScrollbar(listState)
        ) {
            items(
                count = entries.size,
                key = {
                    entries[it].key
                },
                itemContent = { index ->
                    val entry = entries[index]
                    EntryListItem(
                        entry,
                        onItemClick = { uid ->
                            viewModel.logger.details()
                            val text = query.value.text
                            val dictionaryId = dictionary?.uid ?: ""
                            navigator.navigate(
                                DetailDestination(
                                    entryId = uid,
                                    dictionaryId = dictionaryId,
                                    query = text.ifBlank { null }
                                )
                            )
                        }
                    )
                }
            )
        }
        when (loading) {
            MainViewModel.UpdateState.WORKING -> CircularProgressIndicator()
            else -> Unit
        }
    }
}

