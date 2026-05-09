package ru.ya.practicum.shopper

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import ru.ya.practicum.shopper.feature.auth.AuthDataStore
import ru.ya.practicum.shopper.feature.onboard.OnboardDataStore

class MainScreenCreateListsTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testEmail = BuildConfig.TEST_USER_EMAIL
    private val testPassword = BuildConfig.TEST_USER_PASSWORD

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()

        runBlocking {
            val onboardDataStore = OnboardDataStore(context)
            onboardDataStore.setOnboardCompleted(true)
            onboardDataStore.getOrCreateUserId()

            val authDataStore = AuthDataStore(context)
            authDataStore.clearTokens()
        }
    }

    @Test
    fun createTwoShoppingLists() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()

        composeTestRule.setContent {
            ru.ya.practicum.shopper.core.navigation.NavGraph(
                context = context,
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

        try {
            composeTestRule.onNodeWithText("Электронная почта", ignoreCase = true)
                .assertIsDisplayed()
                .performTextInput(testEmail)

            composeTestRule.onNodeWithText("Пароль", ignoreCase = true)
                .performTextInput(testPassword)

            composeTestRule.onNodeWithText("Войти", ignoreCase = true)
                .performClick()

            Thread.sleep(5000)
            composeTestRule.waitForIdle()

        } catch (e: AssertionError) {
            println("Auth screen not found or already authenticated: ${e.message}")
        }

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
