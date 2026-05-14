package ru.ya.practicum.shopper.feature.product.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import ru.ya.practicum.shopper.core.model.Product
import ru.ya.practicum.shopper.core.ui.theme.Dimens

@Composable
fun ChangeProductBottomSheet(
    onDismiss: (ProductAddBottomSheetState) -> Unit,
    productState: Product?,
) {
    var state by remember {
        mutableStateOf(
            ProductAddBottomSheetState(
                productName = productState?.name ?: "",
                quantity = productState?.amount ?: "",
                selectedUnit = productState?.unit ?: ""
            )
        )
    }

    val density = LocalDensity.current
    var sheetTopPx by remember { mutableStateOf(0f) }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
    ) {
        DimmedBackground(onDismiss = {
            onDismiss(state)
        })

        BottomSheetContent(
            onSheetPositioned = { sheetTopPx = it },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .height(Dimens.dp480)
        ) {
            ProductFormFields(
                state = state,
                onStateChange = { state = it }
            )
        }
    }
}