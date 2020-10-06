package com.example.gameboardproj

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @author : Bayron Arturo Palacio
 *
 * This class creates the User table with the following following columns
 * userIdDb - Primary key of the table and will be generated automatically
 * firstNameDb - User First Name
 * lastNameDb - User Last Name
 * emailDb - User email. It will be used as username when the user logs in
 * passwordDb - User password
 * roleTypeDb - The user will need to select between to user types Leader or Player
 *              Leader: this user will be able to control the time of the game, stop timer and create the Main Claim as a teacher.
 *              Player: this user will be able to see the Main Claim of the day, submit Reason in Play and vote
 *
 */

@Entity(tableName = "user")
class Student (
    @PrimaryKey(autoGenerate = true) val userIdDb: Int?=null,
    val firstNameDb: String?,
    val lastNameDb: String?,
    val emailDb: String?,
    val passwordDb: String?,
    val roleTypeDb: Int?,
)