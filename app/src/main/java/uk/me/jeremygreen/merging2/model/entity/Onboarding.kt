package uk.me.jeremygreen.merging2.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * This table stores a set of accepted onboarding-text versions.
 */
@Entity(tableName = "onboarding")
internal data class Onboarding(

    @PrimaryKey
    val version: Long

)
