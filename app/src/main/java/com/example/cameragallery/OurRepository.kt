package com.example.cameragallery

class OurRepository(private val myDao: MyDao) {
    val allData = myDao.observeDatabase()

    suspend fun insertImage(picture: DatabaseData)
    {
        myDao.insertImage(picture)
    }
}