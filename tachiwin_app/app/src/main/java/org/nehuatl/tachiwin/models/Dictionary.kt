package org.nehuatl.tachiwin.models

import android.content.Context
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import me.xdrop.fuzzywuzzy.FuzzySearch
import org.nehuatl.tachiwin.loadAsset
import org.nehuatl.tachiwin.loadWordList
import org.nehuatl.tachiwin.slugify

@Entity
@Serializable
data class Dictionary(
    @PrimaryKey val uid: String,
    val name: String,
    @SerialName("short_name")
    val shortName: String,
    val language1: Language,
    val language2: Language,
    var entries: Entries
) {

    var categories: Categories?= null
    fun characters() = language1.characters
    override fun toString() = name

    fun loadEntries(context: Context): Entries {
        val list = context.loadWordList("${uid}.json")
        entries = List(list.rows.size) { i ->
            mapToEntry(language1, language2, list, i)
        }
        return entries
    }

    fun unloadEntries() {
        entries = listOf()
    }

    private fun mapToEntry(
        language1: Language,
        language2: Language,
        wordList: WordList,
        index: Int
    ): Entry {
        val variantIndex = wordList.headers.indexOfFirst { it == "var" }
        val wordIndex = wordList.headers.indexOfFirst { it == "word" }
        val phoneticIndex = wordList.headers.indexOfFirst { it == "phonetic" }
        val morphologyIndex = wordList.headers.indexOfFirst { it == "morphology" }
        val meaningIndex = wordList.headers.indexOfFirst { it == "meaning" }
        val categoryIndex = wordList.headers.indexOfFirst { it == "category" }
        val entry = wordList.rows.getOrNull(index) ?: listOf()
        return Entry(
            word = entry.getOrNull(wordIndex) ?: "",
            lang = language1,
            variant = Variant(
                uid = entry.getOrNull(variantIndex) ?: "",
                name = entry.getOrNull(variantIndex) ?: "",
                description = ""
            ),
            phonetics = listOf(
                Phonetic(
                    word = entry.getOrNull(phoneticIndex) ?: "",
                    audio = null
                )
            ),
            meanings = listOf(
                Meaning(
                    function = entry.getOrNull(morphologyIndex) ?: "",
                    definitions = listOf(
                        Definition(
                            text = entry.getOrNull(meaningIndex) ?: "",
                            example = null
                        )
                    ),
                    language = language2
                )
            )
        ).also {
            it.categories = entry
                .getOrNull(categoryIndex)
                ?.split("|")
                ?.map { it.trim() }
                ?: listOf()
            it.key = index.toString()
        }
    }

    fun find(query: String, filters: List<String>): Entries {
        val filtered = entries.mapNotNull {
            it.likelihood = FuzzySearch.ratio(
                query.slugify().lowercase(),
                it.word.slugify().lowercase()
            )
            if (it.likelihood >= DEFAULT_THRESHOLD) it else null
        }.sortedByDescending {
            it.isSectionHeader = false
            it.likelihood
        }.filter { entry ->
            entry.categories.isNullOrEmpty() ||
                    entry.categories?.any { category ->
                        filters.isEmpty() || filters.contains(category)
                    } ?: false
        }
        return filtered
    }

    fun any(): Entry {
        return entries.first()
    }

    fun entry(word: String): Entry? {
        return entries.firstOrNull { it.word == word }
    }

    fun all(): Entries {
        return entries
    }

    fun allFiltered(filters: List<String>): Entries {
        return entries.filter { entry ->
            entry.categories.isNullOrEmpty() ||
            entry.categories?.any { category ->
                filters.isEmpty() || filters.contains(category)
            } ?: false
        }
    }

    private fun String.siblings(list: List<String>): List<String> {
        return list.filter {
            it.lowercase().startsWith(this.lowercase()) && it != this
        }
    }

    private fun String.startsWithButNot(other: String, exclude: List<String>): Boolean {
        return this.startsWith(other) && !exclude.any { this.startsWith(it) }
    }


    companion object {
        private const val DEFAULT_THRESHOLD = 30

        fun startDictionaries(context: Context, file: String): List<Dictionary> {
            val string = context.loadAsset(file)
            return Json.decodeFromString(string)
        }
    }
}