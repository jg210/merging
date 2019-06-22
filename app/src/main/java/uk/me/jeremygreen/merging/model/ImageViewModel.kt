package uk.me.jeremygreen.merging.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ImageViewModel(application: Application) : AndroidViewModel(application) {

    private val appDatabase: AppDatabase by lazy {
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

    fun delete(image: Image) {
        viewModelScope.launch(Dispatchers.IO) {
            appDatabase.imageDao().delete(image)
        }
    }

    fun addImage(file: String) {
        viewModelScope.launch(Dispatchers.IO) {
            appDatabase.imageDao().add(Image(0, file))
        }
    }

}

