package pl.pw.mierzopuls.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.androidx.compose.inject
import pl.pw.mierzopuls.R
import pl.pw.mierzopuls.alg.AlgState
import pl.pw.mierzopuls.ui.HomeViewModel
import pl.pw.mierzopuls.ui.theme.LightRose

@Composable
fun  PulseBtn(modifier: Modifier = Modifier,
             algState: AlgState,
             progress: Float,
             onClick: () -> Unit
) {
    val viewModel: HomeViewModel by inject()
    Box(
        modifier = modifier.animateContentSize(),
    ) {
        if (algState !is AlgState.NONE) {
            CircularProgressIndicator(
                progress,
                Modifier
                    .size(270.dp)
                    .align(Alignment.Center),
                color = LightRose,
                strokeWidth = 24.dp,
            )
        }
        Button(modifier = Modifier
            .align(Alignment.Center)
            .width(if (algState is AlgState.NONE) 180.dp else 240.dp)
            .aspectRatio(1f),
            shape = CircleShape,
            onClick = onClick
        ) {
            if (algState is AlgState.NONE || algState is AlgState.Result) {
                Icon(
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.CenterVertically)
                        .size(if (algState !is AlgState.Result) 24.dp else 36.dp)
                    ,
                    imageVector = Icons.Outlined.Favorite,
                    contentDescription = ""
                )
            }
            Text(
                modifier = Modifier.align(Alignment.CenterVertically),
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Bold,
                fontSize = algState.buttonFontSize(),
                text = when(algState) {
                    AlgState.NONE -> stringResource(id = R.string.btn_pulse_alg_NONE)
                    is AlgState.Finished,
                    is AlgState.Register -> stringResource(id = R.string.btn_pulse_alg_REGISTRACTION)
                    is AlgState.Result -> stringResource(id = R.string.btn_pulse_alg_RESULT, algState.pulse)
                    is AlgState.Calibration -> stringResource(id = R.string.btn_pulse_alg_CALIBRATION)
                }
            )
        }
    }
}

@Preview
@Composable
fun PulseBtnPreview(onClick: () -> Unit = {}) {
    CircularProgressIndicator(Modifier
        .size(270.dp),
        color = LightRose,
        strokeWidth = 16.dp
    )
    Button(
        modifier = Modifier
            .width(270.dp)
            .padding(12.dp)
            .aspectRatio(1f),
        onClick = onClick,
        shape = CircleShape
    ) {
        Text(
            modifier = Modifier.align(Alignment.CenterVertically),
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            text = "Przyciśnij palec"
        )
    }
}

@Composable
fun AlgState.buttonFontSize(): TextUnit = when(this) {
    is AlgState.Result -> 48.sp
    is AlgState.Calibration -> 24.sp
    else -> 24.sp
}