package com.example.gameboardproj

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.gameboardproj.data.MainClaim
import com.example.gameboardproj.data.ReasonInPlay
import com.example.gameboardproj.data.ReasonInPlayDao

/**
 * @author: Bayron Arturo Palacio
 *
 * This abstract class extends the RoomDatabase and holds a connection with the SQLite database.
 * Reference: https://medium.com/mindorks/room-kotlin-android-architecture-components-71cad5a1bb35
 *
 */

@Database(entities = [Student::class,MainClaim::class, ReasonInPlay::class], version = 3)
abstract class GameDatabase : RoomDatabase() {

    abstract fun studentDao() : StudentDao
    abstract fun mainclaimDao() : MainClaimDao
    abstract fun ripDao() : ReasonInPlayDao

    companion object {
        var INSTANCE: GameDatabase? = null

        fun getAppDataBase(context: Context): GameDatabase? {
            if (INSTANCE == null){
                synchronized(GameDatabase::class){
                    INSTANCE = Room.databaseBuilder(context.applicationContext, GameDatabase::class.java, "Database")
                        .fallbackToDestructiveMigration().build()
                }
            }
            return INSTANCE
        }

        fun destroyDataBase(){
            INSTANCE = null
        }
    }
}