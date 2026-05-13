package ru.ya.practicum.shopper

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ApplicationProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import ru.ya.practicum.shopper.feature.auth.AuthDataStore
import ru.ya.practicum.shopper.feature.onboard.OnboardDataStore

class AaMainScreenCreateListsTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val testEmail = BuildConfig.TEST_USER_EMAIL
    private val testPassword = BuildConfig.TEST_USER_PASSWORD

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()

        runBlocking {
            // Очищаем данные перед тестом
            val onboardDataStore = OnboardDataStore(context)
            onboardDataStore.setOnboardCompleted(true)

            val authDataStore = AuthDataStore(context)
            authDataStore.clearUserId()

            // Очищаем Firebase сессию
            FirebaseAuth.getInstance().signOut()
        }
    }

    @After
    fun tearDown() {
        runBlocking {
            val context = ApplicationProvider.getApplicationContext<android.content.Context>()
            val authDataStore = AuthDataStore(context)
            authDataStore.clearUserId()
            FirebaseAuth.getInstance().signOut()
        }
    }

    @Test
    fun createTwoShoppingLists() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()

        composeTestRule.setContent {
            ru.ya.practicum.shopper.core.navigation.NavGraph(
                dataStore = OnboardDataStore(context),
                onThemeToggle = {}
            )
        }

        composeTestRule.waitForIdle()
        Thread.sleep(2000)

        try {
            composeTestRule.onNodeWithText("Нажмите в любом месте", ignoreCase = true)
                .performClick()
            Thread.sleep(1000)
        } catch (e: AssertionError) {
            println("Onboarding not shown")
        }

        performLogin()

        Thread.sleep(2000)
        composeTestRule.waitForIdle()

        createList("Лист покупок Один")
        createList("Лист покупок Два")
        createList("Лист покупок Три")
        createList("Лист покупок Четыре")

        composeTestRule.onNodeWithText("Лист покупок Один")
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("Лист покупок Два")
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("Лист покупок Три")
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("Лист покупок Четыре")
            .assertIsDisplayed()
    }

    private fun performLogin() {
        try {
            composeTestRule.onNodeWithText("Электронная почта", ignoreCase = true)
                .assertIsDisplayed()

            composeTestRule.onNodeWithText("Электронная почта", ignoreCase = true)
                .performTextInput(testEmail)

            composeTestRule.onNodeWithText("Пароль", ignoreCase = true)
                .performTextInput(testPassword)

            composeTestRule.onNodeWithText("Войти", ignoreCase = true)
                .performClick()

            Thread.sleep(3000)
            composeTestRule.waitForIdle()

        } catch (e: AssertionError) {
            println("Auth screen not shown, already on main screen: ${e.message}")
        }
    }

    private fun createList(listName: String) {
        composeTestRule.onNodeWithTag("fab_add_list")
            .assertIsDisplayed()
            .performClick()

        composeTestRule.waitForIdle()
        Thread.sleep(500)

        composeTestRule.onNodeWithText("Название списка")
            .assertIsDisplayed()
            .performTextInput(listName)

        composeTestRule.onNodeWithText("Создать")
            .assertIsDisplayed()
            .performClick()

        composeTestRule.waitForIdle()
        Thread.sleep(1000)
    }
}
