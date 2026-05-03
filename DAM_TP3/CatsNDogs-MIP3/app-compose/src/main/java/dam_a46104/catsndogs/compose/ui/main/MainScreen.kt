package dam_a46104.catsndogs.compose.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dam_a46104.catsndogs.compose.ui.common.LoadingIndicator
import dam_a46104.catsndogs.compose.viewmodel.MainViewModel
import dam_a46104.catsndogs.core.common.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    onImageClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val favorites by viewModel.favorites.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = dam_a46104.catsndogs.compose.R.string.title_home)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.loadImages() }) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = stringResource(id = dam_a46104.catsndogs.compose.R.string.content_desc_refresh))
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LoadingIndicator(visible = uiState is UiState.Loading)
            
            when (val state = uiState) {
                is UiState.Loading -> {
                    // Handled by LoadingIndicator outside when
                }
                
                is UiState.Success -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        item {
                            FavoritesBar(
                                favorites = favorites,
                                onImageClick = onImageClick
                            )
                        }
                        items(state.data) { imageItem ->
                            ImageCard(
                                imageItem = imageItem,
                                onClick = onImageClick
                            )
                        }
                    }

                    if (state.isFromCache) {
                        val cacheMessage = stringResource(id = dam_a46104.catsndogs.core.R.string.info_offline_cache)
                        LaunchedEffect(state) {
                            snackbarHostState.showSnackbar(
                                message = cacheMessage,
                                duration = SnackbarDuration.Short
                            )
                        }
                    }
                }
                
                is UiState.Error -> {
                    val errorMessage = stringResource(id = state.messageResId)
                    val retryLabel = stringResource(id = dam_a46104.catsndogs.compose.R.string.action_retry)
                    LaunchedEffect(state) {
                        val result = snackbarHostState.showSnackbar(
                            message = errorMessage,
                            actionLabel = retryLabel,
                            duration = SnackbarDuration.Indefinite
                        )
                        if (result == SnackbarResult.ActionPerformed) {
                            viewModel.loadImages()
                        }
                    }
                }
            }
        }
    }
}
