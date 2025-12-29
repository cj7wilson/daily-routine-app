package com.dailyroutine.app.data.local.dao

import androidx.room.*
import com.dailyroutine.app.data.local.entity.ChecklistItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChecklistItemDao {
    @Query("SELECT * FROM checklist_items WHERE routineId = :routineId ORDER BY orderIndex ASC")
    fun getItemsForRoutine(routineId: Long): Flow<List<ChecklistItemEntity>>

    @Query("SELECT * FROM checklist_items WHERE id = :itemId")
    suspend fun getItemById(itemId: Long): ChecklistItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: ChecklistItemEntity): Long

    @Update
    suspend fun updateItem(item: ChecklistItemEntity)

    @Delete
    suspend fun deleteItem(item: ChecklistItemEntity)

    @Query("DELETE FROM checklist_items WHERE routineId = :routineId")
    suspend fun deleteItemsForRoutine(routineId: Long)
}
