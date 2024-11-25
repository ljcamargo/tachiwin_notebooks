package org.nehuatl.tachiwin.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity
@Serializable
data class Language(
    @PrimaryKey val iso3: String,
    val name: String,
    val variant: Variant?,
    val description: String?,
    val characters: List<String>?
) {
    var demonym: String?= null

    override fun toString() = name
}