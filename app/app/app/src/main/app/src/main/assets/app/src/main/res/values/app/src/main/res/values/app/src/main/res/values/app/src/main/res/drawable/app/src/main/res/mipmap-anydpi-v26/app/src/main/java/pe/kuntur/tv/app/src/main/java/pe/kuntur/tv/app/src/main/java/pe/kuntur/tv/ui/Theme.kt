package pe.kuntur.tv.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val PeruRed = Color(0xFFD91023)
val PeruDark = Color(0xFF0B0D12)
val PeruSurface = Color(0xFF151922)

@Composable
fun PeruTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = PeruRed,
            background = PeruDark,
            surface = PeruSurface,
            onPrimary = Color.White,
            onBackground = Color.White,
            onSurface = Color.White
        ),
        content = content
    )
}
