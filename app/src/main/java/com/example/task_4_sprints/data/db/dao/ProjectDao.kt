package com.example.task_4_sprints.data.db.dao


import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.example.task_4_sprints.data.db.entities.Project
import com.example.task_4_sprints.data.db.entities.ProjectWithTasks
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {
    @Insert
    suspend fun insert(project: Project): Long

    @Update
    suspend fun update(project: Project)

    @Delete
    suspend fun delete(project: Project)

    // Suspend one-time snapshot
    @Query("SELECT * FROM projects")
    suspend fun getAllProjectsOnce(): List<Project>

    // Reactive Flow
    @Query("SELECT * FROM projects")
    fun getAllProjectsFlow(): Flow<List<Project>>

    // LiveData (for ViewModel)
    @Query("SELECT * FROM projects")
    fun getAllProjectsLiveData(): LiveData<List<Project>>

    // Relation
    @Transaction
    @Query("SELECT * FROM projects WHERE id = :projectId")
    suspend fun getProjectWithTasks(projectId: Int): ProjectWithTasks

    // Complex query - projects with more than 3 tasks
    @Query("SELECT * FROM projects WHERE id IN (SELECT projectId FROM tasks GROUP BY projectId HAVING COUNT(*) > 3)")
    suspend fun getProjectsWithMoreThan3Tasks(): List<Project>

    // Same but raw
    @RawQuery
    suspend fun getProjectsWithMoreThan3TasksRaw(query: SupportSQLiteQuery): List<Project>
}
