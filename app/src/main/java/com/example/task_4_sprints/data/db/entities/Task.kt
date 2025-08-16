package com.example.task_4_sprints.data.db.entities


import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tasks"

)
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val description: String,

)