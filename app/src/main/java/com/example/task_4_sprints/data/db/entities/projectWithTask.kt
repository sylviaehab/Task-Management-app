package com.example.task_4_sprints.data.db.relations
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.Relation
import com.example.task_4_sprints.data.db.entities.Task
import com.example.task_4_sprints.data.db.entities.Project



@Entity(tableName = "project_task_cross_ref",primaryKeys = ["projectId", "taskId"])
data class ProjectTaskCrossRef(
    val projectId: Int,
    val taskId: Int
)

data class ProjectWithTasks(
    @Embedded val project: Project,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = ProjectTaskCrossRef::class,
            parentColumn = "projectId",
            entityColumn = "taskId"
        )
    )
    val tasks: List<Task>
)

data class TaskWithProjects(
    @Embedded val task: Task,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = ProjectTaskCrossRef::class,
            parentColumn = "taskId",
            entityColumn = "projectId"
        )
    )
    val projects: List<Project>
)
