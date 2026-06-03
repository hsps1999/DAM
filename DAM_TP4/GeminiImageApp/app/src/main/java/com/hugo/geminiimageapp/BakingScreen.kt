package com.hugo.geminiimageapp

import android.graphics.BitmapFactory
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

val images = arrayOf(
    R.drawable.baked_goods_1,
    R.drawable.baked_goods_2,
    R.drawable.baked_goods_3,
)
val imageDescriptions = arrayOf(
    R.string.image1_description,
    R.string.image2_description,
    R.string.image3_description,
)

@Composable
fun BakingScreen(bakingViewModel: BakingViewModel = viewModel()) {
    val selectedImage = remember { mutableIntStateOf(0) }
    val placeholderPrompt = stringResource(R.string.prompt_placeholder)
    val placeholderResult = stringResource(R.string.results_placeholder)
    var prompt by rememberSaveable { mutableStateOf(placeholderPrompt) }
    var result by rememberSaveable { mutableStateOf(placeholderResult) }
    val uiState by bakingViewModel.uiState.collectAsState()
    val history by bakingViewModel.history.collectAsState()
    val resources = LocalResources.current

    Scaffold { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // Title
            item {
                Text(
                    text = stringResource(R.string.baking_title),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )
            }

            // Image selector row
            item {
                LazyRow(modifier = Modifier.fillMaxWidth()) {
                    itemsIndexed(images) { index, image ->
                        var imageModifier = Modifier
                            .padding(start = 8.dp, end = 8.dp)
                            .requiredSize(200.dp)
                            .clickable { selectedImage.intValue = index }
                        if (index == selectedImage.intValue) {
                            imageModifier = imageModifier.border(
                                BorderStroke(4.dp, MaterialTheme.colorScheme.primary)
                            )
                        }
                        Image(
                            painter = painterResource(image),
                            contentDescription = stringResource(imageDescriptions[index]),
                            modifier = imageModifier
                        )
                    }
                }
            }

            // Prompt + Go button
            item {
                Row(modifier = Modifier.padding(all = 16.dp)) {
                    TextField(
                        value = prompt,
                        label = { Text(stringResource(R.string.label_prompt)) },
                        onValueChange = { prompt = it },
                        modifier = Modifier
                            .weight(0.8f)
                            .padding(end = 16.dp)
                            .align(Alignment.CenterVertically)
                    )
                    Button(
                        onClick = {
                            val bitmap = BitmapFactory.decodeResource(
                                resources,
                                images[selectedImage.intValue]
                            )
                            bakingViewModel.sendPrompt(images[selectedImage.intValue], bitmap, prompt)
                        },
                        enabled = prompt.isNotEmpty(),
                        modifier = Modifier.align(Alignment.CenterVertically)
                    ) {
                        Text(text = stringResource(R.string.action_go))
                    }
                }
            }

            // Current result or loading
            item {
                if (uiState is UiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .wrapContentWidth(Alignment.CenterHorizontally)
                    )
                } else {
                    val textColor = if (uiState is UiState.Error)
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.onSurface

                    if (uiState is UiState.Error) result = (uiState as UiState.Error).errorMessage
                    else if (uiState is UiState.Success) result = (uiState as UiState.Success).outputText

                    Text(
                        text = result,
                        textAlign = TextAlign.Start,
                        color = textColor,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                }
            }

            // History section — only shown after at least one successful request
            if (history.isNotEmpty()) {
                item {
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "History (${history.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }

                items(history.reversed()) { entry ->
                    HistoryItem(entry)
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
private fun HistoryItem(entry: HistoryEntry) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(entry.imageResId),
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outline))
            )
            Column(modifier = Modifier.padding(start = 8.dp)) {
                Text(
                    text = entry.prompt,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = entry.response,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        HorizontalDivider(modifier = Modifier.padding(top = 8.dp))
    }
}

@Preview(showSystemUi = true)
@Composable
fun BakingScreenPreview() {
    BakingScreen()
}
