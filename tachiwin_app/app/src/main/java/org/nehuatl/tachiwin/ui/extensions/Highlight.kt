package org.nehuatl.tachiwin.ui.extensions

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import me.xdrop.fuzzywuzzy.FuzzySearch
import org.nehuatl.tachiwin.v

fun String.highlight(
    query: String, color: Color, threshold: Int = 60, limit: Int = 5
): AnnotatedString {
    val list = split(" ")
    val top = FuzzySearch
        .extractAll(query, list, limit)
        .filter { it.score >= threshold }
        .take(limit)
        .map { it.index }
    val builder = AnnotatedString.Builder("")
    list.forEachIndexed { index, word ->
        if (top.contains(index)) {
            builder.pushStyle(SpanStyle(color = color, fontWeight = FontWeight.Bold))
            builder.append(word)
            builder.pop()
        } else {
            builder.append(word)
        }
        builder.append(" ")
    }
    return builder.toAnnotatedString()
}