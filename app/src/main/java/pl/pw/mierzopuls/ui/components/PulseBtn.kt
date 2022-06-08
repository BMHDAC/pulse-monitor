package pl.pw.mierzopuls.ui.components

import android.util.Log
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.MeteringPointFactory
import androidx.camera.core.SurfaceOrientedMeteringPointFactory
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pl.pw.mierzopuls.alg.AlgState
import pl.pw.mierzopuls.ui.HomeViewModel
import pl.pw.mierzopuls.util.getCameraProvider

@Composable
fun PulseBtn(modifier: Modifier = Modifier, viewModel: HomeViewModel) {
    val algState = viewModel.algState
    Button(modifier = modifier
        .width(if (algState is AlgState.NONE) 180.dp else 270.dp)
        .padding(20.dp)
        .aspectRatio(1f),
        shape = CircleShape,
        onClick = {
            if (algState is AlgState.NONE) viewModel.beginStudy() else viewModel.dismissStudy()
        }) {
        if (algState !is AlgState.NONE) {
            Icon(modifier = Modifier.padding(8.dp),
                imageVector = Icons.Outlined.Favorite,
                contentDescription = "")
        }
        Text(modifier = Modifier,
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            text = algState.toString())
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