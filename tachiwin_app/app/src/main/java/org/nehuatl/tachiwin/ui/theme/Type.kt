package org.nehuatl.tachiwin.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.nehuatl.tachiwin.R

val LibreBaskerville = FontFamily(
    Font(R.font.libre_baskerville),
    Font(R.font.libre_baskerville, FontWeight.SemiBold),
    Font(R.font.libre_baskerville, FontWeight.Bold)
)

val Calistoga = FontFamily(
    Font(R.font.calistoga)
)
val Caladea = FontFamily(
    Font(R.font.caladea),
    Font(R.font.caladea_bold, FontWeight.Bold),
    Font(R.font.caladea_italic, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.caladea_bold_italic, FontWeight.Bold, FontStyle.Italic)
)

val Poppins = FontFamily(
    Font(R.font.poppins),
    Font(R.font.poppins_extralight, FontWeight.ExtraLight),
    Font(R.font.poppins_semibold, FontWeight.SemiBold),
    Font(R.font.poppins_bold, FontWeight.Bold),
    Font(R.font.poppins_bold_italic, FontWeight.Bold, FontStyle.Italic),
    Font(R.font.poppins_extrabold, FontWeight.ExtraBold),
)

val globalText = TextStyle(fontFamily = Poppins, fontWeight = FontWeight.Normal)
val displayText = TextStyle(fontFamily = Poppins, fontWeight = FontWeight.ExtraBold)

val Typography = Typography(
    displayLarge = displayText.copy(
        lineHeight = 58.sp,
        fontSize = 54.sp,
        //fontWeight = FontWeight.W400,
    ),
    displayMedium = displayText.copy(
        lineHeight = 46.sp,
        fontSize = 42.sp,
        //fontWeight = FontWeight.W400,
    ),
    displaySmall = displayText.copy(
        lineHeight = 36.sp,
        fontSize = 34.sp,
        //fontWeight = FontWeight.W400,
    ),
    headlineLarge = globalText.copy(
        lineHeight = 36.sp,
        fontSize = 30.sp,
        fontWeight = FontWeight.W400,
    ),
    headlineMedium = globalText.copy(
        lineHeight = 32.sp,
        fontSize = 26.sp,
        fontWeight = FontWeight.W400,
    ),
    headlineSmall = globalText.copy(
        lineHeight = 30.sp,
        fontSize = 24.sp,
        fontWeight = FontWeight.W400,
    ),
    titleLarge = globalText.copy(
        lineHeight = 24.sp,
        fontSize = 20.sp,
        fontWeight = FontWeight.W400,
    ),
    titleMedium = globalText.copy(
        lineHeight = 24.sp,
        fontSize = 16.sp,
        fontWeight = FontWeight.W500,
    ),
    titleSmall = globalText.copy(
        lineHeight = 18.sp,
        fontSize = 14.sp,
        fontWeight = FontWeight.W500,
    ),
    bodyLarge = globalText.copy(
        lineHeight = 22.sp,
        fontSize = 16.sp,
        fontWeight = FontWeight.W400,
    ),
    bodyMedium = globalText.copy(
        lineHeight = 20.sp,
        fontSize = 14.sp,
        fontWeight = FontWeight.W400,
    ),
    bodySmall = globalText.copy(
        lineHeight = 16.sp,
        fontSize = 12.sp,
        fontWeight = FontWeight.W400,
    ),
    labelLarge = globalText.copy(
        lineHeight = 20.sp,
        fontSize = 14.sp,
        fontWeight = FontWeight.W500,
    ),
    labelMedium = globalText.copy(
        lineHeight = 16.sp,
        fontSize = 12.sp,
        fontWeight = FontWeight.W500,
    ),
    labelSmall = globalText.copy(
        lineHeight = 6.sp,
        fontSize = 11.sp,
        fontWeight = FontWeight.W500,
    ),
)