package com.example.gameboardproj.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// Dan - Oct 3
// Link for Annotations
// https://developer.android.com/training/data-storage/room/defining-data.html#kotlin
// Link for example
// https://codelabs.developers.google.com/codelabs/android-room-with-a-view-kotlin/#4

@Entity(tableName = "main_claim_table")
data class MainClaim(
    @PrimaryKey(autoGenerate = true)
    var mc_id: Int,
    var mc_statement: String?,
    var mc_votes: Int,
    var mc_professor_id: Int
)