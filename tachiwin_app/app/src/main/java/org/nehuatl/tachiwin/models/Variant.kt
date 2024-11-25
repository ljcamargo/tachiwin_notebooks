package org.nehuatl.tachiwin.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity
@Serializable
class Variant(
    @PrimaryKey val uid: String,
    val name: String,
    val description: String?= null,
) {
    val demonym: String?= null
    override fun toString() = name
}