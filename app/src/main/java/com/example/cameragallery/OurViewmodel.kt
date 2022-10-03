package com.example.cameragallery

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OurViewmodel(application: Application): AndroidViewModel(application) {
    private val myDao = OurDatabase.getDatabase(application).myDao()
    private val repository: OurRepository = OurRepository(myDao)
    val allData = repository.allData

    fun insertImage(picture: DatabaseData) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertImage(picture)
    }
}