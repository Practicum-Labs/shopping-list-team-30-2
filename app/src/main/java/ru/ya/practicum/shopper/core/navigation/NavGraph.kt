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
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.flow.first
import org.koin.compose.viewmodel.koinViewModel
import ru.ya.practicum.shopper.feature.auth.AuthDataStore
import ru.ya.practicum.shopper.feature.auth.AuthScreen
import ru.ya.practicum.shopper.feature.auth.RecoveryScreen
import ru.ya.practicum.shopper.feature.auth.SignUpScreen
import ru.ya.practicum.shopper.feature.main.MainScreen
import ru.ya.practicum.shopper.feature.onboard.OnboardDataStore
import ru.ya.practicum.shopper.feature.onboard.OnboardScreen
import ru.ya.practicum.shopper.feature.onboard.OnboardViewModel
import ru.ya.practicum.shopper.feature.product.ProductScreen

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
        onThemeToggle = onThemeToggle,
        startDestination = actualStartDestination
    )
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    onThemeToggle: () -> Unit,
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        onboardScreen(navController)
        authScreen(navController)
        signUpScreen(navController)
        recoveryScreen(navController)
        mainScreen(navController, onThemeToggle)
        productScreen(navController)
    }
}

private fun NavGraphBuilder.onboardScreen(
    navController: NavHostController
) {
    composable(Screen.Onboard.route) {
        val viewModel: OnboardViewModel = koinViewModel()
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
        AuthScreen(
            onAuthSuccess = {
                navController.navigate(Screen.Main.route) {
                    popUpTo(Screen.Auth.route) { inclusive = true }
                }
            },
            onNavigateToSignUp = {
                navController.navigate(Screen.SignUp.route)
            },
            onNavigateToRecovery = {
                navController.navigate(Screen.Recovery.route)
            }
        )
    }
}

private fun NavGraphBuilder.signUpScreen(
    navController: NavHostController
) {
    composable(Screen.SignUp.route) {
        SignUpScreen(
            onBackClick = { navController.popBackStack() },
            onRegistrationSuccess = {
                navController.navigate(Screen.Main.route) {
                    popUpTo(Screen.Auth.route) { inclusive = true }
                }
            }
        )
    }
}

private fun NavGraphBuilder.recoveryScreen(
    navController: NavHostController
) {
    composable(Screen.Recovery.route) {
        RecoveryScreen(
            onBackClick = { navController.popBackStack() },
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

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

sealed class Screen(val route: String) {
    object Onboard : Screen("onboard")
    object Auth : Screen("auth")
    object SignUp : Screen("sign_up")
    object Recovery : Screen("recovery")
    object Main : Screen("main")
    object Product : Screen("product/{listId}/{listName}") {
        fun passArguments(listId: Int, listName: String): String {
            return "product/$listId/$listName"
        }
    }
}
