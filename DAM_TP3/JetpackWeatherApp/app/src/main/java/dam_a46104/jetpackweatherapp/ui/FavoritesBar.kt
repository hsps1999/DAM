package dam_a46104.jetpackweatherapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import dam_a46104.jetpackweatherapp.R
import dam_a46104.jetpackweatherapp.data.FavoriteLocation
import dam_a46104.jetpackweatherapp.ui.theme.Coral
import dam_a46104.jetpackweatherapp.ui.theme.GlassLight
import dam_a46104.jetpackweatherapp.ui.theme.TextOnDark
import dam_a46104.jetpackweatherapp.ui.theme.TextSubtleOnDark

@Composable
fun FavoritesBar(
    favorites: List<FavoriteLocation>,
    isOnDark: Boolean,
    onFavoriteTap: (FavoriteLocation) -> Unit,
    onFavoriteDelete: (FavoriteLocation) -> Unit,
    onAddFavorite: (String) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    val textColor = if (isOnDark) TextOnDark else Color(0xFF1A1A2E)
    val subtleColor = if (isOnDark) TextSubtleOnDark else Color(0x991A1A2E)
    val cardColor = if (isOnDark) GlassLight else Color(0x99FFFFFF)

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.favorites).uppercase(),
                style = TextStyle(
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 1.sp,
                    color = subtleColor
                )
            )
            IconButton(
                onClick = { showDialog = true },
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_favorite),
                    tint = Coral,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (favorites.isEmpty()) {
            Text(
                text = stringResource(R.string.no_favorites),
                style = TextStyle(fontSize = 12.sp, color = subtleColor)
            )
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(favorites) { favorite ->
                    FavoriteCard(
                        favorite = favorite,
                        cardColor = cardColor,
                        textColor = textColor,
                        subtleColor = subtleColor,
                        onTap = { onFavoriteTap(favorite) },
                        onDelete = { onFavoriteDelete(favorite) }
                    )
                }
            }
        }
    }

    if (showDialog) {
        AddFavoriteDialog(
            isOnDark = isOnDark,
            onConfirm = { name ->
                onAddFavorite(name)
                showDialog = false
            },
            onDismiss = { showDialog = false }
        )
    }
}

@Composable
fun FavoriteCard(
    favorite: FavoriteLocation,
    cardColor: Color,
    textColor: Color,
    subtleColor: Color,
    onTap: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(90.dp)
            .clickable { onTap() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box {
            Column(
                modifier = Modifier.padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = favorite.name,
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    ),
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${favorite.latitude.toInt()}°",
                    style = TextStyle(fontSize = 10.sp, color = subtleColor)
                )
            }
            // Botão X para apagar
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(Coral)
                    .clickable { onDelete() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(R.string.delete_favorite),
                    tint = TextOnDark,
                    modifier = Modifier.size(10.dp)
                )
            }
        }
    }
}

@Composable
fun AddFavoriteDialog(
    isOnDark: Boolean,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    val textColor = if (isOnDark) TextOnDark else Color(0xFF1A1A2E)
    val subtleColor = if (isOnDark) TextSubtleOnDark else Color(0x991A1A2E)

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isOnDark) Color(0xFF2A2A3E) else Color(0xFFF5F5F5)
            )
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = stringResource(R.string.add_favorite).uppercase(),
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = textColor
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = {
                        Text(
                            stringResource(R.string.favorite_name),
                            style = TextStyle(fontSize = 12.sp)
                        )
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Coral,
                        unfocusedBorderColor = subtleColor,
                        focusedLabelColor = Coral,
                        focusedTextColor = textColor,
                        unfocusedTextColor = textColor,
                        cursorColor = Coral
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(
                            stringResource(R.string.cancel),
                            color = subtleColor
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(
                        onClick = { if (name.isNotBlank()) onConfirm(name.trim()) },
                    ) {
                        Text(stringResource(R.string.save), color = Coral)
                    }
                }
            }
        }
    }
}