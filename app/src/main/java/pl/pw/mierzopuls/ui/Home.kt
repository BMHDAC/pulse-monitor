package pl.pw.mierzopuls.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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

@Composable
fun Home(navController: NavController) {
    val viewModel: HomeViewModel by inject()
    val setting: AppSetting by inject()

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {}
    val coroutineScope = rememberCoroutineScope()
    Box(modifier = Modifier.fillMaxSize()) {
        LogoPW(modifier = Modifier.align(Alignment.TopCenter))
        IconButton(onClick = { viewModel.openInstruction = true} ) {
            Icon(
                modifier = Modifier.padding(8.dp),
                imageVector = Icons.Outlined.Info,
                contentDescription = ""
            )
        }
        PulseBtn(modifier = Modifier.align(Alignment.Center),
            algState = viewModel.algState,
            progress = viewModel.studyProgress,
            onClick = {
                if (viewModel.algState is AlgState.NONE) {
                    viewModel.beginStudy(launcher, coroutineScope)
                } else viewModel.dismissStudy(coroutineScope)
            }
        )
        Row(
            modifier = Modifier.align(Alignment.BottomCenter),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Button(modifier = Modifier
                .padding(16.dp)
                .wrapContentWidth(Alignment.CenterHorizontally),
                onClick = { navController.navigate("history") }) {
                Text(text = stringResource(id = R.string.app_history))
            }
        }
    }
    if (viewModel.openInstruction) {
        var showAgain by remember { mutableStateOf(!setting.showInstructionOnStart) }
        InstructionDialog(
            onDismiss = { viewModel.openInstruction = false },
            showAgain = showAgain,
            onCheckboxChange = {
                setting.showInstructionOnStart = !it
                showAgain = it
            }
        )
    }
}