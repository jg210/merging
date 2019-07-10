package uk.me.jeremygreen.merging.model

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        Image::class,
        FaceEntity::class,
        Coordinate::class,
        Onboarding::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase: RoomDatabase() {

    abstract fun imageDao(): ImageDao

    abstract fun faceDao(): FaceDao

    abstract fun coordinateDao(): CoordinateDao

    abstract fun onboardingDao(): OnboardingDao

}