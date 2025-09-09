package uk.me.jeremygreen.merging2.model

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.application
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uk.me.jeremygreen.merging2.algo.FaceDetect
import uk.me.jeremygreen.merging2.model.entity.Face
import uk.me.jeremygreen.merging2.model.entity.Image
import uk.me.jeremygreen.merging2.model.entity.Onboarding

internal class AppViewModel(
    application: Application,
    private val appDatabase: AppDatabase)
    : AndroidViewModel(application) {

    companion object {
        private const val TAG = "AppViewModel"
        private fun createAppDatabase(application: Application): AppDatabase {
            val builder = Room.databaseBuilder(
                application.applicationContext,
                AppDatabase::class.java,
                "app"
            )
            builder.fallbackToDestructiveMigrationFrom(1, 2)
            return builder.build()
        }

        /**
         * @param owner must be the same for all things using the instance
         */
        fun getInstance(owner: ViewModelStoreOwner, application: Application) =
            ViewModelProvider(
                owner,
                ViewModelProvider.AndroidViewModelFactory.getInstance(application)
            )[AppViewModel::class.java]
    }

    @Suppress("unused")
    constructor(application: Application): this(application, createAppDatabase(application))

    private fun ensureImagesProcessedInBackground() {
        viewModelScope.launch(Dispatchers.IO) {
            ensureImagesProcessed()
        }
    }

    private suspend fun ensureImagesProcessed() {
        // TODO remove old versions of faces
        findUnprocessedFaces()
    }
    private suspend fun findUnprocessedFaces() {
        val unprocessedImages = appDatabase.imageDao().getUnprocessedImages()
        unprocessedImages.forEach { image ->
            val facesWithCoordinates = FaceDetect.findFaces(image, application)
            val imageWithAlgorithmVersion = image.copy(
                faceDetectionAlgorithmVersion = FaceDetect.VERSION
            )
            addAll(imageWithAlgorithmVersion, facesWithCoordinates)
        }
    }

    fun allImages(): LiveData<ImmutableList<Image>> {
        ensureImagesProcessedInBackground()
        return appDatabase.imageDao().getImages().map { images -> images.toImmutableList() }
    }

    fun delete(image: Image) {
        viewModelScope.launch(Dispatchers.IO) {
            appDatabase.imageDao().delete(image)
        }
    }

    fun addImage(file: String) {
        viewModelScope.launch(Dispatchers.IO) {
            appDatabase.imageDao().add(Image(0, file))
            ensureImagesProcessed()
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
     * Add all the faces to the database, updating the image. The faces must all belong to the image.
     */
    private fun addAll(image: Image, facesWithCoordinates: List<FaceWithCoordinates>) {
        viewModelScope.launch(Dispatchers.IO) {
            appDatabase.runInTransaction {
                viewModelScope.launch(Dispatchers.IO) {
                    val faceEntities = facesWithCoordinates.map { face ->
                        Face(
                            face.id,
                            face.imageId
                        )
                    }
                    val faceIds = appDatabase.faceDao().addAll(faceEntities)
                    Log.d(TAG, "addAll() face ids: ${faceIds.joinToString(", ")}")
                    faceIds.zip(facesWithCoordinates).forEach { pair ->
                        val id = pair.first
                        val face = pair.second
                        require(face.imageId == image.id) { "${face} doesn't belong to ${image}" }
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

    fun findFacesByImageId(imageId: Long): LiveData<List<FaceWithCoordinates>> =
        appDatabase.faceDao().findById(imageId)

}

