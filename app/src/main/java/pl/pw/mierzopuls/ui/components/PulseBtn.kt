package pl.pw.mierzopuls.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PulseBtn(modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    Button(modifier = modifier
        .width(176.dp)
        .padding(20.dp)
        .aspectRatio(1f),
        onClick = onClick,
        shape = CircleShape) {
        Text(modifier = Modifier,
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            text = "START")
    }
}

@Preview
@Composable
fun PulseBtnPreview(onClick: () -> Unit = {}) {
    Button(modifier = Modifier
        .width(160.dp)
        .padding(12.dp)
        .aspectRatio(1f),
        onClick = onClick,
        shape = CircleShape) {
        Text(modifier = Modifier,
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            text = "START")
    }
}