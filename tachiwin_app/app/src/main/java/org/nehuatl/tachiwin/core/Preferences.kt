package org.nehuatl.tachiwin.core

import android.content.Context
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.nehuatl.tachiwin.loadTranslations
import org.nehuatl.tachiwin.models.ModelFile
import org.nehuatl.tachiwin.v
import java.util.*

class Preferences(context: Context): KoinComponent {

    private val preferences = context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE)
    private val translations = context.loadTranslations(TRANSLATIONS_FILE)

    private fun bool(key: String, default: Boolean = false): Boolean {
        return preferences.getBoolean(key, default)
    }

    private fun int(key: String, default: Int = 0): Int {
        return preferences.getInt(key, default)
    }

    private fun intNull(key: String): Int? {
        return preferences.getInt(key, -1).let { if (it == -1) null else it }
    }

    private fun string(key: String, default: String = ""): String {
        return preferences.getString(key, default) ?: ""
    }

    private fun put(key: String, value: String) {
        preferences.edit().putString(key, value).apply()
    }

    private fun put(key: String, value: Int) {
        preferences.edit().putInt(key, value).apply()
    }

    private fun put(key: String, value: Boolean) {
        preferences.edit().putBoolean(key, value).apply()
    }

    var modelFile: ModelFile?
        get() = string(MODEL_FILE).let {
            if (it.isEmpty()) null
            else Json.decodeFromString(ModelFile.serializer(), it)
        }
        set(value) = if (value != null) {
            put(MODEL_FILE, Json.encodeToString(ModelFile.serializer(), value))
        } else Unit

    var inferenceMode: InferenceMode?
        get() = intNull(INFERENCE_MODE)?.let { InferenceMode.entries[it] }
        set(value) = if (value != null) put(INFERENCE_MODE, value.ordinal) else Unit

    var lightMode: LightMode
        get() = int(LIGHT_MODE).let { LightMode.entries[it] }
        set(value) = put(LIGHT_MODE, value.ordinal)

    val availableLanguages = listOf("es","to","en",)
    val exogenousLanguages = listOf("to")

    private var language: String?
        get() = preferences.getString(LANGUAGE, null)
        set(value) = preferences.edit().putString(LANGUAGE, value).apply()

    fun setLanguageIndex(index: Int): String? {
        language = availableLanguages.getOrNull(index)
        return language
    }

    fun currentLanguage() = availableLanguages.find { it == language } ?: availableLanguages.first()

    fun languageIsDefault(): Boolean {
        "language is default? $language vs ${Locale.getDefault().displayLanguage}".v()
        return language == null || language == Locale.getDefault().displayLanguage
    }

    fun languageIsExogenous(): Boolean {
        return language in exogenousLanguages
    }

    fun languageIsExogenous(language: String): Boolean {
        return language in exogenousLanguages
    }

    fun exogenousLanguageEntry(key: String): String? {
        // TODO: implement translation from other exogenous languages
        return translations[key]
    }

    enum class LightMode { AUTO, LIGHT, DARK }
    enum class InferenceMode { LOCAL, REMOTE }

    companion object {
        const val TRANSLATIONS_FILE = "translations.json"
        const val SETTINGS_NAME = "org.tachiwin.v1"
        const val LANGUAGE = "language"
        const val LIGHT_MODE = "lightMode"
        const val INFERENCE_MODE = "inferenceMode"
        const val MODEL_FILE = "modelFile"
    }
}