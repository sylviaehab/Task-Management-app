package com.example.task_4_sprints

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.sqlite.db.SimpleSQLiteQuery
import com.example.task_4_sprints.data.db.AppDatabase
import com.example.task_4_sprints.data.db.entities.Attachment
import com.example.task_4_sprints.data.db.entities.Project
import com.example.task_4_sprints.data.db.entities.Task
import com.example.task_4_sprints.data.db.entities.User
import com.example.task_4_sprints.data.db.relations.ProjectTaskCrossRef
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.system.measureNanoTime

class MainActivity : ComponentActivity() {

    private val TAG_DB = "DB_TEST"
    private val TAG_DAO = "DAO_TEST"
    private val TAG_PERF = "PERF"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = AppDatabase.getInstance(applicationContext)
        val userDao = db.userDao()
        val projectDao = db.projectDao()
        val taskDao = db.taskDao()
        val attachmentDao = db.attachmentDao()

        val projectListState = mutableStateListOf<Project>()

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Text("Projects in DB", style = MaterialTheme.typography.titleLarge)
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyColumn {
                            items(projectListState) { project ->
                                Text("• ${project.title} (Owner: ${project.ownerId})")
                            }
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {

            val userId1 = userDao.insert(User(name = "Alice", email = "alice@example.com")).toInt()
            val userId2 = userDao.insert(User(name = "Bob", email = "bob@example.com")).toInt()


            val projectId1 = projectDao.insert(Project(title = "Project Alpha", ownerId = userId1)).toInt()
            val projectId2 = projectDao.insert(Project(title = "Project Beta", ownerId = userId2)).toInt()


            val alphaTasks = mutableListOf<Long>()
            repeat(4) { idx ->
                val taskId = taskDao.insert(Task(description = "Alpha Task ${idx + 1}"))
                alphaTasks.add(taskId)
            }

            val betaTasks = mutableListOf<Long>()
            repeat(2) { idx ->
                val taskId = taskDao.insert(Task(description = "Beta Task ${idx + 1}"))
                betaTasks.add(taskId)
            }


            alphaTasks.forEach { taskId ->
                projectDao.insertProjectTaskCrossRef(ProjectTaskCrossRef(projectId1, taskId.toInt()))
            }
            betaTasks.forEach { taskId ->
                projectDao.insertProjectTaskCrossRef(ProjectTaskCrossRef(projectId2, taskId.toInt()))
            }

            val sampleTaskId = alphaTasks.firstOrNull()?.toInt() ?: -1
            if (sampleTaskId != -1) {
                attachmentDao.insert(
                    Attachment(filePath = "/storage/emulated/0/Download/sample.png", taskId = sampleTaskId)
                )
            }

            Log.d(TAG_DB, "Inserted Users, Projects, Tasks, CrossRefs, Attachments")


            val projectWithTasks = projectDao.getProjectWithTasks(projectId1)
            Log.d(TAG_DB, "Project with Tasks: $projectWithTasks")

            val onceSnapshot = projectDao.getAllProjectsOnce()
            Log.d(TAG_DAO, "Suspend projects: $onceSnapshot")
            projectListState.clear()
            projectListState.addAll(onceSnapshot)


            projectDao.getAllProjectsFlow()
                .onEach { list ->
                    Log.d(TAG_DAO, "Flow emission: $list")
                    projectListState.clear()
                    projectListState.addAll(list)
                }
                .launchIn(lifecycleScope)


            val sql = """
                SELECT * FROM projects 
                WHERE id IN (
                    SELECT projectId FROM project_task_cross_ref 
                    GROUP BY projectId 
                    HAVING COUNT(taskId) > 3
                )
            """
            val rawQuery = SimpleSQLiteQuery(sql)

            val roomTime = measureNanoTime {
                repeat(100) { projectDao.getProjectsWithMoreThan3Tasks() }
            }
            Log.d(TAG_PERF, "Room query (100 runs): $roomTime ns")

            val rawTime = measureNanoTime {
                repeat(100) { projectDao.getProjectsWithMoreThan3TasksRaw(rawQuery) }
            }
            Log.d(TAG_PERF, "Raw query (100 runs): $rawTime ns")


            val attachments = attachmentDao.getForTask(sampleTaskId)
            Log.d(TAG_DB, "Attachments for task $sampleTaskId: $attachments")
        }
    }
}
