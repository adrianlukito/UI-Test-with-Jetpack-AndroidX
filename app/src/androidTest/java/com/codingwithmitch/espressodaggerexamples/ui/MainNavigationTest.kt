package com.codingwithmitch.espressodaggerexamples.ui

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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
@RunWith(AndroidJUnit4ClassRunner::class)
class MainNavigationTest: BaseMainActivityTests() {

    @get: Rule
    val espressoIdlingResourceRule = EspressoIdlingResourceRule()

    @Inject
    lateinit var jsonUtil: JsonUtil

    @Test
    fun basicNavigationTest() {
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

        val rawJson = jsonUtil.readJSONFromAsset(BLOG_POSTS_DATA_FILENAME)
        val blogs = Gson().fromJson<List<BlogPost>>(
            rawJson,
            object : TypeToken<List<BlogPost>>() {}.type
        )
        val SELECTED_LIST_INDEX = 8
        val selectedBlogPost = blogs.get(SELECTED_LIST_INDEX)

        // run test
        val scenario = launchActivity<MainActivity>()

        val recyclerView = onView(withId(R.id.recycler_view))

        recyclerView.check(matches(isDisplayed()))

        recyclerView.perform(
            RecyclerViewActions.scrollToPosition<BlogPostViewHolder>(SELECTED_LIST_INDEX)
        )

        // Nav to detail fragment
        recyclerView.perform(
            RecyclerViewActions.actionOnItemAtPosition<BlogPostViewHolder>(SELECTED_LIST_INDEX, click())
        )

        onView(withId(R.id.blog_title)).check(matches(withText(selectedBlogPost.title)))
        onView(withId(R.id.blog_category)).check(matches(withText(selectedBlogPost.category)))
        onView(withId(R.id.blog_body)).check(matches(withText(selectedBlogPost.body)))

        // Nav to final fragment
        onView(withId(R.id.blog_image)).perform(click())

        onView(withId(R.id.scaling_image_view)).check(matches(isDisplayed()))

        // back to DetailFragment
        pressBack()

        onView(withId(R.id.blog_title)).check(matches(withText(selectedBlogPost.title)))
        onView(withId(R.id.blog_category)).check(matches(withText(selectedBlogPost.category)))
        onView(withId(R.id.blog_body)).check(matches(withText(selectedBlogPost.body)))

        // back to ListFragment
        pressBack()
        recyclerView.check(matches(isDisplayed()))
    }

    override fun injectTest(application: TestBaseApplication) {
        (application.appComponent as TestAppComponent).inject(this)
    }
}