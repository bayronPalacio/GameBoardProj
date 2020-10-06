package com.example.gameboardproj

import androidx.room.*
import com.example.gameboardproj.data.MainClaim

/*
Author: Haonan Cao
Purpose: mainclaim table queries
 */
@Dao
interface MainClaimDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMainClaim(mainClaim: MainClaim)

    @Update
    fun updateMainClaim(mainClaim: MainClaim)

    @Delete
    fun deleteMainClaim(mainClaim: MainClaim)

    @Query("SELECT * FROM main_claim_table")
    fun getAllMainClaim():List<MainClaim>

    @Query("SELECT * FROM main_claim_table WHERE mc_id ==:inputID")
    fun getMainClaimByID(inputID: Int):MainClaim
}