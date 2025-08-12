package com.example.task_4_sprints.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.task_4_sprints.data.db.entities.User

@Dao
interface UserDao {
    @Insert
    suspend fun insert(user: User): Long

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getById(id: Int): User?

    @Query("SELECT * FROM users")
    suspend fun getAll(): List<User>
}