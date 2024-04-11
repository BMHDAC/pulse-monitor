package pl.pw.mierzopuls.ui.components

import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun Checkbox(
    modifier: Modifier = Modifier,
    title: String = "",
    checked: Boolean = false,
    onCheckedChange: (Boolean) -> Unit = {}
) {
    Checkbox(
        modifier = modifier,
        checked = checked,
        onCheckedChange = onCheckedChange
    )
    Text(
        modifier = Modifier,
        text = title
    )
}