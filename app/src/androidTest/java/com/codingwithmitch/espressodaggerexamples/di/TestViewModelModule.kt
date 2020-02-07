package com.codingwithmitch.espressodaggerexamples.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.codingwithmitch.daggermultifeature.feature1.di.keys.MainViewModelKey
import com.codingwithmitch.espressodaggerexamples.viewmodels.FakeMainViewModelFactory
import com.codingwithmitch.espressodaggerexamples.ui.viewmodel.MainViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
@Module
abstract class TestViewModelModule {

    @Binds
    abstract fun bindViewModelFactory(vmFactory: FakeMainViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @MainViewModelKey(MainViewModel::class)
    abstract fun bindMainViewModel(viewModel: MainViewModel): ViewModel

}













