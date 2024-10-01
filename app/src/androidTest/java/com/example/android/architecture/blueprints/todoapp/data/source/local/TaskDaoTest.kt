package com.example.android.architecture.blueprints.todoapp.data.source.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.filters.SmallTest
import com.example.android.architecture.blueprints.todoapp.data.Task
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@SmallTest
class TaskDaoTest {

    // uma regra que troca o executor em segundo plano da arquitetura de componentes,
    // por um que executa cada tarefa de forma sincrona no mesmo thread
    // para que os resultados sejam postados imediatamente e em ordem repetivel.
    // Executes each task synchronously using Architecture components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: ToDoDatabase

    @Before
    fun initDb() {
        // Using an in-memory database so that the information stored here disappears when the
        // process is killed.
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ToDoDatabase::class.java
        ).build()
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun insertTaskAndGetById() = runTest {
        // GIVEN - Insert a task.
        val task = Task("title", "description")
        database.taskDao().insertTask(task)


        // WHEN - Get the task by id from the database.
        val loaded = database.taskDao().getTaskById(task.id)

        // THEN - The loaded data contains the expected values.
        assertThat(loaded as Task, notNullValue())
        assertThat(loaded.id, `is`(task.id))
        assertThat(loaded.title, `is`(task.title))
        assertThat(loaded.description, `is`(task.description))
        assertThat(loaded.isCompleted, `is`(task.isCompleted))

    }

    @Test
    fun updateTaskAndGetById() = runTest {
        // Given - Insert a task into the DAO.
        val task = Task("title", "description")
        database.taskDao().insertTask(task)

        // When - Update the task by creating a new task with the same ID but different attributes.
        database.taskDao().updateTask(task.copy(title = "new title", description = "new description"))

        // Then - Check that when you get the task by its ID, it has the updated values.
        val updatedTask = database.taskDao().getTaskById(task.id)
        assertThat(updatedTask as Task, notNullValue())
        assertThat(updatedTask.id, `is`(task.id))
        assertThat(updatedTask.title, `is`("new title"))
        assertThat(updatedTask.description, `is`("new description"))
    }

}