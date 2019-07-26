package uk.me.jeremygreen.merging.model

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        Coordinate::class,
        FaceEntity::class,
        Image::class,
        Onboarding::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase: RoomDatabase() {

    abstract fun coordinateDao(): CoordinateDao

    abstract fun faceDao(): FaceDao

    abstract fun imageDao(): ImageDao

    abstract fun onboardingDao(): OnboardingDao

}