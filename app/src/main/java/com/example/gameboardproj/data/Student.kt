package com.example.gameboardproj.data


import androidx.room.Entity
import androidx.room.PrimaryKey

// Dan - Oct 3

@Entity(tableName = "student_table")
data class Student (
    @PrimaryKey(autoGenerate = true)
    var student_id : Int,

    var student_name : String?,
    var student_last : String?,
    var student_email : String?
)