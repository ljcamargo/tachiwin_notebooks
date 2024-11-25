package org.nehuatl.tachiwin.models

import androidx.room.Entity
import kotlinx.serialization.Serializable

@Entity
@Serializable
data class Category(
    val uid: String,
    val name: String,
) {
    var plural: String?= null
    var description: String?= null

    @Transient var selected = false

    override fun toString() = name
}
