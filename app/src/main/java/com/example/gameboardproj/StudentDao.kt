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

    @Query("SELECT * FROM student_table")
    fun getAllStudents():List<Student>

    @Query("SELECT * FROM student_table WHERE emailDb ==:inputEmail")
    fun getStudentByEmail(inputEmail: String):Student

}