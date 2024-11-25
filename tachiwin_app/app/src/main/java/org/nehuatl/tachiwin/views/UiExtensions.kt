package org.nehuatl.tachiwin.views

import android.annotation.SuppressLint
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.em
import org.koin.androidx.compose.get
import org.nehuatl.tachiwin.asComposableColor
import org.nehuatl.tachiwin.core.Preferences
import org.nehuatl.tachiwin.models.Entry
import org.nehuatl.tachiwin.v

fun Entry.annotatedString(): AnnotatedString {
    return buildAnnotatedString {
        color?.let { color ->
            appendInlineContent(Entry.ANNOTATION_COLOR, color)
        }
        presentableText?.let { text ->
            append(text)
        }
    }
}

fun Entry.textAnnotations(): Map<String, InlineTextContent> {
    findAnnotations()
    val map = mutableMapOf<String, InlineTextContent>()
    color?.let { color ->
        map += Entry.ANNOTATION_COLOR to InlineTextContent(
            placeholder = Placeholder(
                width = 1.5.em,
                height = 1.5.em,
                placeholderVerticalAlign = PlaceholderVerticalAlign.Center
            ),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(color.asComposableColor())
            )
        }
    }
    return map
}

@Composable
fun localizedText(string: String): String {
    val preferences = get<Preferences>()
    if (!preferences.languageIsExogenous()) return string
    return preferences.exogenousLanguageEntry(string) ?: string
}

@Composable
fun localizedString(@StringRes id: Int): String {
    val preferences = get<Preferences>()
    if (!preferences.languageIsExogenous()) return stringResource(id)
    val resources = LocalContext.current.resources
    val entry = resources.getResourceEntryName(id)
    return preferences.exogenousLanguageEntry(entry) ?: stringResource(id)
}

@SuppressLint("DiscouragedApi")
@Composable
fun tokenizedLocalizedText(stringId: String): String {
    val preferences = get<Preferences>()
    return if (!preferences.languageIsExogenous()) {
        "language is default".v()
        val resources = LocalContext.current.resources
        val entry = resources.getIdentifier(
            /* name = */ "token_$stringId",
            /* defType = */ "string",
            /* defPackage = */ LocalContext.current.packageName
        )
        "entry? $entry".v()
        if (entry == 0) stringId else stringResource(entry)
    } else {
        "language is not default".v()
        preferences.exogenousLanguageEntry(stringId) ?: stringId
    }
}