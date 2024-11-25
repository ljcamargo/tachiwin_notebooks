package org.nehuatl.tachiwin.views

import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.guru.fontawesomecomposelib.FaIcon
import com.guru.fontawesomecomposelib.FaIcons
import org.nehuatl.tachiwin.R

@Composable
fun DetailTopBar(navigation: NavController, title: String) {
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = {
                navigation.navigateUp()
            }) {
                FaIcon(
                    faIcon = FaIcons.ArrowLeft,
                    tint = MaterialTheme.colorScheme.primary,
                    //contentDescription = stringResource(R.string.back),
                )
            }
        },
        title = {
            if (title.isNotBlank()) Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
            )
        },
        elevation = 0.dp,
        backgroundColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.primary,
    )
}