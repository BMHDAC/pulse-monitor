package pl.pw.mierzopuls.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.koin.androidx.compose.inject
import pl.pw.mierzopuls.R
import pl.pw.mierzopuls.alg.AlgState
import pl.pw.mierzopuls.model.AppSetting
import pl.pw.mierzopuls.ui.components.InstructionDialog
import pl.pw.mierzopuls.ui.components.LogoPW
import pl.pw.mierzopuls.ui.components.PulseBtn

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Home() {
    val viewModel: HomeViewModel by inject()
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {}

    HistoryBottomSheet(viewModel.studies) {
        Column(verticalArrangement = Arrangement.Center) {
            Box {
                LogoPW(modifier = Modifier.align(Alignment.TopCenter))
                IconButton(onClick = { viewModel.openInstruction = true} ) {
                    Icon(
                        modifier = Modifier.padding(8.dp),
                        imageVector = Icons.Outlined.Info,
                        contentDescription = ""
                    )
                }
            }
            PulseBtn(modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth().aspectRatio(1.0f),
                algState = viewModel.algState,
                progress = viewModel.studyProgress,
                onClick = {
                    if (viewModel.algState is AlgState.NONE) {
                        viewModel.beginStudy(launcher)
                    } else viewModel.dismissStudy()
                }
            )
        }
    }

    if (viewModel.openInstruction) {
        var showAgain by remember { mutableStateOf(viewModel.appSetting.showInstructionOnStart) }
        InstructionDialog(
            onDismiss = { viewModel.openInstruction = false },
            showAgain = showAgain,
            onCheckboxChange = {
                viewModel.appSetting.showInstructionOnStart = !it
                showAgain = it
            }
        )
    }
}