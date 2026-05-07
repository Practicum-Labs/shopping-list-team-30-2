package ru.ya.practicum.shopper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import ru.ya.practicum.shopper.core.navigation.NavGraph
import ru.ya.practicum.shopper.core.ui.theme.Theme
import ru.ya.practicum.shopper.feature.onboard.OnboardDataStore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val systemIsDark = isSystemInDarkTheme()
            var isDarkTheme by remember { mutableStateOf(systemIsDark) }

            Theme(darkTheme = isDarkTheme) {
                val dataStore = remember { OnboardDataStore(applicationContext) }
                NavGraph(
                    context = applicationContext,
                    dataStore = dataStore,
                    onThemeToggle = { isDarkTheme = !isDarkTheme },
                )
            }
        }
    }
}
