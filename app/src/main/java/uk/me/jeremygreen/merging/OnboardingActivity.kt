package uk.me.jeremygreen.merging

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.style.BulletSpan
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import kotlinx.android.synthetic.main.onboarding.*

class OnboardingActivity: AppCompatActivity() {

    // Activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.onboarding)
        setSupportActionBar(onboardingToolbar)
        this.onboardingTextContainer.children.forEach { child: View ->
            if (child is TextView) {
                addBullet(child)
            }
        }
    }

    private fun addBullet(textView: TextView) {
        val text = textView.text
        if (!(text is String)) {
            throw IllegalArgumentException("${text.javaClass} ${text}")
        }
        val spannedText = SpannableString(text)
        val bulletSpan = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val defaultTextSize = resources.getDimensionPixelSize(R.dimen.defaultTextSize)
            val gapWidth = defaultTextSize / 2
            val radius = gapWidth / 2
            val color = textView.currentTextColor
            BulletSpan(gapWidth, color, radius)
        } else {
            BulletSpan()
        }
        spannedText.setSpan(bulletSpan, 0, text.length, 0)
        textView.text = spannedText
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