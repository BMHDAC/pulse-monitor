package pl.pw.mierzopuls.ui

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.outlined.Info
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.inject
import pl.pw.mierzopuls.model.Study
import pl.pw.mierzopuls.model.alg.AlgState
import pl.pw.mierzopuls.model.toJson
import pl.pw.mierzopuls.ui.components.InformationForm
import pl.pw.mierzopuls.ui.components.InstructionDialog
import pl.pw.mierzopuls.ui.components.PulseBtn

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Home() {
    val viewModel: HomeViewModel by inject()
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {}
    var exportUri: Uri? by remember { mutableStateOf(null) }
    var exportStudy: Study? by remember { mutableStateOf(null) }
    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("*/*")
    ) { exportUri = it }
    val contentResolver = LocalContext.current.contentResolver

    LaunchedEffect(exportUri) {
        Log.d("LaunchEff", "export uri: $exportUri")
        if (exportUri != null) {
            contentResolver.openOutputStream(exportUri!!).use {
                it!!.write(exportStudy?.toJson()?.toByteArray())
            }
        }
    }

    HistoryBottomSheet(
        studies = viewModel.studies,
        onSave = { study ->
            exportStudy = study
            exportLauncher.launch("${study.id}.json")
        }
    ) {
        Column(verticalArrangement = Arrangement.Center) {
            Box {
                IconButton(onClick = { viewModel.openInstruction = true }) {
                    Icon(
                        modifier = Modifier.padding(8.dp),
                        imageVector = Icons.Outlined.Info,
                        contentDescription = ""
                    )
                }
            }
            Button(
                onClick = { viewModel.enterInformation = true },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.AddCircle,
                    contentDescription = "Add Info",
                    modifier = Modifier.padding(8.dp)
                )
                Text(text = "Edit information")
            }
            PulseBtn(modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth()
                .aspectRatio(1.0f),
                algState = viewModel.algState,
                progress = viewModel.studyProgress,
                onClick = {
                    if (viewModel.algState is AlgState.NONE) {
                        viewModel.beginStudy(permissionLauncher)
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

    if (viewModel.enterInformation) {
        InformationForm(modifier = Modifier.fillMaxSize(), viewModel = viewModel)
    }
}