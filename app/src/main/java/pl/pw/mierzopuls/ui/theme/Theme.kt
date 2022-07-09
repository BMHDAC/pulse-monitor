package pl.pw.mierzopuls.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ColorPalette = lightColors(
    primary = TealPW,
    primaryVariant = OcenPW,
    secondary = Color.LightGray,
    background = Color.White,
    surface = Color.White
)

@Composable
fun MierzoPulsTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = ColorPalette,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}