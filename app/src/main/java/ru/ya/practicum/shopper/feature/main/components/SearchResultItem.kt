package ru.ya.practicum.shopper.feature.main.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ru.ya.practicum.shopper.R
import ru.ya.practicum.shopper.core.model.ShoppingList
import ru.ya.practicum.shopper.core.ui.theme.Dimens

@Composable
fun SearchResultItem(
    shoppingList: ShoppingList,
    onClick: (ShoppingList) -> Unit,
    modifier: Modifier = Modifier
) {
    val iconResId = if (shoppingList.iconResId != 0) shoppingList.iconResId else R.drawable.ic_list

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .clickable { onClick(shoppingList) }
            .padding(horizontal = Dimens.dp16, vertical = Dimens.dp16),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = iconResId),
                contentDescription = null,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSecondaryContainer),
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(Dimens.dp16))

        Text(
            text = shoppingList.name,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }

    HorizontalDivider(
        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
    )
}
