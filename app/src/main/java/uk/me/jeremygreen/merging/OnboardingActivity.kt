package uk.me.jeremygreen.merging

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.onboarding.*

class OnboardingActivity: AppCompatActivity() {

    // Activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.onboarding)
        setSupportActionBar(onboardingToolbar)
    }

    // Activity
    override fun onResume() {
        super.onResume()
        onboarding_accept.setOnClickListener {
            onboarding_accept.setOnClickListener(null)
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_TASK_ON_HOME
            startActivity(intent)
            finish()
        }
    }

    // Activity
    override fun onPause() {
        onboarding_accept.setOnClickListener(null)
        super.onPause()
    }

}