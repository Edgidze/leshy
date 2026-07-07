package compose.project.leshy.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import compose.project.leshy.domain.model.Category
import compose.project.leshy.i18n.categoryDisplayName

@Composable
fun MushroomTile(
    category: Category,
    count: Int,
    onAdd: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val color = runCatching { Color(("ff" + category.colorHex.removePrefix("#")).toLong(16)) }
        .getOrDefault(Color.Gray)

    Card(modifier = modifier.fillMaxWidth()) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.2f)
                    .background(color),
                contentAlignment = Alignment.Center,
            ) {
                Text(categoryDisplayName(category.nameKey))
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onRemove, enabled = count > 0) {
                    Icon(Icons.Filled.Remove, contentDescription = null)
                }
                Text(count.toString())
                IconButton(onClick = onAdd) {
                    Icon(Icons.Filled.Add, contentDescription = null)
                }
            }
        }
    }
}
