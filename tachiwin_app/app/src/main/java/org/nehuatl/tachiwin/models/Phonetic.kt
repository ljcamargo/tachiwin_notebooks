package org.nehuatl.tachiwin.models

import androidx.room.Entity
import kotlinx.serialization.Serializable

@Entity
@Serializable
data class Phonetic(
    val word: String,
    val audio: String?
) {
    override fun toString() = word
}