package org.nehuatl.tachiwin.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme

object ThemeSpecs {
    private val LightColorScheme = lightColorScheme(
        primary = PrimaryLight,
        secondary = SecondaryLight,
        tertiary = TertiaryLight,
    )
    private val LightColorSchemeAlt = lightColorScheme(
        primary = PrimaryLightAlt,
        secondary = SecondaryLightAlt,
        tertiary = TertiaryLightAlt,
    )
    private val LightColorSchemeAlt2 = lightColorScheme(
        primary = PrimaryLightAlt2,
        secondary = SecondaryLightAlt2,
        tertiary = TertiaryLightAlt2,
    )
    private val DarkColorScheme = darkColorScheme(
        primary = PrimaryDark,
        secondary = SecondaryDark,
        tertiary = TertiaryDark,
    )
    private val DarkColorSchemeAlt = darkColorScheme(
        primary = PrimaryDarkAlt,
        secondary = SecondaryDarkAlt,
        tertiary = TertiaryDarkAlt,
    )
    private val DarkColorSchemeAlt2 = darkColorScheme(
        primary = PrimaryDarkAlt2,
        secondary = SecondaryDarkAlt2,
        tertiary = TertiaryDarkAlt2,
    )
    private val DefaultColorScheme = LightColorScheme to DarkColorScheme
    private val SchemeDict = mapOf(
        "toes" to (LightColorScheme to DarkColorScheme),
        "esto" to (LightColorSchemeAlt to DarkColorSchemeAlt),
        "toen" to (LightColorScheme to DarkColorScheme),
        "ento" to (LightColorSchemeAlt2 to DarkColorSchemeAlt2),
    )
    fun findScheme(schemeId: String?) = SchemeDict[schemeId] ?: DefaultColorScheme
    val DefaultPair = LightColorScheme to DarkColorScheme
    val Default = LightColorScheme
}