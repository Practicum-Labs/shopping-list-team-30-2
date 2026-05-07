package ru.ya.practicum.shopper.core.navigation

import android.content.Context
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
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.flow.first
import org.koin.compose.koinInject
import ru.ya.practicum.shopper.feature.auth.AuthDataStore
import ru.ya.practicum.shopper.feature.auth.AuthScreen
import ru.ya.practicum.shopper.feature.auth.AuthViewModel
import ru.ya.practicum.shopper.feature.main.MainScreen
import ru.ya.practicum.shopper.feature.onboard.OnboardDataStore
import ru.ya.practicum.shopper.feature.onboard.OnboardScreen
import ru.ya.practicum.shopper.feature.onboard.OnboardViewModel
import ru.ya.practicum.shopper.feature.product.ProductScreen

sealed class Screen(val route: String) {
    object Onboard : Screen("onboard")
    object Auth : Screen("auth")
    object Main : Screen("main")
    object Product : Screen("product/{listId}/{listName}") {
        fun passArguments(listId: Int, listName: String): String {
            return "product/$listId/$listName"
        }
    }
}

@Composable
fun NavGraph(
    context: Context,
    dataStore: OnboardDataStore,
    onThemeToggle: () -> Unit,
) {
    val navController = rememberNavController()

    var isLoading by remember { mutableStateOf(true) }
    var isOnboardCompleted by remember { mutableStateOf(false) }
    var isAuthenticated by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isOnboardCompleted = dataStore.isOnboardCompleted.first()

        if (isOnboardCompleted) {
            val authDataStore = AuthDataStore(context)
            val token = authDataStore.getAccessToken()
            isAuthenticated = token != null
        }

        isLoading = false
    }

    if (isLoading) {
        LoadingScreen()
        return
    }

    val actualStartDestination = when {
        !isOnboardCompleted -> Screen.Onboard.route
        !isAuthenticated -> Screen.Auth.route
        else -> Screen.Main.route
    }

    AppNavHost(
        navController = navController,
        dataStore = dataStore,
        onThemeToggle = onThemeToggle,
        startDestination = actualStartDestination
    )
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    dataStore: OnboardDataStore,
    onThemeToggle: () -> Unit,
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        onboardScreen(navController, dataStore)
        authScreen(navController)
        mainScreen(navController, onThemeToggle)
        productScreen(navController)
    }
}

private fun NavGraphBuilder.onboardScreen(
    navController: NavHostController,
    dataStore: OnboardDataStore
) {
    composable(Screen.Onboard.route) {
        val viewModel: OnboardViewModel = viewModel(
            factory = OnboardViewModelFactory(dataStore)
        )
        OnboardScreen(
            viewModel = viewModel,
            onNavigateToMain = {
                navController.navigate(Screen.Auth.route) {
                    popUpTo(Screen.Onboard.route) { inclusive = true }
                }
            }
        )
    }
}

private fun NavGraphBuilder.authScreen(
    navController: NavHostController
) {
    composable(Screen.Auth.route) {
        val viewModel: AuthViewModel = koinInject()
        AuthScreen(
            viewModel = viewModel,
            onAuthSuccess = {
                navController.navigate(Screen.Main.route) {
                    popUpTo(Screen.Auth.route) { inclusive = true }
                }
            }
        )
    }
}

private fun NavGraphBuilder.mainScreen(
    navController: NavHostController,
    onThemeToggle: () -> Unit
) {
    composable(Screen.Main.route) {
        MainScreen(
            onNavigateToProduct = { listId, listName ->
                navController.navigate(Screen.Product.passArguments(listId, listName))
            },
            onThemeToggle = onThemeToggle
        )
    }
}

private fun NavGraphBuilder.productScreen(
    navController: NavHostController
) {
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
