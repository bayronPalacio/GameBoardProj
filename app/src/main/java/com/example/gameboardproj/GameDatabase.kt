package com.example.gameboardproj

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [Student::class], version = 1)
abstract class GameDatabase : RoomDatabase() {

    abstract fun studentDao() : StudentDao

    companion object {
        var INSTANCE: GameDatabase? = null

        fun getAppDataBase(context: Context): GameDatabase? {
            if (INSTANCE == null){
                synchronized(GameDatabase::class){
                    INSTANCE = Room.databaseBuilder(context.applicationContext, GameDatabase::class.java, "Database").build()
                }
            }
            return INSTANCE
        }

        fun destroyDataBase(){
            INSTANCE = null
        }
    }
}