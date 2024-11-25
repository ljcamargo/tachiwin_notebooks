package org.nehuatl.tachiwin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.guru.fontawesomecomposelib.FaIcon
import com.guru.fontawesomecomposelib.FaIcons
import com.halilibo.richtext.markdown.Markdown
import com.halilibo.richtext.ui.RichTextStyle
import com.halilibo.richtext.ui.material.MaterialRichText
import com.halilibo.richtext.ui.resolveDefaults
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import me.onebone.toolbar.CollapsingToolbarScaffold
import me.onebone.toolbar.ScrollStrategy
import me.onebone.toolbar.rememberCollapsingToolbarScaffoldState
import org.nehuatl.tachiwin.ui.theme.TachiwinTheme

@RootNavGraph
@Destination
@Composable
fun ReaderScreen(
    navigation: NavController,
    file: String,
) {
    TachiwinTheme {
        CollapsingToolbarScaffold(
            modifier = Modifier.fillMaxWidth(),
            state = rememberCollapsingToolbarScaffoldState(),
            scrollStrategy = ScrollStrategy.EnterAlways,
            toolbar = {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = {
                            navigation.navigateUp()
                        }) {
                            FaIcon(
                                faIcon = FaIcons.ArrowLeft,
                                tint = MaterialTheme.colorScheme.primary,
                                //contentDescription = stringResource(R.string.back),
                            )
                        }
                    },
                    title = {

                    },
                    elevation = 0.dp,
                    backgroundColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.primary,
                )
            }
        ) {
            val content = LocalContext.current.loadAsset(file)
            MarkdownDisplay(content.trimIndent())
        }
    }
}

fun TextStyle.withColor(color: Color) = this.copy(color = color)
fun TextStyle.bold() = this.copy(fontWeight = FontWeight.Bold)
fun TextStyle.moreHeight() = this.copy(lineHeight = this.lineHeight * 1)

@Composable
fun SpanStyle.material3Style(): SpanStyle {
    val colors = MaterialTheme.colorScheme
    val base = MaterialTheme.typography.bodyMedium
    return this.copy(
        color = colors.onBackground,
        fontSize = base.fontSize,
        //fontStyle = base.fontStyle,
        fontFamily = base.fontFamily,
        //fontWeight = base.fontWeight,
    )
}

@Composable
fun RichTextStyle.material3Style(): RichTextStyle {
    val typography = MaterialTheme.typography
    val primary = MaterialTheme.colorScheme.primary
    return this.copy(
        headingStyle = { level, _ ->
            when (level) {
                0 -> typography.displayMedium.withColor(primary).bold().moreHeight()
                1 -> typography.displaySmall.withColor(primary).bold().moreHeight()
                2 -> typography.headlineLarge.withColor(primary).bold().moreHeight()
                3 -> typography.headlineMedium.withColor(primary).bold().moreHeight()
                4 -> typography.headlineSmall.withColor(primary).bold().moreHeight()
                5 -> typography.titleLarge.withColor(primary).bold()
                else -> typography.headlineSmall.withColor(primary).bold()
            }
        },
        stringStyle = stringStyle?.copy(
            boldStyle = stringStyle?.boldStyle?.material3Style(),
            italicStyle = stringStyle?.italicStyle?.material3Style(),
            underlineStyle = stringStyle?.underlineStyle?.material3Style(),
            strikethroughStyle = stringStyle?.strikethroughStyle?.material3Style(),
            subscriptStyle = stringStyle?.subscriptStyle?.material3Style(),
            superscriptStyle = stringStyle?.superscriptStyle?.material3Style(),
            codeStyle = stringStyle?.codeStyle?.material3Style(),
            linkStyle = stringStyle?.linkStyle?.material3Style()?.copy(color = primary),
        ),
    )
}

@Composable
fun MarkdownDisplay(content: String) {
    TachiwinTheme {
        MaterialRichText(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
                .fillMaxSize(),
            style = RichTextStyle().resolveDefaults().material3Style()
                /*.resolveDefaults()
                .run {
                    copy(
                        stringStyle = stringStyle?.copy(
                            boldStyle = stringStyle?.boldStyle,
                            italicStyle = stringStyle?.italicStyle,
                            underlineStyle = stringStyle?.underlineStyle,
                            strikethroughStyle = stringStyle?.strikethroughStyle,
                            subscriptStyle = stringStyle?.subscriptStyle,
                            superscriptStyle = stringStyle?.superscriptStyle,
                            codeStyle = stringStyle?.codeStyle,
                            linkStyle = stringStyle?.linkStyle,
                        )*//*
                    )
                }*/
        ) {
            Markdown(content)
        }
    }
}



@Preview(showBackground = true)
@Composable
fun ReaderScreenPreview() {
    MarkdownDisplay("""
         # Demo
    
        Emphasis, aka italics, with *asterisks* or _underscores_. Strong emphasis, aka bold, with **asterisks** or __underscores__. Combined emphasis with **asterisks and _underscores_**. [Links with two blocks, text in square-brackets, destination is in parentheses.](https://www.example.com). Inline `code` has `back-ticks around` it.
    
        1. First ordered list item
        2. Another item
            * Unordered sub-list.
        3. And another item.
            You can have properly indented paragraphs within list items. Notice the blank line above, and the leading spaces (at least one, but we'll use three here to also align the raw Markdown).
    
        * Unordered list can use asterisks
        - Or minuses
        + Or pluses
        ---
    
        ```javascript
        var s = "code blocks use monospace font";
        alert(s);
        ```
    
        Markdown | Table | Extension
        --- | --- | ---
        *renders* | `beautiful images` | ![random image](https://picsum.photos/seed/picsum/400/400 "Text 1")
        1 | 2 | 3
    
        > Blockquotes are very handy in email to emulate reply text.
        > This line is part of the same quote.
        """
    )
}