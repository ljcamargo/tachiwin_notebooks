package org.nehuatl.tachiwin.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.nehuatl.tachiwin.Constants
import org.nehuatl.tachiwin.models.Entry
import org.nehuatl.tachiwin.ui.theme.TachiwinTheme

@Composable
fun EntryListItem(entry: Entry, onItemClick: (String) -> Unit) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .background(MaterialTheme.colorScheme.background)
        .padding(16.dp, 8.dp)
        .clickable(onClick = {
            onItemClick(entry.word)
        })
    ) {
        if (entry.isSectionHeader && entry.header != null) {
            Row(
                modifier = Modifier
                    .height(64.dp)
                    .fillMaxWidth()
                    .padding(0.dp, 0.dp),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Start,
            ) {
                Text(
                    text = entry.header!!.formalTitle(),
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 2.dp, 0.dp, 0.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
        ) {
            Text(
                text = entry.word,
                modifier = Modifier.alignByBaseline(),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
            if (Constants.SHOW_PHONETICS) entry.phonetic()?.let { phonetic ->
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = phonetic.word,
                    modifier = Modifier.alignByBaseline(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            if (Constants.SHOW_MEANINGS) entry.meaning()?.let { meaning ->
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = meaning.function,
                    modifier = Modifier.alignByBaseline(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
        Text(
            text = entry.annotatedString(),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            inlineContent = entry.textAnnotations()
        )
    }

}