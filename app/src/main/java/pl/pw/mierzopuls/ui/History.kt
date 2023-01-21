package pl.pw.mierzopuls.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import pl.pw.mierzopuls.R
import pl.pw.mierzopuls.model.Study
import pl.pw.mierzopuls.model.toDisplay
import pl.pw.mierzopuls.ui.components.*
import pl.pw.mierzopuls.ui.theme.test
import pl.pw.mierzopuls.util.SampleData

@OptIn(ExperimentalMaterialApi::class)
@ExperimentalFoundationApi
@Composable
fun HistoryBottomSheet(
    studies: List<Study>,
    homeContent: @Composable () -> Unit
) {
    val sheetState = rememberBottomSheetState(initialValue = BottomSheetValue.Collapsed)
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = sheetState
    )
    val coroutineScope = rememberCoroutineScope()
    BottomSheetScaffold(
        scaffoldState = bottomSheetScaffoldState,
        sheetPeekHeight = 80.dp,
        sheetContent = {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            stickyHeader {
                Surface(color = test) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(80.dp)) {
                        Text(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .fillMaxWidth()
                                .height(80.dp)
                                .padding(16.dp),
                            text = stringResource(id = R.string.app_history),
                            fontFamily = FontFamily.Default,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                        FloatingActionButton(
                            modifier = Modifier
                                .padding(8.dp)
                                .align(Alignment.CenterEnd),
                            backgroundColor = MaterialTheme.colors.primary,
                            onClick = {
                                coroutineScope.launch {
                                    if (sheetState.isCollapsed) {
                                        sheetState.expand()
                                    } else {
                                        sheetState.collapse()
                                    }
                                }
                            }) {
                            ArrowIndicator(isExpanded = bottomSheetScaffoldState.bottomSheetState.isCollapsed.not())
                        }
                    }
                }
            }
            items(studies) { study ->
                StudyRow(study = study)
            }
        }
    }) {
        Box(
            Modifier
                .fillMaxSize()
                .padding(it)
                .animateContentSize()
        ) { homeContent() }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun StudyRow(study: Study) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(modifier = Modifier
        .padding(16.dp)
        .fillMaxWidth()
        .height(if (isExpanded) 272.dp else 72.dp),
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
                    horizontalAlignment = Alignment.End
                ) {
                    FloatingActionButton(
                        modifier = Modifier
                            .padding(8.dp)
                            .size(48.dp),
                        backgroundColor = MaterialTheme.colors.primary,
                        onClick = {  }
                    ) {
                        Icon(modifier = Modifier.padding(16.dp),
                            painter = painterResource(id = R.drawable.ic_save),
                            contentDescription = ""
                        )
                    }
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
                    study = study,
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