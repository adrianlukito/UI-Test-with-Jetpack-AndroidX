package com.codingwithmitch.espressodaggerexamples.ui

import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry
import com.codingwithmitch.espressodaggerexamples.R
import com.codingwithmitch.espressodaggerexamples.TestBaseApplication
import com.codingwithmitch.espressodaggerexamples.di.TestAppComponent
import com.codingwithmitch.espressodaggerexamples.util.Constants
import com.codingwithmitch.espressodaggerexamples.util.Constants.BLOG_POSTS_DATA_FILENAME
import com.codingwithmitch.espressodaggerexamples.util.Constants.CATEGORIES_DATA_FILENAME
import com.codingwithmitch.espressodaggerexamples.util.Constants.EMPTY_LIST
import com.codingwithmitch.espressodaggerexamples.util.EspressoIdlingResourceRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * ListFragment integration tests (ActivityScenario).
 * Launch app and check ListFragment properties (menu, recyclerview).
 *
 */
@InternalCoroutinesApi
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4ClassRunner::class)
class ListFragmentIntegrationTests: BaseMainActivityTests() {

    private val CLASS_NAME = "MainActivityIntegrationTests"

    @get: Rule
    val espressoIdlingResourceRule = EspressoIdlingResourceRule()

    @Test
    fun isBlogListEmpty() {

        // setup
        val app = InstrumentationRegistry
            .getInstrumentation()
            .targetContext
            .applicationContext as TestBaseApplication

        val apiService = configureFakeApiService(
            blogsDataSource = EMPTY_LIST, // empty list of data
            categoriesDataSource = CATEGORIES_DATA_FILENAME,
            networkDelay = 0L,
            application = app
        )

        configureFakeRepository(apiService, app)

        injectTest(app)

        // run test
        val scenario = launchActivity<MainActivity>()

        val recyclerView = onView(withId(R.id.recycler_view))

        recyclerView.check(matches(isDisplayed()))

        onView(withId(R.id.no_data_textview))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }

    @Test
    fun isCategoryListEmpty() {

        // setup
        val app = InstrumentationRegistry
            .getInstrumentation()
            .targetContext
            .applicationContext as TestBaseApplication

        val apiService = configureFakeApiService(
            blogsDataSource = BLOG_POSTS_DATA_FILENAME, // empty list of data
            categoriesDataSource = EMPTY_LIST,
            networkDelay = 0L,
            application = app
        )

        configureFakeRepository(apiService, app)

        injectTest(app)

        val scenario = launchActivity<MainActivity>().onActivity { mainActivity ->
            val toolbar: Toolbar = mainActivity.findViewById(R.id.tool_bar)

            mainActivity.viewModel.viewState.observe(mainActivity, Observer { viewState ->
                if(viewState.activeJobCounter.size == 0) {
                    toolbar.showOverflowMenu()
                }
            })
        }

        // assert
        onView(withSubstring("earthporn")).check(doesNotExist())
        onView(withSubstring("dogs")).check(doesNotExist())
        onView(withSubstring("fun")).check(doesNotExist())
        onView(withSubstring("All")).check(matches(isDisplayed()))
    }

    override fun injectTest(application: TestBaseApplication) {
        (application.appComponent as TestAppComponent)
            .inject(this)
    }
}









