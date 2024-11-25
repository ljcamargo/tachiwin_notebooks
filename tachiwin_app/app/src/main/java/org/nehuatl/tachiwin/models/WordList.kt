package org.nehuatl.tachiwin.models

import kotlinx.serialization.Serializable

@Serializable
data class WordList(
    val headers: List<String>,
    val rows: List<List<String>>
)
