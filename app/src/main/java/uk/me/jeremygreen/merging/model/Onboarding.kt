package uk.me.jeremygreen.merging.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * This table stores a set of accepted onboarding-text versions.
 */
@Entity(tableName = "onboarding")
data class Onboarding(

    @PrimaryKey
    val version: Long

)
