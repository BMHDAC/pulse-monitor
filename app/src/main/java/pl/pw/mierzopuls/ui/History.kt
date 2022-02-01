package pl.pw.mierzopuls.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pl.pw.mierzopuls.model.Study
import pl.pw.mierzopuls.ui.components.LogoPW

@ExperimentalFoundationApi
@Composable
fun History(
    studies: List<Study>
) {
    LazyColumn {
        stickyHeader {
            LogoPW()
        }
        items(studies) { study -> 
            StudyRow(study = study)
        }
    }
}

@Composable
fun StudyRow(study: Study) {
    Card(
        modifier = Modifier.padding(16.dp),
        elevation = 2.dp) {
        Text(modifier = Modifier.padding(16.dp),
            text = "this is study: ${study.id}")
    }
}