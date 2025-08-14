package com.example.task_4_sprints.data.db.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.task_4_sprints.data.db.dao.ProjectDao
import com.example.task_4_sprints.data.db.dao.TaskDao
import com.example.task_4_sprints.data.db.entities.Project
import kotlinx.coroutines.flow.Flow

class ProjectViewModel(
    private val projectDao: ProjectDao,
    private val taskDao: TaskDao
) : ViewModel() {

    // LiveData for UI (list all projects)
    val allProjectsLiveData = projectDao.getAllProjectsLiveData()

    // Flow for tasks in a specific project (example projectId param can be provided by factory)
    fun tasksFlow(projectId: Int): Flow<List<com.example.task_4_sprints.data.db.entities.Task>> =
        taskDao.getTasksInProjectFlow(projectId)
}

class ProjectViewModelFactory(
    private val projectDao: ProjectDao,
    private val taskDao: TaskDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProjectViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProjectViewModel(projectDao, taskDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
