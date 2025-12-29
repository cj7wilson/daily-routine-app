package com.dailyroutine.app.data.local.dao

import androidx.room.*
import com.dailyroutine.app.data.local.entity.ItemCompletionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemCompletionDao {
    @Query("SELECT * FROM item_completions WHERE itemId = :itemId AND completionDate = :date")
    suspend fun getCompletionForDate(itemId: Long, date: String): ItemCompletionEntity?

    @Query("SELECT * FROM item_completions WHERE completionDate = :date")
    fun getCompletionsForDate(date: String): Flow<List<ItemCompletionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompletion(completion: ItemCompletionEntity): Long

    @Update
    suspend fun updateCompletion(completion: ItemCompletionEntity)

    @Delete
    suspend fun deleteCompletion(completion: ItemCompletionEntity)

    @Query("DELETE FROM item_completions WHERE completionDate < :date")
    suspend fun deleteCompletionsBefore(date: String)
}
