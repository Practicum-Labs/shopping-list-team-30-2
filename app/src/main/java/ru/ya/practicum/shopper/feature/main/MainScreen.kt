//package ru.ya.practicum.shopper.feature.main
//
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material3.Card
//import androidx.compose.material3.CardDefaults
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.FloatingActionButton
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Text
//import androidx.compose.material3.rememberModalBottomSheetState
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.saveable.rememberSaveable
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import ru.ya.practicum.shopper.core.ui.theme.Dimens
//import ru.ya.practicum.shopper.feature.main.components.IconsModalBottomSheet
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun MainScreen(
//    modifier: Modifier = Modifier
//) {
//    Column(
//        modifier = modifier
//            .fillMaxSize()
//            .padding(Dimens.dp16),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center
//    ) {
//        Text(
//            text = "Мои списки",
//            style = MaterialTheme.typography.headlineMedium
//        )
//
//        Spacer(modifier = Modifier.height(Dimens.dp32))
//
//        Card(
//            modifier = Modifier.fillMaxWidth(),
//            elevation = CardDefaults.cardElevation(Dimens.dp4)
//        ) {
//            Column(
//                modifier = Modifier.padding(Dimens.dp24),
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                Text(
//                    text = "У вас пока нет списков",
//                    style = MaterialTheme.typography.titleMedium
//                )
//
//                Spacer(modifier = Modifier.height(Dimens.dp16))
//
//                Text(
//                    text = "Нажмите на кнопку + ниже, чтобы создать свой первый список",
//                    style = MaterialTheme.typography.bodyMedium
//                )
//            }
//        }
//        ShowBottomSheet()
//        Spacer(modifier = Modifier.weight(1f))
//
//        FloatingActionButton(
//            onClick = { },
//            modifier = Modifier.fillMaxWidth(Dimens.ZERO_FIVE)
//        ) {
//            Text("+", fontSize = Dimens.sp32)
//        }
//    }
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun ShowBottomSheet() {
//    var bottomSheetIsOpen by rememberSaveable { mutableStateOf(false) }
//
//    if (bottomSheetIsOpen) {
//        IconsModalBottomSheet(
//            bottomSheetState = rememberModalBottomSheetState(
//                skipPartiallyExpanded = true
//            ),
//            onDismissRequest = {},
//            onIconClick = {}
//        )
//    }
//}

package ru.ya.practicum.shopper.feature.main

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ru.ya.practicum.shopper.core.ui.theme.Dimens
import ru.ya.practicum.shopper.feature.main.components.MainCreateList
import ru.ya.practicum.shopper.feature.main.components.MainEmptyContent
import ru.ya.practicum.shopper.feature.main.components.MainTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            MainTopBar(
                onSearchClick = { },
                onDeleteClick = { },
                onThemeClick = { }
            )
        },
        floatingActionButton = {
            MainCreateList(onClick = { })
        },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        MainEmptyContent(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = Dimens.dp16),
        )
    }
}