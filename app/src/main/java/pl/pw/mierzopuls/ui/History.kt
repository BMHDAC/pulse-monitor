package pl.pw.mierzopuls.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.androidx.compose.inject
import pl.pw.mierzopuls.model.Study
import pl.pw.mierzopuls.model.toDisplay
import pl.pw.mierzopuls.ui.components.LogoPW
import pl.pw.mierzopuls.ui.components.StudyChart
import pl.pw.mierzopuls.util.SampleData

@ExperimentalFoundationApi
@Composable
fun History() {
    val viewModel: HomeViewModel by inject()
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        stickyHeader {
            LogoPW()
        }
        items(viewModel.studies) { study ->
            StudyRow(study = study)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun StudyRow(study: Study) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .height(if (isExpanded) 264.dp else 64.dp),
        elevation = 2.dp,
        onClick = {
            isExpanded = !isExpanded
        }) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        modifier = Modifier.padding(16.dp),
                        text = study.date.toDisplay(),
                        color = Color.DarkGray
                    )
                }
                Row(Modifier.width(120.dp)) {
                    Icon(modifier = Modifier.padding(8.dp),
                        imageVector = Icons.Filled.Favorite,
                        tint = Color(195, 5, 60),
                        contentDescription = "")
                    Text(
                        fontSize = 17.sp,
                        modifier = Modifier.padding(8.dp),
                        text = study.pulse.let { if (it == 2) " $it" else "$it"}
                    )
                }
                Column(modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.End) {
                    Icon(modifier = Modifier.padding(16.dp),
                        imageVector = Icons.Outlined.ArrowDropDown,
                        contentDescription = ""
                    )
                }
            }
        }
        if (isExpanded) {
            Row(
                modifier = Modifier
                    .height(200.dp)
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                StudyChart(
                    modifier = Modifier
                        .height(200.dp)
                        .fillMaxWidth(),
                    study = study
                )
            }

        }
    }
}

@ExperimentalFoundationApi
@Composable
@Preview
fun HistoryPreview(
    studies: List<Study> = SampleData.studies
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        stickyHeader {
            LogoPW()
        }
        items(studies) { study ->
            StudyRow(study = study)
        }
    }
}