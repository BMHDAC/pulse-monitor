package pl.pw.mierzopuls.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Checkbox(
    modifier: Modifier = Modifier,
    title: String = "",
    checked: Boolean = false,
    onCheckedChange: (Boolean) -> Unit = {}
) {
        androidx.compose.material.Checkbox(
            modifier = modifier,
            checked = checked,
            onCheckedChange = onCheckedChange
        )
        Text(modifier = Modifier, text = title)

}