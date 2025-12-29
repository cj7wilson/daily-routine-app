package com.dailyroutine.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "item_completions",
    foreignKeys = [
        ForeignKey(
            entity = ChecklistItemEntity::class,
            parentColumns = ["id"],
            childColumns = ["itemId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("itemId"), Index("completionDate")]
)
data class ItemCompletionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val itemId: Long,
    val completionDate: String, // Format: YYYY-MM-DD
    val isCompleted: Boolean,
    val completedAt: Long = System.currentTimeMillis()
)
