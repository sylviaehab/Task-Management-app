package com.example.task_4_sprints.data.db.dao



import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.task_4_sprints.data.db.entities.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Insert
    suspend fun insert(task: Task): Long

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getById(id: Int): Task?

    @Query("SELECT * FROM tasks WHERE projectId = :projectId")
    suspend fun getTasksInProjectOnce(projectId: Int): List<Task>

    @Query("SELECT * FROM tasks WHERE projectId = :projectId")
    fun getTasksInProjectFlow(projectId: Int): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE projectId = :projectId")
    fun getTasksInProjectLiveData(projectId: Int): LiveData<List<Task>>
}
