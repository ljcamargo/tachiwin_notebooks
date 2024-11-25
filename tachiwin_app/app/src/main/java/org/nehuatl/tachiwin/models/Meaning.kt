package org.nehuatl.tachiwin.models

import androidx.room.Entity
import kotlinx.serialization.Serializable

@Entity
@Serializable
data class Meaning(
    val function: String,
    val definitions: Definitions?,
    val language: Language?
) {
    fun definition() = definitions?.firstOrNull()

    override fun toString() = "$function ${definitions?.joinToString { "$it\n" }}"
}