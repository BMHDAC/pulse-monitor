package pl.pw.mierzopuls.ui

import android.util.Log
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.MeteringPointFactory
import androidx.camera.core.SurfaceOrientedMeteringPointFactory
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import pl.pw.mierzopuls.alg.AlgState
import pl.pw.mierzopuls.ui.components.LogoPW
import pl.pw.mierzopuls.ui.components.PulseBtn
import pl.pw.mierzopuls.util.getCameraProvider

@Composable
fun Home(viewModel: HomeViewModel) {
    Box(modifier = Modifier.fillMaxSize()) {
        LogoPW(modifier = Modifier.align(Alignment.TopCenter))
        PulseBtn(modifier = Modifier.align(Alignment.Center),
            viewModel = viewModel)
        Row(modifier = Modifier.align(Alignment.BottomCenter),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center) {
            Button(modifier = Modifier
                .padding(16.dp)
                .wrapContentWidth(Alignment.Start),
                onClick = { viewModel.onHistory() }) {
                Text(text = "Wyświetl pomiary")
            }
            Button(modifier = Modifier
                .padding(16.dp)
                .wrapContentWidth(Alignment.End),
                onClick = { viewModel.onStudy() })  {
                Text(text = "Tryb debug")
            }
        }
    }
    LaunchedEffect(viewModel.studyOn) {
        if (viewModel.studyOn) {
            viewModel.prepareCamera()
        } else {
            viewModel.dismissStudy()
        }
    }
//    if (viewModel.algState != AlgState.NONE) {
//        LaunchedEffect(true) {
//            viewModel.prepareCamera()
//        }
//    } else {
//        LaunchedEffect(true) {
//            viewModel.dismissStudy()
//        }
//    }
}