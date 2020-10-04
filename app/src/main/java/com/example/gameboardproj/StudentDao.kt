package com.example.gameboardproj

import androidx.room.*

@Dao
interface StudentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStudent(student: Student)

    @Update
    fun updateStudent(student: Student)

    @Delete
    fun deleteStudent(student: Student)

    @Query("SELECT * FROM user")
    fun getAllStudents():List<Student>

    @Query("SELECT * FROM user WHERE emailDb ==:inputEmail")
    fun getStudentByEmail(inputEmail: String):Student

}