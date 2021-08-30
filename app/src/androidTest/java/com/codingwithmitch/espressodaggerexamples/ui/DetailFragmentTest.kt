package com.codingwithmitch.espressodaggerexamples.ui

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry
import com.codingwithmitch.espressodaggerexamples.R
import com.codingwithmitch.espressodaggerexamples.TestBaseApplication
import com.codingwithmitch.espressodaggerexamples.di.TestAppComponent
import com.codingwithmitch.espressodaggerexamples.fragments.FakeMainFragmentFactory
import com.codingwithmitch.espressodaggerexamples.models.BlogPost
import com.codingwithmitch.espressodaggerexamples.ui.viewmodel.setSelectedBlogPost
import com.codingwithmitch.espressodaggerexamples.util.Constants.BLOG_POSTS_DATA_FILENAME
import com.codingwithmitch.espressodaggerexamples.util.Constants.CATEGORIES_DATA_FILENAME
import com.codingwithmitch.espressodaggerexamples.util.FakeGlideRequestManager
import com.codingwithmitch.espressodaggerexamples.util.JsonUtil
import com.codingwithmitch.espressodaggerexamples.viewmodels.FakeMainViewModelFactory
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
@RunWith(AndroidJUnit4ClassRunner::class)
class DetailFragmentTest: BaseMainActivityTests() {

    @Inject
    lateinit var viewModelFactory: FakeMainViewModelFactory

    @Inject
    lateinit var requestManager: FakeGlideRequestManager

    @Inject
    lateinit var jsonUtil: JsonUtil

    @Inject
    lateinit var fragmentFactory: FakeMainFragmentFactory

    val uiCommunicationListener = mockk<UICommunicationListener>()

    @Before
    fun init() {
        every { uiCommunicationListener.showStatusBar() } just runs
        every { uiCommunicationListener.expandAppBar() } just runs
        every { uiCommunicationListener.hideCategoriesMenu() } just runs
    }

    @Test
    fun isSelectedBlogPostDetailSet() {

        // setup
        val app = InstrumentationRegistry
            .getInstrumentation()
            .targetContext
            .applicationContext as TestBaseApplication

        val apiService = configureFakeApiService(
            blogDataSource = BLOG_POSTS_DATA_FILENAME,
            categoriesDataSource = CATEGORIES_DATA_FILENAME,
            networkDelay = 0L,
            application = app
        )

        configureFakeRepository(apiService, app)

        injectTest(app)

        fragmentFactory.uiCommunicationListener = uiCommunicationListener

        // run test
        val scenario = launchFragmentInContainer<DetailFragment>(
            factory = fragmentFactory
        )
        val rawJson = jsonUtil.readJSONFromAsset(BLOG_POSTS_DATA_FILENAME)
        val blogs = Gson().fromJson<List<BlogPost>>(
            rawJson,
            object : TypeToken<List<BlogPost>>() {}.type
        )
        val selectedBlogPost = blogs[0]

        scenario.onFragment { fragment ->
            fragment.viewModel.setSelectedBlogPost(selectedBlogPost)
        }

        onView(withId(R.id.blog_title))
            .check(matches(withText(selectedBlogPost.title)))
        onView(withId(R.id.blog_category))
            .check(matches(withText(selectedBlogPost.category)))
        onView(withId(R.id.blog_body))
            .check(matches(withText(selectedBlogPost.body)))
    }

    override fun injectTest(application: TestBaseApplication) {
        (application.appComponent as TestAppComponent).inject(this)
    }
}