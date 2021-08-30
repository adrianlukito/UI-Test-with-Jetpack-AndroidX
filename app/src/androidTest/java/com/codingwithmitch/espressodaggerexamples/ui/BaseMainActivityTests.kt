package com.codingwithmitch.espressodaggerexamples.ui

import com.codingwithmitch.espressodaggerexamples.TestBaseApplication
import com.codingwithmitch.espressodaggerexamples.api.FakeApiService
import com.codingwithmitch.espressodaggerexamples.di.TestAppComponent
import com.codingwithmitch.espressodaggerexamples.repository.FakeMainRespositoryImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
abstract class BaseMainActivityTests {

    abstract fun injectTest(application: TestBaseApplication)

    fun configureFakeApiService(
        blogDataSource: String? = null,
        categoriesDataSource: String? = null,
        networkDelay: Long? = null,
        application: TestBaseApplication
    ): FakeApiService {
        val apiService = (application.appComponent as TestAppComponent).apiService
        blogDataSource?.let { apiService.blogPostsJsonFileName = it }
        categoriesDataSource?.let { apiService.categoriesJsonFileName = it }
        networkDelay?.let { apiService.networkDelay = it }

        return apiService
    }

    fun configureFakeRepository(
        apiService: FakeApiService,
        application: TestBaseApplication
    ): FakeMainRespositoryImpl {
        val mainRepository = (application.appComponent as TestAppComponent).mainRepository
        mainRepository.apiService = apiService

        return mainRepository
    }
}
