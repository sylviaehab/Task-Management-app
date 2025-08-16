

package com.example.task_4_sprints.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.example.task_4_sprints.data.db.entities.Project
import com.example.task_4_sprints.data.db.relations.ProjectWithTasks
import com.example.task_4_sprints.data.db.relations.TaskWithProjects
import com.example.task_4_sprints.data.db.relations.ProjectTaskCrossRef
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {

    @Insert
    suspend fun insert(project: Project): Long

    @Update
    suspend fun update(project: Project)

    @Delete
    suspend fun delete(project: Project)


    @Query("SELECT * FROM projects")
    suspend fun getAllProjectsOnce(): List<Project>


    @Query("SELECT * FROM projects")
    fun getAllProjectsFlow(): Flow<List<Project>>


    @Query("SELECT * FROM projects")
    fun getAllProjectsLiveData(): LiveData<List<Project>>


    @Transaction
    @Query("SELECT * FROM projects WHERE id = :projectId")
    suspend fun getProjectWithTasks(projectId: Int): ProjectWithTasks


    @Transaction
    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskWithProjects(taskId: Int): TaskWithProjects


    @Insert
    suspend fun insertProjectTaskCrossRef(crossRef: ProjectTaskCrossRef)


    @Query("""
    SELECT * FROM projects
    WHERE id IN (
        SELECT projectId 
        FROM project_task_cross_ref
        GROUP BY projectId
        HAVING COUNT(taskId) > 3
    )
""")
    suspend fun getProjectsWithMoreThan3Tasks(): List<Project>


    @RawQuery
    suspend fun getProjectsWithMoreThan3TasksRaw(query: SupportSQLiteQuery): List<Project>
}
