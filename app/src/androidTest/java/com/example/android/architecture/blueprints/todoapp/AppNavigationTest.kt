package com.example.android.architecture.blueprints.todoapp

import android.app.Activity
import android.view.Gravity
import androidx.appcompat.widget.Toolbar
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerMatchers.isClosed
import androidx.test.espresso.contrib.DrawerMatchers.isOpen
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.filters.LargeTest
import com.example.android.architecture.blueprints.todoapp.ServiceLocator.tasksRepository
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.TasksRepository
import com.example.android.architecture.blueprints.todoapp.tasks.TasksActivity
import com.example.android.architecture.blueprints.todoapp.util.DataBindingIdlingResource
import com.example.android.architecture.blueprints.todoapp.util.EspressoIdlingResource
import com.example.android.architecture.blueprints.todoapp.util.monitorActivity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test

@LargeTest
class AppNavigationTest {
    private lateinit var taskRepository: TasksRepository

    // An idling resource that waits for Data Binding to have no pending bindings.
    private val dataBindingIdlingResource = DataBindingIdlingResource()

    @Before
    fun setup() {
        taskRepository =
            ServiceLocator.provideTasksRepository(ApplicationProvider.getApplicationContext())
        runBlocking {
            taskRepository.clearCompletedTasks()
        }
    }

    @After
    fun reset() {
        ServiceLocator.resetRepository()
    }

    /**
     * Idling resources tell Espresso that the app is idle or busy. This is needed when operations
     * are not scheduled in the main Looper (for example when executed on a different thread).
     */
    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().register(dataBindingIdlingResource)
    }

    /**
     * Unregister your Idling Resource so it can be garbage collected and does not leak any memory.
     */
    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
    }

    @Test
    fun tasksScreen_clickOnDrawerIcon_OpensNavigation() {
        // Start the tasks screen
        val activityScenario = ActivityScenario.launch(TasksActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // 1. Check that left drawer is closed at startup.
        onView(withId(R.id.drawer_layout))
            .check(matches(isClosed(Gravity.START)))

        // 2. Open drawer by clicking drawer icon.
        onView(
            withContentDescription(
                activityScenario
                    .getToolbarNavigationContentDescription()
            )
        ).perform(click())

        // 3. Check if drawer is open.
        onView(withId(R.id.drawer_layout))
            .check(matches(isOpen(Gravity.START)))

        // When using ActivityScenario.launch(), always call close()
        activityScenario.close()
    }

    @Test
    fun taskDetailScreen_doubleUpButton() = runBlocking {
        val task = Task("Up button", "Description")
        tasksRepository?.saveTask(task)

        // Start the Tasks screen.
        val activityScenario = ActivityScenario.launch(TasksActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // 1. Click on the task on the list.
        onView(withText("Up button")).perform(click())

        // 2. Click on the edit task button.
        onView(withId(R.id.edit_task_fab)).perform(click())

        // 3. Confirm that if we click Up button once, we end up back at the task details page.
        onView(
            withContentDescription(
                activityScenario.getToolbarNavigationContentDescription()
            )
        ).perform(click())
        onView(withId(R.id.task_detail_title_text)).check(matches(isDisplayed()))

        // 4. Confirm that if we click Up button a second time, we end up back at the home screen.
        onView(
            withContentDescription(
                activityScenario.getToolbarNavigationContentDescription()
            )
        ).perform(click())
        onView(withId(R.id.tasks_container_layout)).check(matches(isDisplayed()))

        // When using ActivityScenario.launch(), always call close().
        activityScenario.close()
    }


    @Test
    fun taskDetailScreen_doubleBackButton() = runBlocking {
        val task = Task("Back button", "Description")
        tasksRepository?.saveTask(task)

        // Start Tasks screen.
        val activityScenario = ActivityScenario.launch(TasksActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // 1. Click on the task on the list.
        onView(withText("Back button")).perform(click())

        // 2. Click on the Edit task button.
        onView(withId(R.id.edit_task_fab)).perform(click())

        // 3. Confirm that if we click Back once, we end up back at the task details page.
        pressBack()
        onView(withId(R.id.task_detail_title_text)).check(matches(isDisplayed()))

        // 4. Confirm that if we click Back a second time, we end up back at the home screen.
        pressBack()
        onView(withId(R.id.tasks_container_layout)).check(matches(isDisplayed()))

        // When using ActivityScenario.launch(), always call close()
        activityScenario.close()
    }
}

fun <T : Activity> ActivityScenario<T>.getToolbarNavigationContentDescription(): String {
    var description = ""
    onActivity {
        description =
            it.findViewById<Toolbar>(R.id.toolbar).navigationContentDescription as String
    }
    return description
}