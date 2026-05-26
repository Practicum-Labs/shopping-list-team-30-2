package ru.ya.practicum.shopper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import ru.ya.practicum.shopper.core.navigation.NavGraph
import ru.ya.practicum.shopper.core.ui.theme.Theme
import ru.ya.practicum.shopper.core.ui.theme.ThemeDataStore
import ru.ya.practicum.shopper.feature.onboard.OnboardDataStore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ShopperApp()
        }
    }

    @Composable
    private fun ShopperApp() {
        val onboardDataStore = remember { OnboardDataStore(applicationContext) }
        val themeDataStore = remember { ThemeDataStore(applicationContext) }
        val scope = rememberCoroutineScope()

        val systemDarkTheme = isSystemInDarkTheme()

        var isDarkTheme by remember { mutableStateOf(systemDarkTheme) }
        var isReady by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            val hasUserPreference = themeDataStore.hasUserThemePreference.first()
            if (hasUserPreference) {
                val savedTheme = themeDataStore.isDarkTheme.first()
                isDarkTheme = savedTheme ?: systemDarkTheme
            } else {
                isDarkTheme = systemDarkTheme
            }
            isReady = true
        }

        if (isReady) {
            Theme(darkTheme = isDarkTheme) {
                NavGraph(
                    dataStore = onboardDataStore,
                    onThemeToggle = {
                        scope.launch {
                            val newTheme = !isDarkTheme
                            isDarkTheme = newTheme
                            themeDataStore.setDarkTheme(newTheme, isUserPreference = true)
                        }
                    }
                )
            }
        } else {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}
