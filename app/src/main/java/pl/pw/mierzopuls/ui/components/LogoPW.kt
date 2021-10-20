package pl.pw.mierzopuls.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import pl.pw.mierzopuls.R

@Composable
fun LogoPW(modifier: Modifier = Modifier) {
    Image(modifier = modifier
        .padding(16.dp)
        .fillMaxWidth(),
        painter = painterResource(id = R.drawable.pw_mech_logo),
        contentDescription = "pw logo",
        contentScale = ContentScale.FillWidth
    )
}