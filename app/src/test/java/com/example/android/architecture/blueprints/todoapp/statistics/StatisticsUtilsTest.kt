package com.example.android.architecture.blueprints.todoapp.statistics

import com.example.android.architecture.blueprints.todoapp.data.Task
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class StatisticsUtilsTest {

    @Test
    fun getActiveAndCompletedStats_noCompleted_returnsHundredZero() {
        // Create an active task
        val tasksNoCompleted = listOf<Task>(
            Task("title", "desc", isCompleted = false)
        )

        // Call your function
        // When the list of tasks is computed with an active task
        val result = getActiveAndCompletedStats(tasksNoCompleted)

        // Check the result
        // Then the percentages are 100 and 0
        //assertEquals(result.completedTasksPercent, 0f)
        //assertEquals(result.activeTasksPercent, 100f)
        assertThat(result.activeTasksPercent, `is`(100f))
        assertThat(result.completedTasksPercent, `is`(0f))
    }

    @Test
    fun getActiveAndCompleteStats_noActive_returnsZeroHundred() {
        // Create an completed task
        val tasksCompleted = listOf<Task>(
            Task("title", "desc", isCompleted = true),
        )

        // Call your function
        // When the list of tasks is computed with a completed task
        val result = getActiveAndCompletedStats(tasksCompleted)

        // Check the result
        // Then the percentages are 0 and 100
        assertThat(result.activeTasksPercent, `is`(0f))
        assertThat(result.completedTasksPercent, `is`(100f))
    }

    @Test
    fun getActiveAndCompletedStats_both_returnsSixtyForty() {
        // Create two completed tasks, and three active tasks
        // Given 2 completed tasks and 3 active tasks
        val tasks = listOf<Task>(
            Task("title", "desc", isCompleted = true),
            Task("title 2", "desc 2", isCompleted = true),
            Task("title 3", "desc 3", isCompleted = false),
            Task("title 4", "desc 4", isCompleted = false),
            Task("title 5", "desc 5", isCompleted = false)
        )

        // Call your function
        // When the list of tasks is computed
        val result = getActiveAndCompletedStats(tasks)

        // Check the result
        // Then the result is 60-40
        assertThat(result.activeTasksPercent, `is`(60f))
        assertThat(result.completedTasksPercent, `is`(40f))
    }

    @Test
    fun getActivieAndCompletedStats_empty_returnsZeros() {
        // Create an emptyList
        // Given an emptyList
        val tasksEmptyList = emptyList<Task>()

        // Call your function
        // When there are no tasks
        val result = getActiveAndCompletedStats(tasksEmptyList)

        // Check the result
        // Both active and completed tasks are 0
        assertThat(result.activeTasksPercent, `is`(0f))
        assertThat(result.completedTasksPercent, `is`(0f))
    }

    @Test
    fun getActiveAndCompletedStats_error_returnsZeros() {
        // Create an null list
        // Given an null list
        val tasksNullList = null

        // Call your function
        // When there are no tasks
        val result = getActiveAndCompletedStats(tasksNullList)

        // Check the result
        // Both active and completed tasks are 0
        assertThat(result.activeTasksPercent, `is`(0f))
        assertThat(result.completedTasksPercent, `is`(0f))
    }
}