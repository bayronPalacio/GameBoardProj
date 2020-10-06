package com.example.gameboardproj.data

import androidx.room.*

@Dao
interface ReasonInPlayDao {

    @Insert
    fun insertRip(reasonInPlay : ReasonInPlay)

    @Update
    fun updateRip(reasonInPlay : ReasonInPlay)

    @Delete
    fun deleteStudent(reasonInPlay: ReasonInPlay)

    // get RiP
    @Query("SELECT * FROM rip_table WHERE rip_statement == :ripStatement")
    fun getRipByStatement(ripStatement : String): ReasonInPlay

    // get all votes for a RiP
    // return integer list with votes
    @Query("SELECT rip_vote FROM rip_table WHERE rip_id == :ripId")
    fun getRipVotes(ripId: Int): List<Int>

    // must display all RiPs in Waiting
    @Query("Select * From rip_table")
    fun getRips():List<ReasonInPlay>
}