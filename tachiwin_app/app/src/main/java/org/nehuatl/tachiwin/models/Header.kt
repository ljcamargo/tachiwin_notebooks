package org.nehuatl.tachiwin.models

import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class Header(
    val index: Int,
    val title: String,
    val link: String,
    val slug: String,
    val icon: String?= null,
    val description: String?= null,
) {
    fun formalTitle() = title.replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
    }//uppercase(Locale.getDefault())
}