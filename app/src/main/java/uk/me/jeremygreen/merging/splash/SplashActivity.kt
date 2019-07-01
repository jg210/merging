package uk.me.jeremygreen.merging.splash

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.style.BulletSpan
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.onboarding.*
import kotlinx.coroutines.*
import uk.me.jeremygreen.merging.main.MainActivity
import uk.me.jeremygreen.merging.R
import uk.me.jeremygreen.merging.model.ImageViewModel
import uk.me.jeremygreen.merging.onboarding.OnboardingActivity
import kotlin.coroutines.CoroutineContext

class SplashActivity: AppCompatActivity(), CoroutineScope by MainScope() {

    // Activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash)
        val imageViewModel = ViewModelProviders.of(this).get(ImageViewModel::class.java)
        launch(Dispatchers.IO) {
            val activity = if (imageViewModel.onboardingAccepted(OnboardingActivity.version)) {
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