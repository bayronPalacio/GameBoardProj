package com.example.gameboardproj.data


import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


/**
 * Used to house groups for play
 */
@Entity(tableName = "group_table")
data class Group (
    @PrimaryKey(autoGenerate = true)
    var group_id : Int,

    var group_number : Int,
    @ForeignKey(entity = Student::class,
                parentColumns = [""],
                childColumns =  [""]
    )
    var student_id : Int,

    @ForeignKey(entity = Student::class,
        parentColumns = [""],
        childColumns =  [""]
    )
    var rip_id : Int
)