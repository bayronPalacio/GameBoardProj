package com.example.gameboardproj.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

// Dan - Oct 3
// Link for Annotations
// https://developer.android.com/training/data-storage/room/defining-data.html#kotlin
// Link for example
// https://codelabs.developers.google.com/codelabs/android-room-with-a-view-kotlin/#4

@Entity(tableName = "rip_table")
data class ReasonInPlay(
    @PrimaryKey(autoGenerate = true)
    var rip_id: Int,

    var rip_statement: String?,
    var rip_submitted_by: String?,
    var rip_vote: Int,

    // NOT SURE IF THIS IS CORRECT ?????
    @ForeignKey(entity = MainClaim::class,  // confusing naming?
                parentColumns = ["mc_id"],  // mc table column
                childColumns = ["mc_id"])   // rip table column
    var mc_id: Int
)