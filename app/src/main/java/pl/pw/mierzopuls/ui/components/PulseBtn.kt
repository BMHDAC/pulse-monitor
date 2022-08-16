package pl.pw.mierzopuls.ui.components

import android.content.res.Resources
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pl.pw.mierzopuls.R
import pl.pw.mierzopuls.alg.AlgState
import pl.pw.mierzopuls.ui.HomeViewModel
import pl.pw.mierzopuls.ui.theme.LightRose

@Composable
fun PulseBtn(modifier: Modifier = Modifier,
             algState: AlgState,
             progress: Float,
             onClick: () -> Unit
) {
    Box(modifier = modifier) {
        if (algState !is AlgState.NONE) {
            CircularProgressIndicator(
                progress,
                modifier.size(270.dp),
                color = LightRose,
                strokeWidth = 24.dp,
            )
        }
        Button(modifier = modifier
            .width(if (algState is AlgState.NONE) 180.dp else 270.dp)
            .padding(20.dp)
            .aspectRatio(1f),
            shape = CircleShape,
            onClick = onClick
        ) {
            if (algState is AlgState.NONE || algState is AlgState.Result) {
                Icon(
                    modifier = Modifier.padding(8.dp),
                    imageVector = Icons.Outlined.Favorite,
                    contentDescription = ""
                )
            }
            Text(text = algState.buttonText(),
                modifier = Modifier.align(Alignment.CenterVertically),
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Bold,
                fontSize = algState.buttonFontSize()
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
fun AlgState.buttonText(): String = when (this) {
    AlgState.NONE -> stringResource(id = R.string.btn_pulse_alg_NONE)
    is AlgState.Register -> stringResource(id = R.string.btn_pulse_alg_REGISTRACTION)
    is AlgState.Result -> stringResource(id = R.string.btn_pulse_alg_RESULT, this.study.pulse)
    is AlgState.Calibrate -> stringResource(id = R.string.btn_pulse_alg_CALIBRATION)
    AlgState.DEBUG -> stringResource(id = R.string.not_implemented)
}


@Composable
fun AlgState.buttonFontSize(): TextUnit = when(this) {
    is AlgState.Result -> 32.sp
    is AlgState.Calibrate -> 20.sp
    else -> 24.sp
}