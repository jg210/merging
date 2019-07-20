package uk.me.jeremygreen.merging.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AppViewModel(application: Application) : AndroidViewModel(application) {

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

    suspend fun onboardingAccepted(version: Long): Boolean {
        return appDatabase.onboardingDao().findById(version) != null
    }

    fun acceptOnboarding(version: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            appDatabase.onboardingDao().add(Onboarding(version))
        }
    }

    fun add(faces: List<Face>) {
        viewModelScope.launch(Dispatchers.IO) {
            appDatabase.runInTransaction {
                viewModelScope.launch(Dispatchers.IO) {
                    val faceEntities = faces.map { face -> FaceEntity(face.id, face.imageId) }
                    val faceIds = appDatabase.faceDao().addAll(faceEntities)
                    faceIds.zip(faces).forEach { pair ->
                        val id = pair.first
                        val face = pair.second
                        // Replace Coordinate instances with new instance with correct face ids.
                        val relatedCoordinates = face.coordinates.map { coordinate ->
                            coordinate.copy(faceId = id)
                        }
                        appDatabase.coordinateDao().addAll(relatedCoordinates)
                    }
                }
            }
        }
    }

}

