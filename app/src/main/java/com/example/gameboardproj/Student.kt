package com.example.gameboardproj

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
class Student (
    @PrimaryKey(autoGenerate = true) val userIdDb: Int?=null,
    val firstNameDb: String?,
    val lastNameDb: String?,
    val emailDb: String?,
    val passwordDb: String?,
    val roleTypeDb: Int?,
)