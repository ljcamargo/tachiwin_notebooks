package org.nehuatl.tachiwin

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import kotlinx.serialization.json.Json
import org.json.JSONArray
import org.json.JSONObject
import org.koin.androidx.compose.get
import org.nehuatl.tachiwin.models.WordList
import org.nehuatl.tachiwin.viewmodels.MainViewModel
import java.text.Normalizer
import java.util.Locale

fun Context.loadAsset(file: String): String {
    return assets.open(file).bufferedReader().use { it.readText() }
}

fun Context.loadWordList(file: String): WordList {
    val string = loadAsset(file)
    return Json.decodeFromString(string)
}

fun Context.loadTranslations(file: String): Map<String, String> {
    val string = assets.open(file).bufferedReader().use { it.readText() }
    return JSONObject(string).toMap().mapValues { (_, value) -> value.toString() }
}

private fun toValue(element: Any) = when (element) {
    JSONObject.NULL -> null
    is JSONObject -> element.toMap()
    is JSONArray -> element.toList()
    else -> element
}

fun JSONArray.toList(): List<Any?> =
    (0 until length()).map { index -> toValue(get(index)) }

fun JSONObject.toMap(): Map<String, Any?> =
    keys().asSequence().associateWith { key -> toValue(get(key)) }

fun String.slugify(replacement: String = "-") = Normalizer
    .normalize(this, Normalizer.Form.NFD)
    .replace("[^\\p{ASCII}]".toRegex(), "")
    .replace("[^a-zA-Z0-9\\s]+".toRegex(), "").trim()
    .replace("\\s+".toRegex(), replacement)
    .lowercase(Locale.getDefault())


fun String.firstUpper(): String {
    return replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
    }
}

fun String.asComposableColor(): Color {
    return Color(android.graphics.Color.parseColor(this))
}

fun String.v() {
    Log.v(">>>>>>>", ">>>>>$this")
}

fun Color.toHex(): String {
    return String.format("#%06X", 0xFFFFFF and this.toArgb())
}

fun Long.toGigs(): String {
    return (this / 1_073_741_824.0).format(2) + " Gb"
}
fun Double.format(digits: Int) = "%.${digits}f".format(this)

@Composable
fun contextualViewModel(): MainViewModel {
    val model = get<MainViewModel>()
    return model
}

fun Context.updateAppLocale(languageCode: String): Context {
    val locale = Locale(languageCode)
    Locale.setDefault(locale)
    val config = resources.configuration
    config.setLocale(locale)
    config.setLayoutDirection(locale)
    return createConfigurationContext(config)
}