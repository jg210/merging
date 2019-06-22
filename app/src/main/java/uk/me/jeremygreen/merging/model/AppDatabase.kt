package uk.me.jeremygreen.merging.model

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Image::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase: RoomDatabase() {

    abstract fun imageDao(): ImageDao

}