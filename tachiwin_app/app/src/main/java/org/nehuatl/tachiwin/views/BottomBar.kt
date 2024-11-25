package org.nehuatl.tachiwin.views

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.nehuatl.tachiwin.Constants
import org.nehuatl.tachiwin.NavGraphs
import org.nehuatl.tachiwin.appCurrentDestinationAsState
import org.nehuatl.tachiwin.contextualViewModel
import org.nehuatl.tachiwin.startAppDestination
import org.nehuatl.tachiwin.ui.theme.ThemeSpecs

@Suppress("KotlinConstantConditions")
@Composable
fun BottomBar(nav: NavController) {

    val viewModel = contextualViewModel()
    val isDark = isSystemInDarkTheme()
    val scheme by viewModel.schemeP.collectAsState(ThemeSpecs.Default)
    val currentDestination = nav.appCurrentDestinationAsState().value
        ?: if (Constants.STARTING_TAB_INDEX >= 0) {
            (homeTabs.getOrNull(Constants.STARTING_TAB_INDEX) ?: homeTabs[0]).destination
        } else {
            NavGraphs.root.startAppDestination
        }

    LaunchedEffect(Unit) { viewModel.refreshTheme(isDark) }

    NavigationBar(
        containerColor = scheme.surface,
        contentColor = scheme.onSurface
    ) {
        homeTabs.forEach { screen ->
            val selected = currentDestination.route == screen.destination.route
            val color = if (selected) scheme.primary else scheme.onBackground
            val text = stringResource(screen.resourceId)
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(screen.drawableId),
                        contentDescription = stringResource(screen.resourceId),
                        modifier = Modifier.size(16.dp),
                        tint = color
                    )
                },
                label = {
                    Text(text = text, color = color)
                },
                selected = selected,
                onClick = {
                    nav.navigate(screen.destination.route) {
                        popUpTo(nav.graph.id) { saveState = true }
                        launchSingleTop = false
                        restoreState = true
                    }
                }
            )
        }
    }

}