package ru.ya.practicum.shopper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import ru.ya.practicum.shopper.core.navigation.NavGraph
import ru.ya.practicum.shopper.core.ui.theme.Theme
import ru.ya.practicum.shopper.feature.onboard.OnboardDataStore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val dataStore = remember { OnboardDataStore(applicationContext) }
            val scope = rememberCoroutineScope()
            val isDarkTheme by dataStore.isDarkTheme.collectAsState(initial = false)

            Theme(darkTheme = isDarkTheme) {
                NavGraph(
                    dataStore = dataStore,
                    onThemeToggle = {
                        scope.launch {
                            dataStore.setDarkTheme(!isDarkTheme)
                        }
                    },
                    startDestination = "onboard"
                )
            }
        }
    }
}
