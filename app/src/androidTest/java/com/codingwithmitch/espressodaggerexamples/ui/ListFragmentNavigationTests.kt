package com.codingwithmitch.espressodaggerexamples.ui

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry
import com.codingwithmitch.espressodaggerexamples.R
import com.codingwithmitch.espressodaggerexamples.TestBaseApplication
import com.codingwithmitch.espressodaggerexamples.di.TestAppComponent
import com.codingwithmitch.espressodaggerexamples.fragments.FakeMainFragmentFactory
import com.codingwithmitch.espressodaggerexamples.models.BlogPost
import com.codingwithmitch.espressodaggerexamples.ui.BlogPostListAdapter.BlogPostViewHolder
import com.codingwithmitch.espressodaggerexamples.util.Constants
import com.codingwithmitch.espressodaggerexamples.util.Constants.BLOG_POSTS_DATA_FILENAME
import com.codingwithmitch.espressodaggerexamples.util.Constants.CATEGORIES_DATA_FILENAME
import com.codingwithmitch.espressodaggerexamples.util.Constants.EMPTY_LIST
import com.codingwithmitch.espressodaggerexamples.util.EspressoIdlingResource
import com.codingwithmitch.espressodaggerexamples.util.EspressoIdlingResourceRule
import com.codingwithmitch.espressodaggerexamples.util.JsonUtil
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
@RunWith(AndroidJUnit4ClassRunner::class)
class ListFragmentNavigationTests: BaseMainActivityTests() {

    @get: Rule
    val espressoIdlingResourceRule = EspressoIdlingResourceRule()

    @Inject
    lateinit var fragmentFactory: FakeMainFragmentFactory

    val uiCommunicationListener = mockk<UICommunicationListener>()

    @Before
    fun init() {
        every { uiCommunicationListener.showStatusBar() } just runs
        every { uiCommunicationListener.expandAppBar() } just runs
        every { uiCommunicationListener.hideCategoriesMenu() } just runs
        every { uiCommunicationListener.showCategoriesMenu(any()) } just runs
    }

    @Test
    fun testNavigationToDetailFragment() {
        // setup
        val app = InstrumentationRegistry
            .getInstrumentation()
            .targetContext
            .applicationContext as TestBaseApplication

        val apiService = configureFakeApiService(
            blogsDataSource = BLOG_POSTS_DATA_FILENAME,
            categoriesDataSource = CATEGORIES_DATA_FILENAME,
            networkDelay = 0L,
            application = app
        )

        configureFakeRepository(apiService, app)

        injectTest(app)

        fragmentFactory.uiCommunicationListener = uiCommunicationListener

        val navController = TestNavHostController(app)
        navController.setGraph(R.navigation.main_nav_graph)
        navController.setCurrentDestination(R.id.listFragment)

        val scenario = launchFragmentInContainer<ListFragment>(
            factory = fragmentFactory
        )
        scenario.onFragment { fragment ->
            Navigation.setViewNavController(fragment.requireView(), navController)
        }

        val recyclerView = onView(withId(R.id.recycler_view))

        recyclerView.check(matches(isDisplayed()))
        recyclerView.perform(RecyclerViewActions.scrollToPosition<BlogPostViewHolder>(5))
        recyclerView.perform(RecyclerViewActions.actionOnItemAtPosition<BlogPostViewHolder>(5, click()))

        assertEquals(navController.currentDestination?.id, R.id.detailFragment)
    }

    override fun injectTest(application: TestBaseApplication) {
        (application.appComponent as TestAppComponent).inject(this)
    }
}