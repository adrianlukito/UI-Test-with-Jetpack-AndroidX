package com.codingwithmitch.espressodaggerexamples.ui

import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry
import com.codingwithmitch.espressodaggerexamples.R
import com.codingwithmitch.espressodaggerexamples.TestBaseApplication
import com.codingwithmitch.espressodaggerexamples.di.TestAppComponent
import com.codingwithmitch.espressodaggerexamples.ui.BlogPostListAdapter.BlogPostViewHolder
import com.codingwithmitch.espressodaggerexamples.ui.viewmodel.state.MainViewState
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

    @Test
    fun chechListData_testScrolling() {
        // setup
        val app = InstrumentationRegistry
            .getInstrumentation()
            .targetContext
            .applicationContext as TestBaseApplication

        val apiService = configureFakeApiService(
            blogsDataSource = BLOG_POSTS_DATA_FILENAME, // empty list of data
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
        recyclerView.perform(
            RecyclerViewActions.scrollToPosition<BlogPostViewHolder>(5)
        )

        onView(withText("Mountains in Washington")).check(matches(isDisplayed()))

        recyclerView.perform(
            RecyclerViewActions.scrollToPosition<BlogPostViewHolder>(8)
        )

        onView(withText("Blake Posing for his Website")).check(matches(isDisplayed()))

        recyclerView.perform(
            RecyclerViewActions.scrollToPosition<BlogPostViewHolder>(0)
        )

        onView(withText("Vancouver PNE 2019")).check(matches(isDisplayed()))

        onView(withId(R.id.no_data_textview)).check(matches(withEffectiveVisibility(Visibility.GONE)))
    }

    @Test
    fun chechListData_onCategoryChange_toEarthPorn() {
        // setup
        val app = InstrumentationRegistry
            .getInstrumentation()
            .targetContext
            .applicationContext as TestBaseApplication

        val apiService = configureFakeApiService(
            blogsDataSource = BLOG_POSTS_DATA_FILENAME, // empty list of data
            categoriesDataSource = CATEGORIES_DATA_FILENAME,
            networkDelay = 0L,
            application = app
        )

        configureFakeRepository(apiService, app)

        injectTest(app)

        // run test
        val scenario = launchActivity<MainActivity>().onActivity { mainActivity ->
            val toolbar: Toolbar = mainActivity.findViewById(R.id.tool_bar)

            mainActivity.viewModel.viewState.observe(mainActivity, object : Observer<MainViewState> {
                override fun onChanged(viewState: MainViewState?) {
                    if(viewState?.activeJobCounter?.size == 0) {
                        toolbar.showOverflowMenu()
                        mainActivity.viewModel.viewState.removeObserver(this)
                    }
                }
            })
        }

        val CATEGORY_NAME = "earthporn"
        onView(withText(CATEGORY_NAME)).perform(click())
        onView(withText("Mountains in Washington")).check(matches(isDisplayed()))
        onView(withText("France Mountain Range")).check(matches(isDisplayed()))
        onView(withText("Vancouver PNE 2019")).check(doesNotExist())
    }

    @Test
    fun chechListData_onCategoryChange_toFun() {
        // setup
        val app = InstrumentationRegistry
            .getInstrumentation()
            .targetContext
            .applicationContext as TestBaseApplication

        val apiService = configureFakeApiService(
            blogsDataSource = BLOG_POSTS_DATA_FILENAME, // empty list of data
            categoriesDataSource = CATEGORIES_DATA_FILENAME,
            networkDelay = 0L,
            application = app
        )

        configureFakeRepository(apiService, app)

        injectTest(app)

        // run test
        val scenario = launchActivity<MainActivity>().onActivity { mainActivity ->
            val toolbar: Toolbar = mainActivity.findViewById(R.id.tool_bar)

            mainActivity.viewModel.viewState.observe(mainActivity, object : Observer<MainViewState> {
                override fun onChanged(viewState: MainViewState?) {
                    if(viewState?.activeJobCounter?.size == 0) {
                        toolbar.showOverflowMenu()
                        mainActivity.viewModel.viewState.removeObserver(this)
                    }
                }
            })
        }

        val CATEGORY_NAME = "fun"
        onView(withText(CATEGORY_NAME)).perform(click())
        onView(withText("Vancouver PNE 2019")).check(matches(isDisplayed()))
        onView(withText("My Brother Blake")).check(matches(isDisplayed()))
        onView(withText("Ready for a Walk")).check(doesNotExist())
    }

    override fun injectTest(application: TestBaseApplication) {
        (application.appComponent as TestAppComponent)
            .inject(this)
    }
}









