package com.example.cameragallery

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MyDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertImage(picture: DatabaseData)
    @Query("Select * from PictureTable order by id ASC")
    fun observeDatabase() : LiveData<List<DatabaseData>>
}