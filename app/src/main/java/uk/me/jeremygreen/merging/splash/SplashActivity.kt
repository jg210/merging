package uk.me.jeremygreen.merging.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.*
import uk.me.jeremygreen.merging.main.MainActivity
import uk.me.jeremygreen.merging.R
import uk.me.jeremygreen.merging.model.AppViewModel
import uk.me.jeremygreen.merging.onboarding.OnboardingActivity

class SplashActivity: AppCompatActivity(), CoroutineScope by MainScope() {

    // Activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash)
        val appViewModelFactory = AppViewModel.Factory(application);
        val appViewModel = ViewModelProvider(this, appViewModelFactory).get(AppViewModel::class.java)
        launch(Dispatchers.IO) {
            val acceptedVersion = async {
                appViewModel.onboardingAccepted(OnboardingActivity.version)
            }
            val delayJob = launch {
                // Show splash screen for at least this long.
                delay(1000)
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