package dam_a46104.catsndogs.compose.ui.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import dam_a46104.catsndogs.core.model.ImageItem

@Composable
fun ImageCard(
    imageItem: ImageItem,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick(imageItem.id) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            val breed = imageItem.breeds.firstOrNull()
            val unknownBreed = stringResource(id = dam_a46104.catsndogs.compose.R.string.unknown_breed)
            
            AsyncImage(
                model = imageItem.url,
                contentDescription = stringResource(
                    id = dam_a46104.catsndogs.compose.R.string.content_desc_breed,
                    breed?.name ?: unknownBreed
                ),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            )
            
            if (breed != null) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = breed.name,
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (!breed.bredFor.isNullOrEmpty()) {
                        Text(
                            text = breed.bredFor!!,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}
