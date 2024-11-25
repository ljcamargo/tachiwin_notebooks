@file:OptIn(ExperimentalLayoutApi::class)

package org.nehuatl.tachiwin.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import org.nehuatl.tachiwin.Constants
import org.nehuatl.tachiwin.asComposableColor
import org.nehuatl.tachiwin.contextualViewModel
import org.nehuatl.tachiwin.ui.extensions.highlight
import org.nehuatl.tachiwin.ui.theme.TachiwinTheme

@RootNavGraph
@Destination
@Composable
fun Detail(
    navigation: NavController,
    dictionaryId: String,
    entryId: String,
    query: String?= null
) {
    val viewModel = contextualViewModel()
    val dictionary = viewModel.dictionary.observeAsState()
    val entry = viewModel.entry.observeAsState()
    val title = if (dictionary.value?.shortName != null) {
        localizedText(dictionary.value?.shortName!!)
    } else {
        dictionary.value?.name
    }
    val categories = dictionary.value?.categories?.filter {
        it.uid in (entry.value?.categories ?: emptyList())
    } ?: emptyList()

    LaunchedEffect(Unit) {
        viewModel.setDictionary(dictionaryId)
    }

    LaunchedEffect(dictionary) {
        viewModel.findEntry(entryId)
    }

    TachiwinTheme {
        Scaffold(
            topBar = {
                DetailTopBar(
                    navigation = navigation,
                    title = title ?: "",
                )
            },
            backgroundColor = MaterialTheme.colorScheme.background
        ) { pad ->
            Box(
                modifier = Modifier.fillMaxSize().consumeWindowInsets(pad),
                contentAlignment = Alignment.BottomCenter
            ) {
                entry.value?.let { entry ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background)
                            .padding(PaddingValues(16.dp, 16.dp))
                    ) {
                        Text(
                            text = entry.word,
                            style = MaterialTheme.typography.displaySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        if (Constants.SHOW_PHONETICS) entry.phonetic()?.let { phonetic ->
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = phonetic.word,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                        if (Constants.SHOW_MEANINGS) entry.meaning()?.let { meaning ->
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = meaning.function,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        entry.presentableText?.let { definition ->
                            if (query != null) {
                                Text(
                                    text = definition.highlight(
                                        query,
                                        MaterialTheme.colorScheme.primary
                                    ),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            } else {
                                Text(
                                    text = definition,
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                        categories.forEach { category ->
                            Spacer(modifier = Modifier.height(16.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(MaterialTheme.colorScheme.primary)
                                    .padding(10.dp, 6.dp)
                            ) {
                                Text(
                                    text = category.name,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                )
                            }
                        }
                        entry.variant?.let { variant ->
                            Spacer(modifier = Modifier.height(16.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(10.dp, 6.dp)
                            ) {
                                Text(
                                    text = variant.name,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                        entry.color?.let { color ->
                            Spacer(modifier = Modifier.height(48.dp))
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(128.dp)
                                        .clip(CircleShape)
                                        .background(color.asComposableColor())
                                )
                                Text(
                                    modifier = Modifier.padding(top = 16.dp),
                                    text = color.uppercase(),
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.outline,
                                )
                            }
                        }
                    }
                }
                if (Constants.SHOW_ADS) BannerAd()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DetailPreview() {
    Detail(
        navigation = rememberNavController(),
        dictionaryId = "toes",
        entryId = "tachiwin"
    )
}