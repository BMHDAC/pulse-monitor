package pl.pw.mierzopuls.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun PulseBtn(modifier: Modifier = Modifier) {
    Button(onClick = { /*TODO*/ },
        modifier = modifier) {
        Text(text = "Start")
    }
}

@Preview
@Composable
fun PulseBtnPreview() {
    Button(onClick = { /*TODO*/ },
        modifier = Modifier) {
        Text(text = "Start")
    }
}