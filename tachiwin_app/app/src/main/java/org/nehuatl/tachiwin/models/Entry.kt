package org.nehuatl.tachiwin.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Entity
@Serializable
data class Entry(
    @PrimaryKey val word: String,
    val lang: Language,
    val variant: Variant?,
    var phonetics: Phonetics?,
    var meanings: Meanings?,
) {
    var key = word
    var likelihood: Int = -1
    var categories: List<String>?= null
    @Transient var isSectionHeader = false
    @Transient var header: Header?= null
    @Transient var presentableText = definition()
    @Transient var color: String? = null

    fun phonetic() = phonetics?.firstOrNull()
    fun meaning() = meanings?.firstOrNull()
    fun definition() = meaning()?.definition()?.text

    private fun colorAnnotation() {
        val definition = definition() ?: return
        val pattern = "#[a-fA-F0-9]{6}".toRegex()
        val color = pattern.find(definition)?.value ?: return
        presentableText = definition.replace(color, "")
        this.color = color
    }

    fun findAnnotations() {
        colorAnnotation()
    }

    override fun toString() = word

    companion object {
        const val ANNOTATION_COLOR = "[color]"
    }
}