package uk.me.jeremygreen.merging.onboarding

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.style.BulletSpan
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import kotlinx.android.synthetic.main.onboarding.*
import uk.me.jeremygreen.merging.R
import uk.me.jeremygreen.merging.databinding.OnboardingBinding
import uk.me.jeremygreen.merging.main.MainActivity
import uk.me.jeremygreen.merging.model.AppViewModel

class OnboardingActivity: AppCompatActivity() {

    companion object {
        // Increase this whenever onboarding text is changed.
        const val version = 2L
    }

    private lateinit var onboardingBinding: OnboardingBinding
    private lateinit var appViewModel: AppViewModel

    // Activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onboardingBinding = OnboardingBinding.inflate(layoutInflater)
        setContentView(R.layout.onboarding)
        appViewModel = AppViewModel.getInstance(this, application)
        setSupportActionBar(onboardingBinding.onboardingToolbar)
        onboardingBinding.onboardingTextContainer.children.forEach { child: View ->
            if (child is TextView) {
                addBullet(child)
            }
        }
    }

    private fun addBullet(textView: TextView) {
        val text = textView.text
        if (text !is String) {
            throw IllegalArgumentException("${text.javaClass} ${text}")
        }
        val spannedText = SpannableString(text)
        val gapWidth = (0.4 * defaultTextSize()).toInt()
        val color = textView.currentTextColor
        val bulletSpan = BulletSpan(gapWidth, color)
        spannedText.setSpan(bulletSpan, 0, text.length, 0)
        textView.text = spannedText
    }

    private fun defaultTextSize(): Float {
        val typedValue = TypedValue()
        theme.resolveAttribute(android.R.attr.textSize, typedValue, true)
        return typedValue.getDimension(resources.displayMetrics)
    }

    // Activity
    override fun onResume() {
        super.onResume()
        onboardingBinding.onboardingAcceptButton.setOnClickListener {
            onboardingBinding.onboardingAcceptButton.setOnClickListener(null)
            appViewModel.acceptOnboarding(version)
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_TASK_ON_HOME
            startActivity(intent)
            finish()
        }
        onboardingBinding.onboardingAcceptCheckbox.setOnClickListener {
            updateFabState()
        }
        updateFabState()
    }

    /**
     * Update FloatingActionButton properties etc.
     */
    private fun updateFabState() {
        if (onboardingBinding.onboardingAcceptCheckbox.isChecked) {
            onboardingBinding.onboardingAcceptButton.show()
        } else {
            onboardingBinding.onboardingAcceptButton.hide()
        }
    }

    // Activity
    override fun onPause() {
        onboardingBinding.onboardingAcceptButton.setOnClickListener(null)
        onboardingBinding.onboardingAcceptCheckbox.setOnClickListener(null)
        super.onPause()
    }

}