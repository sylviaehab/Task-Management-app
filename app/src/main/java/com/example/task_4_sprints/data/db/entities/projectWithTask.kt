package com.example.task_4_sprints.data.db.entities

import androidx.room.Embedded
import androidx.room.Relation
import com.example.task_4_sprints.data.db.entities.Project
import com.example.task_4_sprints.data.db.entities.Task

data class ProjectWithTasks(
    @Embedded val project: Project,
    @Relation(
        parentColumn = "id",
        entityColumn = "projectId"
    )
    val tasks: List<Task>
)