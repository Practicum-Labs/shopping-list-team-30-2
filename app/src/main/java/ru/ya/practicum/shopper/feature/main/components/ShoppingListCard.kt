package ru.ya.practicum.shopper.feature.main.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ru.ya.practicum.shopper.R
import ru.ya.practicum.shopper.core.model.ShoppingList
import ru.ya.practicum.shopper.core.ui.theme.Dimens

@Composable
fun ShoppingListCard(
    shoppingList: ShoppingList,
    onClick: (ShoppingList) -> Unit,
    modifier: Modifier = Modifier
) {
    val iconResId = getValidIconResId(shoppingList.iconResId)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimens.dp12),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp,
            pressedElevation = 3.dp
        )
    ) {
        ShoppingListCardContent(
            shoppingList = shoppingList,
            iconResId = iconResId,
            onClick = onClick
        )
    }
}

@Composable
private fun ShoppingListCardContent(
    shoppingList: ShoppingList,
    iconResId: Int,
    onClick: (ShoppingList) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dimens.dp16))
            .background(MaterialTheme.colorScheme.inverseOnSurface)
            .clickable { onClick(shoppingList) }
            .padding(Dimens.dp8),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ListIcon(iconResId = iconResId)
        Spacer(modifier = Modifier.width(Dimens.dp16))
        ListName(name = shoppingList.name)
    }
}

@Composable
private fun ListIcon(iconResId: Int) {
    Box(
        modifier = Modifier
            .size(40.dp)
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
}

@Composable
private fun ListName(name: String) {
    Column {
        Text(
            text = name,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

private fun getValidIconResId(iconResId: Int): Int {
    return if (iconResId != 0) iconResId else R.drawable.ic_list
}
