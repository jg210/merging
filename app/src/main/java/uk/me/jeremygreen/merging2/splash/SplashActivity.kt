package uk.me.jeremygreen.merging2.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import uk.me.jeremygreen.merging2.main.MainActivity
import uk.me.jeremygreen.merging2.model.AppViewModel
import uk.me.jeremygreen.merging2.onboarding.OnboardingActivity

@SuppressLint("CustomSplashScreen")
internal class SplashActivity: AppCompatActivity(), CoroutineScope by MainScope() {

    // Activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // No UI!
        val appViewModel = AppViewModel.getInstance(this, application)
        launch(Dispatchers.IO) {
            val acceptedVersion = async {
                appViewModel.onboardingAccepted(OnboardingActivity.VERSION)
            }
            val activity = if (acceptedVersion.await()) {
                MainActivity::class.java
            } else {
                OnboardingActivity::class.java
            }
            val intent = Intent(this@SplashActivity, activity)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_TASK_ON_HOME
            startActivity(intent)
            finish()
        }
    }

    // Activity
    override fun onDestroy() {
        cancel() // CoroutineScope
        super.onDestroy()
    }

}
