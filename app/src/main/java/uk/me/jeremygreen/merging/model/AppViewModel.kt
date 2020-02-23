package uk.me.jeremygreen.merging.model

import android.app.Application
import androidx.room.Room

class AppViewModel(application: Application)
    : AppViewModelImpl(application, createAppDatabase(application) ) {

    companion object {

        private fun createAppDatabase(application: Application): AppDatabase {
            val builder = Room.databaseBuilder(
                application.applicationContext,
                AppDatabase::class.java,
                "app"
            )
            builder.fallbackToDestructiveMigrationFrom(1)
            return builder.build()
        }

    }

}