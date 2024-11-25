package org.nehuatl.tachiwin.views

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.ramcosta.composedestinations.annotation.NavGraph
import org.nehuatl.tachiwin.R
import org.nehuatl.tachiwin.destinations.Destination
import org.nehuatl.tachiwin.destinations.DictionaryDestination
import org.nehuatl.tachiwin.destinations.TranslateDestination

sealed class NavItem(
    val destination: Destination,
    @DrawableRes val drawableId: Int,
    @StringRes val resourceId: Int
) {
    data object Dictionary: NavItem(
        destination = DictionaryDestination,
        drawableId = R.drawable.ic_dictionary,
        resourceId = R.string.dictionary
    )
    data object Translation: NavItem(
        destination = TranslateDestination,
        drawableId = R.drawable.ic_translate,
        resourceId = R.string.translator
    )
}

val homeTabs = listOf(NavItem.Dictionary, NavItem.Translation)

@NavGraph
annotation class SubsequentNavGraph(val start: Boolean = false)

val Destination.shouldShowScaffoldElements get() = homeTabs.fold(false) { acc, it ->
    it.destination == this || acc
}