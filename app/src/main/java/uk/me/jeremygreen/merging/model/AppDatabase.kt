package uk.me.jeremygreen.merging.model

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Version 1
 *
 * Tables: images, faces, coordinates.
 *
 * Version 2
 *
 * Changed type of coordinate table's x and y columns from INTEGER to REAL.
 */
@Database(
    entities = [
        Coordinate::class,
        FaceEntity::class,
        Image::class,
        Onboarding::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase: RoomDatabase() {

    abstract fun coordinateDao(): CoordinateDao

    abstract fun faceDao(): FaceDao

    abstract fun imageDao(): ImageDao

    abstract fun onboardingDao(): OnboardingDao

}