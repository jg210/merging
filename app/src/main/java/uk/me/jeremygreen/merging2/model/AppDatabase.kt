package uk.me.jeremygreen.merging2.model

import androidx.room.Database
import androidx.room.RoomDatabase
import uk.me.jeremygreen.merging2.model.entity.Coordinate
import uk.me.jeremygreen.merging2.model.entity.Face
import uk.me.jeremygreen.merging2.model.entity.Image
import uk.me.jeremygreen.merging2.model.entity.Onboarding

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
        Face::class,
        Image::class,
        Onboarding::class
    ],
    version = 2,
    exportSchema = false
)
internal abstract class AppDatabase: RoomDatabase() {

    abstract fun coordinateDao(): CoordinateDao

    abstract fun faceDao(): FaceDao

    abstract fun imageDao(): ImageDao

    abstract fun onboardingDao(): OnboardingDao

}
