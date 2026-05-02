package dam_a46104.jetpackweatherapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dam_a46104.jetpackweatherapp.ui.theme.Coral
import dam_a46104.jetpackweatherapp.ui.theme.GlassLight
import dam_a46104.jetpackweatherapp.ui.theme.TextOnDark

@Composable
fun MetricCard(
    label: String,
    value: String,
    unit: String,
    icon: ImageVector,
    isOnDark: Boolean,
    modifier: Modifier = Modifier
) {
    val cardColor = if (isOnDark) GlassLight else Color(0x99FFFFFF)
    val textColor = if (isOnDark) TextOnDark else Color(0xFF1A1A2E)
    val subtleColor = if (isOnDark) Color(0xB3FFFFFF) else Color(0x991A1A2E)

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = subtleColor,
                    letterSpacing = 1.sp
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Coral,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
            Text(
                text = buildAnnotatedString {
                    withStyle(
                        SpanStyle(
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            color = textColor
                        )
                    ) { append(value) }
                    withStyle(
                        SpanStyle(
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Normal,
                            color = subtleColor
                        )
                    ) { append(" $unit") }
                },
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}