package uk.me.jeremygreen.merging.model

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AppViewModel(
    application: Application,
    private val appDatabase: AppDatabase)
    : AndroidViewModel(application) {

    companion object {
        private val TAG = "AppViewModelImpl"
        private fun createAppDatabase(application: Application): AppDatabase {
            val builder = Room.databaseBuilder(
                application.applicationContext,
                AppDatabase::class.java,
                "app"
            )
            builder.fallbackToDestructiveMigrationFrom(1)
            return builder.build()
        }
        fun create(owner: ViewModelStoreOwner, application: Application) =
            ViewModelProvider(
                owner,
                ViewModelProvider.AndroidViewModelFactory.getInstance(application)
            ).get(AppViewModel::class.java)
    }

    constructor(application: Application): this(application, createAppDatabase(application)) {}

    fun allImages(): LiveData<List<Image>> {
        return appDatabase.imageDao().getImages()
    }

    suspend fun getProcessingStage(imageId: Long): Int {
        return appDatabase.imageDao().getProcessingStage(imageId)
    }

    suspend fun findById(imageId: Long): Image {
        return appDatabase.imageDao().findById(imageId)
    }

    fun delete(image: Image) {
        viewModelScope.launch(Dispatchers.IO) {
            appDatabase.imageDao().delete(image)
        }
    }

    fun addImage(file: String) {
        viewModelScope.launch(Dispatchers.IO) {
            appDatabase.imageDao().add(Image(0, file, ProcessingStage.unprocessed))
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

    /**
     * Add all the faces to the database, updating the image (in particular, the [ProcessingStage].
     * The faces must all belong to the image.
     */
    fun addAll(image: Image, faces: List<Face>) {
        viewModelScope.launch(Dispatchers.IO) {
            appDatabase.runInTransaction {
                viewModelScope.launch(Dispatchers.IO) {
                    val faceEntities = faces.map { face -> FaceEntity(face.id, face.imageId) }
                    val faceIds = appDatabase.faceDao().addAll(faceEntities)
                    Log.d(TAG, "addAll() face ids: ${faceIds.joinToString(", ")}")
                    faceIds.zip(faces).forEach { pair ->
                        val id = pair.first
                        val face = pair.second
                        if (face.imageId != image.id) {
                            throw IllegalArgumentException("${face} doesn't belong to ${image}")
                        }
                        // Replace Coordinate instances with new instance with correct face ids.
                        val relatedCoordinates = face.coordinates.map { coordinate ->
                            coordinate.copy(faceId = id)
                        }
                        appDatabase.coordinateDao().addAll(relatedCoordinates)
                    }
                    appDatabase.imageDao().update(image)
                }
            }
        }
    }

    fun faces(imageId: Long): LiveData<List<Face>> {
        return appDatabase.faceDao().findById(imageId)
    }

}

