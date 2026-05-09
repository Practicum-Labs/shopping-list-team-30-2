package ru.ya.practicum.shopper.feature.main

import ru.ya.practicum.shopper.domain.usecase.list.CreateListUseCase
import ru.ya.practicum.shopper.domain.usecase.list.DeleteAllListsUseCase
import ru.ya.practicum.shopper.domain.usecase.list.DeleteListUseCase
import ru.ya.practicum.shopper.domain.usecase.list.GetListsUseCase
import ru.ya.practicum.shopper.domain.usecase.list.MapListsUseCase
import ru.ya.practicum.shopper.domain.usecase.list.UpdateListIconUseCase
import ru.ya.practicum.shopper.domain.usecase.list.UpdateListNameUseCase

data class MainUseCases(
    val getLists: GetListsUseCase,
    val createList: CreateListUseCase,
    val deleteList: DeleteListUseCase,
    val updateListName: UpdateListNameUseCase,
    val updateListIcon: UpdateListIconUseCase,
    val deleteAllLists: DeleteAllListsUseCase,
    val mapLists: MapListsUseCase
)
