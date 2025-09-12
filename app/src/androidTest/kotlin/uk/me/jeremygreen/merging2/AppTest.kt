package uk.me.jeremygreen.merging2

import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import uk.me.jeremygreen.merging2.splash.SplashActivity

@RunWith(AndroidJUnit4::class)
class AppTest {

    @get:Rule
    internal val composeTestRule = createAndroidComposeRule<SplashActivity>()

    @Test
    fun appLaunches() {
        composeTestRule
            .onNode(hasTestTag("topBarTitle"))
            .assertTextEquals("Merging")
    }

}