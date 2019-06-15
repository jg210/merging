package uk.me.jeremygreen.merging

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.room.Room
import java.io.File

class ImageViewModel(application: Application) : AndroidViewModel(application) {

    val appDatabase: AppDatabase by lazy {
        Room.databaseBuilder(
            application.applicationContext,
            AppDatabase::class.java,
            "app").build()
    }

    val images = appDatabase.imageDao().getImages()

    fun findById(id: Long): Image {
        return appDatabase.imageDao().findById(id)
    }

    suspend fun delete(image: Image) {
        appDatabase.imageDao().delete(image)
    }

    suspend fun addImage(file: String) {
        appDatabase.imageDao().add(Image(0, file))
    }

}

