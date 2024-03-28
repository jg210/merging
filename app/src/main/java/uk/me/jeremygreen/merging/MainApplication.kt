@file:Suppress("unused")

package uk.me.jeremygreen.merging

import android.app.Application
import com.facebook.drawee.backends.pipeline.Fresco

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Fresco.initialize(this) // Bitmap caching - https://frescolib.org/
    }
}
