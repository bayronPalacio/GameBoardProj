package com.example.gameboardproj.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * @author Daniel Cooper
 * Create the Reason in Play Entity Table
 *
 * rip_id = a unique key to identify the RiP
 * rip_statement - the statement created by a user
 * rip_vote - votes made by students on the Rip
 * mc_id - links the RiP to the Main Claim's unique ID
 */

@Entity(tableName = "rip_table")
data class ReasonInPlay(
    @PrimaryKey(autoGenerate = true)
    val rip_id: Int? = null,

    var rip_statement: String?,
    var rip_submitted_by: String?,
    var rip_vote: Int,

    // NOT SURE IF THIS IS CORRECT ?????
    @ForeignKey(entity = MainClaim::class,
                parentColumns = ["mc_id"],  // mc table column
                childColumns = ["mc_id"])   // rip table column
    var mc_id: Int
)