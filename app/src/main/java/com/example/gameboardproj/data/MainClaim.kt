package com.example.gameboardproj.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @author Daniel Cooper
 * The Main Claim Entity Table
 *
 * store a unique MC ID, the statement, true or false votes, and the MC creator (the professor)
 */

@Entity(tableName = "main_claim_table")
data class MainClaim(
    @PrimaryKey(autoGenerate = true)
    var mc_id: Int,
    var mc_statement: String?,
    var mc_votes: Int,
    var mc_professor_id: Int
)