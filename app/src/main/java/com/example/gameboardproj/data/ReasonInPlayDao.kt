package com.example.gameboardproj.data

import androidx.room.*

/**
 * @author Daniel Cooper
 *
 * Reason in Play Data Access Object
 * Assigns SQL queries to the corresponding abstract functions
 *
 * insertRip -  add RiP to DB
 * updateRip - make modifications to a created RiP, used mainly to update statements
 * deleteRip - remove RiP from DB
 * getRipByStatement - allow search of RiP ID in DB by using the statement in play
 * getRipVotes - gets all votes on RiP
 * getRips - returns all RiPs from the DB
 */
@Dao
interface ReasonInPlayDao {

    @Insert
    fun insertRip(reasonInPlay : ReasonInPlay)

    @Update
    fun updateRip(reasonInPlay : ReasonInPlay)

    @Delete
    fun deleteRip(reasonInPlay: ReasonInPlay)

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