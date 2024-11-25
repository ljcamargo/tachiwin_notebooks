package org.nehuatl.tachiwin.views

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.guru.fontawesomecomposelib.FaIcon
import com.guru.fontawesomecomposelib.FaIcons
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import kotlinx.coroutines.launch
import me.onebone.toolbar.*
import org.nehuatl.tachiwin.Constants
import org.nehuatl.tachiwin.contextualViewModel
import org.nehuatl.tachiwin.destinations.TranscribeDestination
import org.nehuatl.tachiwin.ui.theme.TachiwinTheme

@RootNavGraph(start = true)
@Destination
@Composable
fun Dictionary(navigation: NavController) {
    val viewModel = contextualViewModel()
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val scrollState = rememberCollapsingToolbarScaffoldState()
    val systemUiController = rememberSystemUiController()
    val collapsedFraction by remember { derivedStateOf {
        scrollState.toolbarState.progress
    } }
    val query by viewModel.dictionaryQuery.collectAsState(null)
    val categories by viewModel.categories.observeAsState(listOf())
    val isFiltered by viewModel.isFiltered.observeAsState(false)
    val sliderState = remember { mutableFloatStateOf(0f) }
    val firstCharIndex = viewModel.firstCharIndex.observeAsState(-1)
    val categoriesCollapsed = remember { mutableStateOf(false) }
    val isCollapsed = remember(scrollState.offsetY) {
        derivedStateOf {
            scrollState.offsetY <= (scrollState.toolbarState.minHeight * -1)
        }
    }
    val displayCategories by remember(query, categories) {
        derivedStateOf {
            if (categories.any { it.selected }) {
                categories.filter { it.selected }
            } else {
                categories
            }
        }
    }
    val showCategories by remember(categoriesCollapsed.value, categories) {
        derivedStateOf {
            categories.isNotEmpty() && !categoriesCollapsed.value
        }
    }   

    LaunchedEffect(listState.firstVisibleItemIndex, listState.isScrollInProgress) {
        if (listState.isScrollInProgress) {
            val index = listState.layoutInfo.visibleItemsInfo.firstOrNull()?.index ?: 0
            viewModel.visibleItemToCharIndex(index)?.let {
                sliderState.value = it
            }
        }
    }

    LaunchedEffect(firstCharIndex.value) {
        if (firstCharIndex.value < 1) return@LaunchedEffect
        viewModel.logger.jump()
        scope.launch {
            listState.scrollToItem(firstCharIndex.value)
        }
    }

    LaunchedEffect(scrollState.offsetY) {
        categoriesCollapsed.value = scrollState.offsetY <= (scrollState.toolbarState.minHeight * -1)
    }

    LaunchedEffect(Unit) {
        viewModel.startDictionary()
    }

    TachiwinTheme {
        if (isCollapsed.value) {
            systemUiController.setStatusBarColor(MaterialTheme.colorScheme.background)
        } else {
            systemUiController.setStatusBarColor(MaterialTheme.colorScheme.primary)
        }
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter,
        ) {
            CollapsingToolbarScaffold(
                modifier = Modifier.fillMaxWidth(),
                state = scrollState,
                scrollStrategy = ScrollStrategy.EnterAlways,
                toolbar = {
                    Header(
                        modifier = Modifier.parallax(0.5f),
                        navigation = navigation,
                        progress = collapsedFraction,
                        model = viewModel
                    ) {
                        SearchView(
                            modifier = Modifier.fillMaxWidth(),
                            model = viewModel,
                        )
                    }
                }
            ) {
                Column {
                    AnimatedVisibility(
                        visible = showCategories,
                        enter = expandVertically(
                            expandFrom = Alignment.Top,
                            animationSpec = tween(300)
                        ),
                        exit = shrinkVertically(
                            shrinkTowards = Alignment.Top,
                            animationSpec = tween(300)
                        )
                    ) {
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp, horizontal = 4.dp)
                        ) {
                            items(displayCategories) { category ->
                                Card(
                                    modifier = Modifier
                                        .padding(4.dp)
                                        .clickable {
                                            viewModel.toggleFilter(category.uid)
                                        },
                                    shape = RoundedCornerShape(24.dp),
                                    elevation = CardDefaults.cardElevation(0.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (category.selected) {
                                            MaterialTheme.colorScheme.primary
                                        } else {
                                            MaterialTheme.colorScheme.secondary
                                        },
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier.padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = localizedText(category.plural ?: category.name),
                                            style = MaterialTheme.typography.titleMedium,
                                            modifier = Modifier.padding(horizontal = 4.dp),
                                            color = if (category.selected) {
                                                MaterialTheme.colorScheme.onPrimary
                                            } else {
                                                MaterialTheme.colorScheme.onSecondary
                                            }
                                        )
                                        if (category.selected) FaIcon(
                                            faIcon = FaIcons.Times,
                                            tint = MaterialTheme.colorScheme.onPrimary,
                                            size = 20.dp,
                                            modifier = Modifier
                                                .padding(
                                                    start = 2.dp,
                                                    end = 3.dp,
                                                    bottom = 1.dp
                                                )
                                        )
                                    }
                                }
                            }
                        }
                    }
                    EntryList(
                        modifier = Modifier.weight(1f),
                        navigation = navigation,
                        listState = listState,
                        viewModel = viewModel
                    )
                    if (!isFiltered) {
                        CharacterSlider(
                            modifier = Modifier
                                .padding(bottom = if (Constants.SHOW_ADS) 56.dp else 8.dp),
                            labels = viewModel.characters(),
                            value = sliderState
                        ) { position ->
                            viewModel.charIndexToScrollPosition(position)
                        }
                    }
                }
            }
            if (Constants.SHOW_ADS) BannerAd()
        }
    }
}



