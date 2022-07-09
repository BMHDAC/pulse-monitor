package pl.pw.mierzopuls.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import pl.pw.mierzopuls.R
import pl.pw.mierzopuls.ui.components.LogoPW
import pl.pw.mierzopuls.ui.components.PulseBtn

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
                .wrapContentWidth(Alignment.CenterHorizontally),
                onClick = { viewModel.onHistory() }) {
                Text(text = stringResource(id = R.string.app_history))
            }
        }
    }
}