package com.codingwithmitch.espressodaggerexamples.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.codingwithmitch.espressodaggerexamples.repository.FakeMainRespositoryImpl
import com.codingwithmitch.espressodaggerexamples.ui.viewmodel.MainViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import javax.inject.Inject
import javax.inject.Singleton

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
@Singleton
class FakeMainViewModelFactory @Inject constructor(
    private val mainRespository: FakeMainRespositoryImpl
): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(mainRespository) as T
        }
        throw IllegalArgumentException("Unknow ViewModel class")
    }
}