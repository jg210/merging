package uk.me.jeremygreen.merging.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import uk.me.jeremygreen.merging.main.MainActivity
import uk.me.jeremygreen.merging.databinding.SplashBinding
import uk.me.jeremygreen.merging.model.AppViewModel
import uk.me.jeremygreen.merging.onboarding.OnboardingActivity

class SplashActivity: AppCompatActivity(), CoroutineScope by MainScope() {

    companion object {
        private const val SPLASH_SCREEN_MINIMUM_DURATION_MILLIS: Long = 1000
    }

    // Activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = SplashBinding.inflate(this.layoutInflater)
        setContentView(binding.root)
        val appViewModel = AppViewModel.getInstance(this, application)
        launch(Dispatchers.IO) {
            val acceptedVersion = async {
                appViewModel.onboardingAccepted(OnboardingActivity.version)
            }
            val delayJob = launch {
                // Show splash screen for at least this long.
                delay(SPLASH_SCREEN_MINIMUM_DURATION_MILLIS)
            }
            val activity = if (acceptedVersion.await()) {
                MainActivity::class.java
            } else {
                OnboardingActivity::class.java
            }
            delayJob.join()
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
