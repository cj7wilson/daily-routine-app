package com.dailyroutine.app.data.local.dao

import androidx.room.*
import com.dailyroutine.app.data.local.entity.RoutineEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RoutineDao {
    @Query("SELECT * FROM routines WHERE isActive = 1 ORDER BY createdAt ASC")
    fun getAllActiveRoutines(): Flow<List<RoutineEntity>>

    @Query("SELECT * FROM routines WHERE id = :routineId")
    suspend fun getRoutineById(routineId: Long): RoutineEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutine(routine: RoutineEntity): Long

    @Update
    suspend fun updateRoutine(routine: RoutineEntity)

    @Delete
    suspend fun deleteRoutine(routine: RoutineEntity)

    @Query("SELECT COUNT(*) FROM routines WHERE isActive = 1")
    suspend fun getActiveRoutineCount(): Int
}
