package pl.pw.mierzopuls.ui.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pl.pw.mierzopuls.R
import pl.pw.mierzopuls.alg.AlgState
import pl.pw.mierzopuls.ui.HomeViewModel

@Composable
fun PulseBtn(modifier: Modifier = Modifier, viewModel: HomeViewModel) {
    val algState = viewModel.algState
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {}
    val coroutineScope = rememberCoroutineScope()
    Button(modifier = modifier
        .width(if (algState is AlgState.NONE) 180.dp else 270.dp)
        .padding(20.dp)
        .aspectRatio(1f),
        shape = CircleShape,
        onClick = {
            if (algState is AlgState.NONE) viewModel.beginStudy(launcher, coroutineScope)
            if (algState is AlgState.Result) viewModel.dismissResult()
        }) {
        if (algState is AlgState.NONE || algState is AlgState.Result) {
            Icon(
                modifier = Modifier.padding(8.dp),
                imageVector = Icons.Outlined.Favorite,
                contentDescription = ""
            )
        }
        Text(
            modifier = Modifier,
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            text = algState.buttonText()
        )
    }
}

@Preview
@Composable
fun PulseBtnPreview(onClick: () -> Unit = {}) {
    Button(
        modifier = Modifier
            .width(160.dp)
            .padding(12.dp)
            .aspectRatio(1f),
        onClick = onClick,
        shape = CircleShape
    ) {
        Text(
            modifier = Modifier,
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            text = "START"
        )
    }
}

@Composable
fun AlgState.buttonText(): String {
    return when (this) {
        AlgState.NONE -> stringResource(id = R.string.btn_pulse_alg_NONE)
        is AlgState.CountDown -> stringResource(id = R.string.btn_pulse_alg_COUNTDOWN, this.count)
        is AlgState.Register -> stringResource(id = R.string.btn_pulse_alg_REGISTRACTION)
        is AlgState.Result -> stringResource(id = R.string.btn_pulse_alg_RESULT, this.study.pulse)
    }
}