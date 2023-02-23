package com.asanme.youify.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.asanme.youify.font.interFamily

// Set of Material typography styles to start with
val Typography = Typography(
    body1 = TextStyle(
        fontFamily = interFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    button = TextStyle(
        fontFamily = interFamily,
        fontWeight = FontWeight.W500,
        fontSize = 14.sp
    ),
    /* Other default text styles to override
    caption = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    )
    */
)