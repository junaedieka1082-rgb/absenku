package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = DarkPrimary,
    onPrimary = Color.Black,
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = Color.White,
    secondary = CyanAccent,
    onSecondary = Color.White,
    background = DarkBackground,
    surface = DarkSurface,
    surfaceVariant = DarkSurfaceVariant,
    onBackground = Color.White,
    onSurface = Color.White,
  )

private val LightColorScheme =
  lightColorScheme(
    primary = CorporateBluePrimary,
    onPrimary = Color.White,
    primaryContainer = CorporateBlueContainer,
    onPrimaryContainer = OnCorporateBlueContainer,
    secondary = CyanAccent,
    onSecondary = Color.White,
    secondaryContainer = CyanAccentContainer,
    onSecondaryContainer = CorporateBlueDark,
    background = OffWhiteSurface,
    onBackground = DarkSlateText,
    surface = PureWhite,
    onSurface = DarkSlateText,
    surfaceVariant = SoftGraySurfaceVariant,
    onSurfaceVariant = MediumSlateText,
    outline = SlateBorder,
    error = CrimsonError,
    onError = Color.White,
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  brandPrimaryColor: Color? = null,
  content: @Composable () -> Unit,
) {
  val baseScheme = if (darkTheme) DarkColorScheme else LightColorScheme
  val colorScheme = if (brandPrimaryColor != null) {
    baseScheme.copy(
      primary = brandPrimaryColor,
      primaryContainer = brandPrimaryColor.copy(alpha = 0.15f)
    )
  } else {
    baseScheme
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}

