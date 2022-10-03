package com.example.cameragallery

import android.content.Context
import androidx.room.*
//@TypeConverters(Converter::class) used for bitmap store without uri
@Database(entities = [DatabaseData::class],version = 1, exportSchema = false)
abstract class OurDatabase: RoomDatabase() {
    abstract fun myDao(): MyDao
    companion object {
        @Volatile
        private var INSTANCE: OurDatabase? = null

        fun getDatabase(context: Context): OurDatabase =
            INSTANCE?: synchronized(this) {
                INSTANCE?: buildDatabase(context).also {
                    INSTANCE = it
                }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context,OurDatabase::class.java,"pictureDatabase").build()

    }
}