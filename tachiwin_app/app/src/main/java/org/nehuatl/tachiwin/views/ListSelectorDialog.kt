package org.nehuatl.tachiwin.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import org.nehuatl.tachiwin.R

@Composable
fun ListSelectorDialog(
    modifier: Modifier,
    showDialog: MutableState<Boolean>,
    title: Int,
    options: List<String>,
    nullOption: String? = null,
    selectedOption: String?,
    onOptionSelected: (Int) -> Unit,
) {
    Dialog(onDismissRequest = { showDialog.value = false }) {
        Surface(
            modifier = modifier
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.background),
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = localizedString(title),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                options.forEachIndexed { index, option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clickable {
                                onOptionSelected(index)
                                showDialog.value = false
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = option,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.weight(1f)
                        )
                        if (index == options.indexOf(selectedOption)) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = localizedString(R.string.selected),
                                tint = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                }
                if (nullOption != null) Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable {
                            onOptionSelected(-1)
                            showDialog.value = false
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = nullOption,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f)
                    )
                    if (selectedOption == null) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = localizedString(R.string.selected),
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }
        }
    }
}