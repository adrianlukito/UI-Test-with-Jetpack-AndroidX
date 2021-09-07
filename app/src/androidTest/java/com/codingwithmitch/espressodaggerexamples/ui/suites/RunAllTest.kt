package com.codingwithmitch.espressodaggerexamples.ui.suites

import com.codingwithmitch.espressodaggerexamples.ui.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import org.junit.runner.RunWith
import org.junit.runners.Suite

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
@RunWith(Suite::class)
@Suite.SuiteClasses(
    MainNavigationTest::class,
    ListFragmentNavigationTests::class,
    ListFragmentIntegrationTests::class,
    ListFragmentErrorTests::class,
    DetailFragmentTest::class
)
class RunAllTest