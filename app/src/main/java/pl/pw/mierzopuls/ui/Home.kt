package pl.pw.mierzopuls.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pl.pw.mierzopuls.ui.components.LogoPW
import pl.pw.mierzopuls.ui.components.PulseBtn

@Composable
fun Home() {
    val viewModel = HomeViewModel(LocalContext.current)
    Box(modifier = Modifier.fillMaxSize()) {
        LogoPW(modifier = Modifier.align(Alignment.TopCenter))
        PulseBtn(modifier = Modifier.align(Alignment.Center),
            onClick = { viewModel.onStart() } )
        Row(modifier = Modifier.align(Alignment.BottomCenter),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center) {
            Button(onClick = { viewModel.onHistory() },
                modifier = Modifier
                    .padding(16.dp)
                    .wrapContentWidth(Alignment.Start)) {
                Text(text = "Wyświetl pomiary")
            }
            Button(onClick = { viewModel.onStudy() },
                modifier = Modifier
                    .padding(16.dp)
                    .wrapContentWidth(Alignment.End)) {
                Text(text = "Nowe badanie")
            }
        }
    }
}

@Preview
@Composable
fun HomePreview(){
    Box(modifier = Modifier.fillMaxSize()) {
        LogoPW(modifier = Modifier.align(Alignment.TopCenter))
        PulseBtn(modifier = Modifier.align(Alignment.Center))
        Row(modifier = Modifier.align(Alignment.BottomCenter),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center) {
            Button(onClick = { /*TODO*/ },
                modifier = Modifier
                    .padding(16.dp)
                    .wrapContentWidth(Alignment.Start)) {
                Text(text = "Wyświetl pomiary")
            }
            Button(onClick = { /*TODO*/ },
                modifier = Modifier
                    .padding(16.dp)
                    .wrapContentWidth(Alignment.End)) {
                Text(text = "Nowe badanie")
            }
        }
    }
}