package pl.pw.mierzopuls.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import pl.pw.mierzopuls.ui.components.LogoPW
import pl.pw.mierzopuls.ui.components.PulseBtn

@Composable
fun Home(navController: NavController) {
    val viewModel = HomeViewModel(LocalContext.current)

    Box(modifier = Modifier.fillMaxSize()) {
        LogoPW(modifier = Modifier.align(Alignment.TopCenter))
        PulseBtn(modifier = Modifier.align(Alignment.Center),
            onClick = {
                //TODO
            })
        Row(modifier = Modifier.align(Alignment.BottomCenter),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center) {
            Button(modifier = Modifier
                .padding(16.dp)
                .wrapContentWidth(Alignment.Start),
                onClick = { navController.navigate("history") }) {
                Text(text = "Wyświetl pomiary")
            }
            Button(modifier = Modifier
                .padding(16.dp)
                .wrapContentWidth(Alignment.End),
                onClick = { navController.navigate("debug") })  {
                Text(text = "Tryb debug")
            }
        }
    }
}

@Preview
@Composable
fun HomePreview(){
    Box(modifier = Modifier.fillMaxSize()) {
        LogoPW(modifier = Modifier.align(Alignment.TopCenter))
        PulseBtn(modifier = Modifier.align(Alignment.Center),
            onClick = { } )
        Row(modifier = Modifier.align(Alignment.BottomCenter),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center) {
            Button(modifier = Modifier
                .padding(16.dp)
                .wrapContentWidth(Alignment.Start),
                onClick = { },
                enabled = false) {
                Text(text = "Wyświetl pomiary")
            }
            Button(modifier = Modifier
                .padding(16.dp)
                .wrapContentWidth(Alignment.End),
                onClick = { },
                enabled = false)  {
                Text(text = "Nowe badanie")
            }
        }
    }
}