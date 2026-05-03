package ru.ya.practicum.shopper.core.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.flow.first
import ru.ya.practicum.shopper.feature.main.MainScreen
import ru.ya.practicum.shopper.feature.onboard.OnboardDataStore
import ru.ya.practicum.shopper.feature.onboard.OnboardScreen
import ru.ya.practicum.shopper.feature.onboard.OnboardViewModel
import ru.ya.practicum.shopper.feature.product.ProductScreen

sealed class Screen(val route: String) {
    object Onboard : Screen("onboard")
    object Main : Screen("main")
    object Product : Screen("product/{listId}/{listName}") {
        fun passArguments(listId: Int, listName: String): String {
            return "product/$listId/$listName"
        }
    }
}

@Composable
fun NavGraph(
    dataStore: OnboardDataStore,
    startDestination: String = Screen.Onboard.route
) {
    val navController = rememberNavController()

    var isLoading by remember { mutableStateOf(true) }
    var isOnboardCompleted by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isOnboardCompleted = dataStore.isOnboardCompleted.first()
        isLoading = false
    }

    if (isLoading) {
        LoadingScreen()
        return
    }

    AppNavHost(
        navController = navController,
        dataStore = dataStore,
        startDestination = if (isOnboardCompleted) Screen.Main.route else startDestination
    )
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    dataStore: OnboardDataStore,
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Onboard.route) {
            val viewModel: OnboardViewModel = viewModel(
                factory = OnboardViewModelFactory(dataStore)
            )
            OnboardScreen(
                viewModel = viewModel,
                onNavigateToMain = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Onboard.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Main.route) {
            MainScreen(
                onNavigateToProduct = { listId, listName ->
                    navController.navigate(Screen.Product.passArguments(listId, listName))
                }
            )
        }

        composable(
            route = Screen.Product.route,
            arguments = listOf(
                navArgument("listId") { type = NavType.IntType },
                navArgument("listName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val listId = backStackEntry.arguments?.getInt("listId") ?: 0
            val listName = backStackEntry.arguments?.getString("listName") ?: ""
            ProductScreen(
                listId = listId,
                listName = listName,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}

class OnboardViewModelFactory(
    private val dataStore: OnboardDataStore
) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OnboardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OnboardViewModel(dataStore) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}
