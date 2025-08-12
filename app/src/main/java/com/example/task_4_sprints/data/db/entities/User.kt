package com.example.task_4_sprints.data.db.entities
import androidx.room.Index
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true)]
)
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val email: String
)