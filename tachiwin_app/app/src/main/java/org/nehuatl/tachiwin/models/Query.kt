package org.nehuatl.tachiwin.models

import kotlinx.serialization.Serializable

@Serializable
data class Query(
    val text: String,
    val dictionary: String,
    val filters: List<String> = listOf()
) {
    override fun toString() = text

    companion object {
        fun empty() = Query("", "")
    }
}