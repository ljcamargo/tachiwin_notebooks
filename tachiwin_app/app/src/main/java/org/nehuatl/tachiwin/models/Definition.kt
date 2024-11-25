package org.nehuatl.tachiwin.models

import androidx.room.Entity
import kotlinx.serialization.Serializable

@Entity
@Serializable
data class Definition(
    val text: String,
    val example: String?,
    //val synonyms: List<String>?,
    //val antonyms: List<String>?,
) {
    override fun toString() = text
}