package org.nehuatl.tachiwin.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.TopAppBar
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.guru.fontawesomecomposelib.FaIcon
import com.guru.fontawesomecomposelib.FaIcons
import com.ramcosta.composedestinations.utils.rememberDestinationsNavigator
import org.nehuatl.tachiwin.R
import org.nehuatl.tachiwin.destinations.SettingsDestination
import org.nehuatl.tachiwin.viewmodels.MainViewModel

@Composable
fun Header(
    modifier: Modifier = Modifier,
    navigation: NavController,
    model: MainViewModel,
    progress: Float = 0f,
    subView: @Composable () -> Unit = {},
) {
    val navigator = navigation.rememberDestinationsNavigator()
    Column(modifier = modifier) {
        TopAppBar(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primary)
                .padding(top = 8.dp, bottom = 16.dp),
            title = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.Center,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().padding(0.dp)
                    ) {
                        Image(
                            painter = painterResource(R.drawable.badger_round),
                            contentDescription = stringResource(R.string.app_name),
                            modifier = Modifier.size(28.dp)
                        )
                        Text(
                            text = stringResource(R.string.app_name),
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.displaySmall,
                            modifier = Modifier.padding(top=0.dp, start = 8.dp)
                        )
                    }
                }
            },
            actions = {
                Row(
                    modifier = Modifier.align(Alignment.Top)
                ) {
                    IconButton(
                        onClick = {
                            navigator.navigate(SettingsDestination())
                            model.logger.about()
                        }
                    ) {
                        FaIcon(
                            faIcon = FaIcons.Cog,
                            tint = MaterialTheme.colorScheme.onPrimary,
                        )
                    }
                }
            },
            elevation = 0.dp,
            backgroundColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp * progress)
                .background(MaterialTheme.colorScheme.primary)
        )
        subView()
    }
}