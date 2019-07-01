package uk.me.jeremygreen.merging.model

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface OnboardingDao {

    @Query("SELECT * from onboarding`` WHERE version = :version")
    suspend fun findById(version: Long): Onboarding?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun add(version: Onboarding)

}

