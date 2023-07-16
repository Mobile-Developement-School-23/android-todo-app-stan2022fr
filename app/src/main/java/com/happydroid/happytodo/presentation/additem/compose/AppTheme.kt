package com.happydroid.happytodo.presentation.additem.compose

import android.annotation.SuppressLint
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        darkColorPalette()
    } else {
        lightColorPalette()
    }

    MaterialTheme(
        colors = colors,
        content = content
    )
}

@SuppressLint("ConflictingOnColor")
@Composable
fun lightColorPalette(): Colors = lightColors(
    primary = Color(0xFFF7F6F2),
    primaryVariant = Color(0xFFF7F6F2),
    secondary = Color(0xFFFFFFFF),
    secondaryVariant = Color(0xFFFFFFFF),
    background = Color(0xFFFFFFFF),
    surface = Color(0xFFF7F6F2),
    error = Color(0xFFFF3B30),
    onPrimary = Color.Black,
    onSecondary = Color(0x99000000),
    onBackground = Color(0x4D000000),
    onSurface = Color.Black,
    onError = Color.White
)

@Composable
fun darkColorPalette(): Colors = darkColors(
    primary = Color(0xFF161618),
    secondary = Color(0xFF252528),
    background = Color(0xFF3C3C3F),
    surface = Color(0xFF161618),
    onPrimary = Color.White,
    onSecondary = Color(0x99FFFFFF),
    onBackground = Color(0x66FFFFFF),
    onSurface = Color.White,
    onError = Color.White,
)
