package com.dailyroutine.app.domain.model

data class Routine(
    val id: Long = 0,
    val name: String,
    val items: List<ChecklistItem> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)

data class ChecklistItem(
    val id: Long = 0,
    val routineId: Long,
    val description: String,
    val orderIndex: Int,
    val isCompleted: Boolean = false
)
