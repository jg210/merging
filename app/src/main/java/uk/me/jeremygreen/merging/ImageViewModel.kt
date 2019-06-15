package uk.me.jeremygreen.merging

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.room.Room
import java.io.File

class ImageViewModel(application: Application) : AndroidViewModel(application) {

    val appDatabase: AppDatabase by lazy {
        Room.databaseBuilder(
            application.applicationContext,
            AppDatabase::class.java,
            "app").build()
    }

    fun allImages(): LiveData<List<Image>> {
        return appDatabase.imageDao().getImages()
    }

    suspend fun findById(id: Long): Image {
        return appDatabase.imageDao().findById(id)
    }

    suspend fun delete(image: Image) {
        appDatabase.imageDao().delete(image)
    }

    suspend fun addImage(file: String) {
        appDatabase.imageDao().add(Image(0, file))
    }

}
