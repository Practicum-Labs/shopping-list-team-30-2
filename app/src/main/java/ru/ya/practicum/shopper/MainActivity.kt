package ru.ya.practicum.shopper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.first
import ru.ya.practicum.shopper.core.ui.theme.Theme
import ru.ya.practicum.shopper.feature.main.MainScreen
import ru.ya.practicum.shopper.feature.onboard.OnboardDataStore
import ru.ya.practicum.shopper.feature.onboard.OnboardScreen
import ru.ya.practicum.shopper.feature.onboard.OnboardViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Theme {
                val dataStore = remember { OnboardDataStore(applicationContext) }
                val isOnboardCompleted by produceState(initialValue = false) {
                    value = dataStore.isOnboardCompleted.first()
                }

                var showOnboard by remember { mutableStateOf(!isOnboardCompleted) }

                LaunchedEffect(isOnboardCompleted) {
                    showOnboard = !isOnboardCompleted
                }

                Scaffold(
                    containerColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .fillMaxSize()
                ) { innerPadding ->
                    if (showOnboard) {
                        val viewModel: OnboardViewModel = viewModel(
                            factory = OnboardViewModelFactory(dataStore)
                        )
                        OnboardScreen(
                            viewModel = viewModel,
                            onNavigateToMain = { showOnboard = false },
                            modifier = Modifier.padding(innerPadding)
                        )
                    } else {
                        MainScreen(
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
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
