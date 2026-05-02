package ru.ya.practicum.shopper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import ru.ya.practicum.shopper.core.navigation.NavGraph
import ru.ya.practicum.shopper.core.ui.theme.Theme
import ru.ya.practicum.shopper.feature.onboard.OnboardDataStore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Theme {
                val dataStore = remember { OnboardDataStore(applicationContext) }
                NavGraph(
                    dataStore = dataStore,
                    startDestination = "onboard"
                )
            }
        }
    }
}
