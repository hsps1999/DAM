package dam_a46104.catsndogs.compose.ui.details

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import dam_a46104.catsndogs.compose.viewmodel.DetailsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    viewModel: DetailsViewModel,
    onNavigateBack: () -> Unit
) {
    val image by viewModel.image.collectAsStateWithLifecycle()
    val isFavorite by viewModel.isFavorite.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = dam_a46104.catsndogs.compose.R.string.title_details)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = dam_a46104.catsndogs.compose.R.string.content_desc_back)
                        )
                    }
                },
                actions = {
                    // Sem animação no toggle (M5 tratará disto mais tarde)
                    IconButton(onClick = { viewModel.toggleFavorite() }) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                            contentDescription = stringResource(
                                id = if (isFavorite) dam_a46104.catsndogs.compose.R.string.content_desc_remove_favorite
                                else dam_a46104.catsndogs.compose.R.string.content_desc_add_favorite
                            ),
                            tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        if (image == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding), 
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            val currentImage = image!!
            val breed = currentImage.breeds.firstOrNull()
            val unknownBreed = stringResource(id = dam_a46104.catsndogs.compose.R.string.unknown_breed)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
            ) {
                AsyncImage(
                    model = currentImage.url,
                    contentDescription = stringResource(
                        id = dam_a46104.catsndogs.compose.R.string.content_desc_breed,
                        breed?.name ?: unknownBreed
                    ),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                )

                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(
                            id = dam_a46104.catsndogs.compose.R.string.label_breed,
                            breed?.name ?: unknownBreed
                        ),
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    if (!breed?.breedGroup.isNullOrEmpty()) {
                        Text(
                            text = stringResource(
                                id = dam_a46104.catsndogs.compose.R.string.label_group,
                                breed!!.breedGroup!!
                            ),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                    
                    if (!breed?.bredFor.isNullOrEmpty()) {
                        Text(
                            text = stringResource(
                                id = dam_a46104.catsndogs.compose.R.string.label_bred_for,
                                breed!!.bredFor!!
                            ),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = stringResource(
                            id = dam_a46104.catsndogs.compose.R.string.label_id,
                            currentImage.id
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(
                            id = dam_a46104.catsndogs.compose.R.string.label_url,
                            currentImage.url
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
