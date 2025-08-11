package meow.softer.mydiary

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.NoMatchingRootException
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.Root
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import junit.framework.AssertionFailedError
import meow.softer.mydiary.ui.App
import meow.softer.mydiary.ui.home.MainViewModel
import meow.softer.mydiary.ui.navigation.DiaryNav
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class HomeTest {


    @get:Rule
    val composeTestRule = createComposeRule()

    private val mainViewModel = MainViewModel()
    @Before
    fun setup(){
        composeTestRule.setContent {
            DiaryNav(
                mainViewModel = mainViewModel,
                onTopicClick = {},
                onSettingClick = {},
                onProfileClick = {},
            )
        }
    }

    val context: Context = InstrumentationRegistry.getInstrumentation().targetContext


    @Test
    fun testHomeScreen_displayElements(){
        composeTestRule.apply{
            onNodeWithTag("home_bottom_bar_setting")
                .assertIsDisplayed()
            pressBack()
            onNodeWithText(context.getString(R.string.diary_back_message))
                .assertIsDisplayed()
        }
    }

    @Test
    fun testDoublePressBack() {
        // by pressing twice in a row within 2000ms app should be closed
        // manual check if app is closed
    }

    fun waitForToast(toastText: String, timeoutMillis: Long = 5000L): ViewInteraction {
        val startTime = System.currentTimeMillis()
        while (System.currentTimeMillis() - startTime < timeoutMillis) {
            try {
                // Check if the toast is displayed
                val interaction = onView(withText(toastText))
                    .inRoot(SimpleToastMatcher()) // Or your current ToastMatcher
                    .check(matches(isDisplayed()))
                // If the check passes, return the interaction
                return interaction
            } catch (e: NoMatchingRootException) {
                // Root not found yet, Toast might not be visible or matcher is wrong
                Thread.sleep(100) // Wait a bit before retrying
            } catch (e: NoMatchingViewException) {
                // View with text not found in the identified root, Toast might not have this text
                Thread.sleep(100)
            } catch (e: AssertionFailedError) {
                // View found but assertion (e.g., isDisplayed) failed.
                Thread.sleep(100)
            }
        }
        // If timeout is reached, try one last time to get a meaningful failure
        return onView(withText(toastText))
            .inRoot(SimpleToastMatcher())
            .check(matches(isDisplayed()))
    }

    @Test
    fun testPressBackOnce_displaysExitConfirmationToast() {

    }

}