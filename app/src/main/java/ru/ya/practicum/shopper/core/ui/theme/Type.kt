package ru.ya.practicum.shopper.core.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight

val Typography = Typography(
    headlineSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = Dimens.sp24,
        lineHeight = Dimens.sp32,
        letterSpacing = Dimens.sp0
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = Dimens.sp22,
        lineHeight = Dimens.sp28,
        letterSpacing = Dimens.sp0
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = Dimens.sp16,
        lineHeight = Dimens.sp24,
        letterSpacing = Dimens.sp015
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = Dimens.sp16,
        lineHeight = Dimens.sp24,
        letterSpacing = Dimens.sp05
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = Dimens.sp14,
        lineHeight = Dimens.sp20,
        letterSpacing = Dimens.sp025
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = Dimens.sp12,
        lineHeight = Dimens.sp16,
        letterSpacing = Dimens.sp04
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = Dimens.sp14,
        lineHeight = Dimens.sp20,
        letterSpacing = Dimens.sp01
    )
)
