package pl.pw.mierzopuls.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import pl.pw.mierzopuls.R

@Composable
fun InstructionDialog(
    onDismiss: () -> Unit,
    showAgain: (Boolean) -> Unit
) {
    AlertDialog(
        title = { Text(text = stringResource(id = R.string.instruction_title)) },
        text = {
            Column {
                InstructionContent()
                Row {
                    Checkbox(modifier = Modifier.padding(horizontal = 8.dp), checked = false, onCheckedChange = showAgain)
                    Text(stringResource(id = R.string.instruction_show_again))
                }
            }
        },
        buttons = {
            Row(
                modifier = Modifier.padding(all = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onDismiss() }
                ) { Text(stringResource(id = R.string.instruction_ok)) }
            }
        },
        onDismissRequest = onDismiss
    )
}

@Composable
fun InstructionContent() {
    Text(modifier = Modifier.padding(12.dp),
        text = "Przed badaniem należy zezwolić na dostęp do tylnej kamery. Następnie zasłonić palcem obiektyw i lampę błyskową. Rejestracja trwa około 18 s."
    )
    Image(
        modifier = Modifier
            .padding(12.dp)
            .fillMaxWidth(),
        painter = painterResource(id = R.drawable.instruction),
        contentDescription = stringResource(id = R.string.instruction_title),
        contentScale = ContentScale.FillWidth
    )
}