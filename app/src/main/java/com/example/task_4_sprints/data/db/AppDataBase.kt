package com.example.task_4_sprints.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.task_4_sprints.data.db.dao.*
import com.example.task_4_sprints.data.db.entities.*
import com.example.task_4_sprints.data.db.relations.ProjectTaskCrossRef

@Database(
    entities = [
        User::class,
        Project::class,
        Task::class,
        Attachment::class,
        ProjectTaskCrossRef::class
    ],
    version = 3,
    exportSchema = true

)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun projectDao(): ProjectDao
    abstract fun taskDao(): TaskDao
    abstract fun attachmentDao(): AttachmentDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "task_manager_db"
            )
                .fallbackToDestructiveMigration()
                .build()
    }
}
