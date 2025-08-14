package com.example.task_4_sprints

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import androidx.sqlite.db.SimpleSQLiteQuery
import com.example.task_4_sprints.data.db.AppDatabase
import com.example.task_4_sprints.data.db.entities.Attachment
import com.example.task_4_sprints.data.db.entities.Project
import com.example.task_4_sprints.data.db.entities.Task
import com.example.task_4_sprints.data.db.entities.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.system.measureNanoTime
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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

            val userId = userDao.insert(User(name = "Alice", email = "alice@example.com"))
            val userId2 = userDao.insert(User(name = "Bob", email = "bob@example.com"))

            val projectId1 = projectDao.insert(Project(title = "Project Alpha", ownerId = userId.toInt()))
            val projectId2 = projectDao.insert(Project(title = "Project Beta", ownerId = userId2.toInt()))

            // Create multiple tasks (Alpha gets 4, Beta gets 2)
            repeat(4) { idx ->
                taskDao.insert(Task(description = "Alpha Task ${idx + 1}", projectId = projectId1.toInt()))
            }
            repeat(2) { idx ->
                taskDao.insert(Task(description = "Beta Task ${idx + 1}", projectId = projectId2.toInt()))
            }

            val sampleTask = taskDao.getTasksInProjectOnce(projectId1.toInt()).firstOrNull()
            sampleTask?.let { t ->
                attachmentDao.insert(Attachment(filePath = "/storage/emulated/0/Download/sample.png", taskId = t.id))
            }

            Log.d(TAG_DB, "Inserted: User($userId, $userId2), Projects($projectId1,$projectId2)")


            val projectWithTasks = projectDao.getProjectWithTasks(projectId1.toInt())
            Log.d(TAG_DB, "Project with Tasks: $projectWithTasks")


            val onceSnapshot = projectDao.getAllProjectsOnce()
            Log.d(TAG_DAO, "Suspend projects: $onceSnapshot")


            projectListState.clear()
            projectListState.addAll(onceSnapshot)

            // Collect Flow in background
            launch {
                projectDao.getAllProjectsFlow().collectLatest { list ->
                    Log.d(TAG_DAO, "Flow emission: $list")
                }
            }

            // Delay to allow Flow to emit
            delay(2000)

            // Performance measurement
            val sql = "SELECT * FROM projects WHERE id IN (SELECT projectId FROM tasks GROUP BY projectId HAVING COUNT(*) > 3)"
            val rawQuery = SimpleSQLiteQuery(sql)

            val RoomTime = measureNanoTime {
                repeat(100) { projectDao.getProjectsWithMoreThan3Tasks() }
            }
            Log.d(TAG_PERF, "Room query (100 runs): ${RoomTime}ns")

            val tRaw = measureNanoTime {
                repeat(100) { projectDao.getProjectsWithMoreThan3TasksRaw(rawQuery) }
            }
            Log.d(TAG_PERF, "Raw query (100 runs): ${tRaw}ns")

            // Attachments log
            val attachments = attachmentDao.getForTask(sampleTask?.id ?: -1)
            Log.d(TAG_DB, "Attachments for task ${sampleTask?.id}: $attachments")
        }
    }
}
