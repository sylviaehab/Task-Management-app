package com.example.task_4_sprints.data.db.dao
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.task_4_sprints.data.db.entities.Attachment
@Dao
interface AttachmentDao {
    @Insert
    suspend fun insert(attachment: Attachment): Long

    @Query("SELECT * FROM attachments WHERE taskId = :taskId")
    suspend fun getForTask(taskId: Int): List<Attachment>
}