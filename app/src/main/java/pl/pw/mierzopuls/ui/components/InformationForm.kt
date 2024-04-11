package pl.pw.mierzopuls.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import pl.pw.mierzopuls.ui.HomeViewModel

@Composable
fun InformationForm(
    modifier: Modifier,
    viewModel: HomeViewModel
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(16.dp)
            .background(color = Color.White)
    ) {
        Text(
            text = "Please Enter you information",
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(8.dp)
        )

        TextField(
            value = viewModel.name,
            onValueChange = { viewModel.name = it },
            placeholder = {
                Text(text = "Name")
            },
            modifier = Modifier.padding(8.dp)
        )
        TextField(
            value = viewModel.age,
            onValueChange = { viewModel.age = it },
            placeholder = {
                Text(text = "Age")
            },
            modifier = Modifier.padding(8.dp)
        )
        TextField(
            value = viewModel.gender,
            onValueChange = { viewModel.gender = it },
            placeholder = {
                Text(text = "Gender")
            },
            modifier = Modifier.padding(8.dp)
        )
        TextField(
            value = viewModel.height,
            onValueChange = { viewModel.height = it },
            placeholder = {
                Text(text = "Height (in cm)")
            },
            modifier = Modifier.padding(8.dp)
        )
        TextField(
            value = viewModel.weight,
            onValueChange = { viewModel.weight = it },
            placeholder = {
                Text(text = "Weight (in kg)")
            },
            modifier = Modifier.padding(8.dp)
        )

        Button(onClick = { viewModel.enterInformation = false }, modifier = Modifier) {
            Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "Add Information")
        }
    }
}

