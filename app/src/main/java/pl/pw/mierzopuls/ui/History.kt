package pl.pw.mierzopuls.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pl.pw.mierzopuls.model.Study
import pl.pw.mierzopuls.ui.components.LogoPW
import pl.pw.mierzopuls.ui.components.StudyChart
import pl.pw.mierzopuls.util.SampleData

@ExperimentalFoundationApi
@Composable
fun History(
    studies: List<Study>
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun StudyRow(study: Study) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .height(if (isExpanded) 264.dp else 64.dp),
        elevation = 2.dp,
        onClick = {
            isExpanded = !isExpanded
        }) {
        Row(verticalAlignment = Alignment.Top) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    modifier = Modifier.padding(16.dp),
                    text = "date: ${study.date}",
                    color = Color.DarkGray
                )
                Text(
                    modifier = Modifier.padding(16.dp),
                    text = "pulse: ${study.pulse}"
                )
            }
            Box(
                modifier = Modifier.padding(16.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Outlined.ArrowDropDown,
                    contentDescription = ""
                )
            }
        }
        if (isExpanded) {
            Row(
                modifier = Modifier
                    .height(200.dp)
                    .fillMaxWidth(),
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