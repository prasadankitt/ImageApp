package com.example.cameragallery


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "PictureTable")
data class DatabaseData(
    @PrimaryKey(autoGenerate = true) val id:Int = 0,
//    val picture: Bitmap     used when bitmap is stored
    val pictureUri: String
)