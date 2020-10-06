package com.example.gameboardproj

import androidx.room.*

/**
 * @author: Bayron Arturo Palacio
 *
 * The Data Access Objects (DAO) is an interface that contains several methods like @insert, @update @delete and queries.
 * This methods are used to access the data from the Room database.
 * This interface has two additional queries:
 *      getAllStudent() - this method will get all the users from the user table
 *      getStudentByEmail() - this method will get an user that match the email entered
 *
 */

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