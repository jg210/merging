@file:Suppress("unused")

package uk.me.jeremygreen.merging2

import android.app.Application
import com.facebook.drawee.backends.pipeline.Fresco

internal class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Fresco.initialize(this) // Bitmap caching - https://frescolib.org/
    }
}
