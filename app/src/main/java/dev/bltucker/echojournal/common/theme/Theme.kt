package dev.bltucker.echojournal.common.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val EchoJournalColorScheme = lightColorScheme(
    primary = EchoJournalColors.Primary40,
    onPrimary = EchoJournalColors.Primary100,
    primaryContainer = EchoJournalColors.Primary90,
    onPrimaryContainer = EchoJournalColors.Primary10,

    secondary = EchoJournalColors.Secondary50,
    onSecondary = EchoJournalColors.Primary100,
    secondaryContainer = EchoJournalColors.Secondary90,
    onSecondaryContainer = EchoJournalColors.Secondary30,

    tertiary = EchoJournalColors.Secondary70,
    onTertiary = EchoJournalColors.Primary100,
    tertiaryContainer = EchoJournalColors.Secondary95,
    onTertiaryContainer = EchoJournalColors.Secondary30,

    error = EchoJournalColors.Error20,
    onError = EchoJournalColors.Error100,
    errorContainer = EchoJournalColors.Error95,
    onErrorContainer = EchoJournalColors.Error20,

    background = EchoJournalColors.Background,
    onBackground = EchoJournalColors.OnSurface,

    surface = EchoJournalColors.Surface,
    onSurface = EchoJournalColors.OnSurface,
    surfaceVariant = EchoJournalColors.SurfaceVariant,
    onSurfaceVariant = EchoJournalColors.OnSurfaceVariant,

    outline = EchoJournalColors.NeutralVariant50,
    outlineVariant = EchoJournalColors.NeutralVariant80,

    scrim = EchoJournalColors.NeutralVariant30,
    inverseOnSurface = EchoJournalColors.InverseOnSurface,
    inverseSurface = EchoJournalColors.NeutralVariant30,
    inversePrimary = EchoJournalColors.Primary60
)

@Composable
fun EchoJournalTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = EchoJournalColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}


val ColorScheme.moodColors: MoodColors
    get() = MoodColors

val ColorScheme.gradientColors: GradientColors
    get() = GradientColors