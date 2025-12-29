package com.dailyroutine.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dailyroutine.app.data.local.dao.ChecklistItemDao
import com.dailyroutine.app.data.local.dao.ItemCompletionDao
import com.dailyroutine.app.data.local.dao.RoutineDao
import com.dailyroutine.app.data.local.entity.ChecklistItemEntity
import com.dailyroutine.app.data.local.entity.ItemCompletionEntity
import com.dailyroutine.app.data.local.entity.RoutineEntity

@Database(
    entities = [
        RoutineEntity::class,
        ChecklistItemEntity::class,
        ItemCompletionEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class DailyRoutineDatabase : RoomDatabase() {
    abstract fun routineDao(): RoutineDao
    abstract fun checklistItemDao(): ChecklistItemDao
    abstract fun itemCompletionDao(): ItemCompletionDao

    companion object {
        @Volatile
        private var INSTANCE: DailyRoutineDatabase? = null

        fun getDatabase(context: Context): DailyRoutineDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DailyRoutineDatabase::class.java,
                    "daily_routine_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
