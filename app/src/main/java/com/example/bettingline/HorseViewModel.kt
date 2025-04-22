package com.example.bettingline

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class HorseViewModel(application: Application) : AndroidViewModel(application) {
    private val repo: HorseRepository
    val horses: LiveData<List<Horse>>

    init {
        val dao = HorseDatabase.getDatabase(application).horseDao()
        repo = HorseRepository(dao)
        horses = repo.horses.asLiveData()
    }

    fun addHorse(horse: Horse) = viewModelScope.launch {
        repo.insertHorse(horse)
    }

    fun deleteHorse(horse: Horse) = viewModelScope.launch {
        repo.deleteHorse(horse)
    }
    fun deleteHorseById(id: Int) = viewModelScope.launch {
        repo.deleteHorseById(id)
    }
}

//class HorseViewModelFactory(private val application: Application) :
//    ViewModelProvider.AndroidViewModelFactory(application) {
//
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(HorseViewModel::class.java)) {
//            return HorseViewModel(application) as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel class")
//    }
//}