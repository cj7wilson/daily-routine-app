package com.dailyroutine.app.data.repository

import com.dailyroutine.app.data.local.DailyRoutineDatabase
import com.dailyroutine.app.data.local.entity.ChecklistItemEntity
import com.dailyroutine.app.data.local.entity.ItemCompletionEntity
import com.dailyroutine.app.data.local.entity.RoutineEntity
import com.dailyroutine.app.domain.model.ChecklistItem
import com.dailyroutine.app.domain.model.Routine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.*

class RoutineRepository(private val database: DailyRoutineDatabase) {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    fun getAllRoutines(): Flow<List<Routine>> {
        val today = getTodayString()
        return combine(
            database.routineDao().getAllActiveRoutines(),
            database.itemCompletionDao().getCompletionsForDate(today)
        ) { routines, completions ->
            val completionMap = completions.associateBy { it.itemId }
            
            // For each routine, we need to get its items
            // This is tricky in a Flow context, so we'll do it differently
            routines.map { routine ->
                Routine(
                    id = routine.id,
                    name = routine.name,
                    items = emptyList(), // Will be populated separately
                    createdAt = routine.createdAt
                )
            }
        }
    }

    fun getRoutineWithItems(routineId: Long): Flow<Routine?> {
        val today = getTodayString()
        return combine(
            database.checklistItemDao().getItemsForRoutine(routineId),
            database.itemCompletionDao().getCompletionsForDate(today)
        ) { items, completions ->
            val completionMap = completions.associateBy { it.itemId }
            val routine = database.routineDao().getRoutineById(routineId)
            routine?.let {
                Routine(
                    id = it.id,
                    name = it.name,
                    items = items.map { item ->
                        ChecklistItem(
                            id = item.id,
                            routineId = item.routineId,
                            description = item.description,
                            orderIndex = item.orderIndex,
                            isCompleted = completionMap[item.id]?.isCompleted ?: false
                        )
                    },
                    createdAt = it.createdAt
                )
            }
        }
    }
    
    suspend fun getRoutineWithItemsSnapshot(routineId: Long): Routine? {
        val today = getTodayString()
        val routine = database.routineDao().getRoutineById(routineId) ?: return null
        val items = database.checklistItemDao().getItemsForRoutine(routineId).first()
        val completions = database.itemCompletionDao().getCompletionsForDate(today).first()
        val completionMap = completions.associateBy { it.itemId }
        
        return Routine(
            id = routine.id,
            name = routine.name,
            items = items.map { item ->
                ChecklistItem(
                    id = item.id,
                    routineId = item.routineId,
                    description = item.description,
                    orderIndex = item.orderIndex,
                    isCompleted = completionMap[item.id]?.isCompleted ?: false
                )
            },
            createdAt = routine.createdAt
        )
    }
    
    suspend fun getAllRoutinesSnapshot(): List<Routine> {
        val today = getTodayString()
        val routines = database.routineDao().getAllActiveRoutines().first()
        val completions = database.itemCompletionDao().getCompletionsForDate(today).first()
        val completionMap = completions.associateBy { it.itemId }
        
        return routines.map { routine ->
            val items = database.checklistItemDao().getItemsForRoutine(routine.id).first()
            Routine(
                id = routine.id,
                name = routine.name,
                items = items.map { item ->
                    ChecklistItem(
                        id = item.id,
                        routineId = item.routineId,
                        description = item.description,
                        orderIndex = item.orderIndex,
                        isCompleted = completionMap[item.id]?.isCompleted ?: false
                    )
                },
                createdAt = routine.createdAt
            )
        }
    }

    suspend fun createRoutine(name: String): Long {
        val routine = RoutineEntity(name = name)
        return database.routineDao().insertRoutine(routine)
    }

    suspend fun updateRoutine(routine: Routine) {
        val entity = RoutineEntity(
            id = routine.id,
            name = routine.name,
            createdAt = routine.createdAt
        )
        database.routineDao().updateRoutine(entity)
    }

    suspend fun deleteRoutine(routineId: Long) {
        val routine = database.routineDao().getRoutineById(routineId)
        routine?.let {
            database.routineDao().deleteRoutine(it)
        }
    }

    suspend fun addItemToRoutine(routineId: Long, description: String, orderIndex: Int): Long {
        val item = ChecklistItemEntity(
            routineId = routineId,
            description = description,
            orderIndex = orderIndex
        )
        return database.checklistItemDao().insertItem(item)
    }

    suspend fun updateItem(item: ChecklistItem) {
        val entity = ChecklistItemEntity(
            id = item.id,
            routineId = item.routineId,
            description = item.description,
            orderIndex = item.orderIndex
        )
        database.checklistItemDao().updateItem(entity)
    }

    suspend fun deleteItem(itemId: Long) {
        val item = database.checklistItemDao().getItemById(itemId)
        item?.let {
            database.checklistItemDao().deleteItem(it)
        }
    }

    suspend fun toggleItemCompletion(itemId: Long) {
        val today = getTodayString()
        val completion = database.itemCompletionDao().getCompletionForDate(itemId, today)

        if (completion == null) {
            // Create new completion
            val newCompletion = ItemCompletionEntity(
                itemId = itemId,
                completionDate = today,
                isCompleted = true
            )
            database.itemCompletionDao().insertCompletion(newCompletion)
        } else {
            // Toggle existing completion
            val updated = completion.copy(
                isCompleted = !completion.isCompleted,
                completedAt = System.currentTimeMillis()
            )
            database.itemCompletionDao().updateCompletion(updated)
        }
    }

    suspend fun getActiveRoutineCount(): Int {
        return database.routineDao().getActiveRoutineCount()
    }

    private fun getTodayString(): String {
        return dateFormat.format(Date())
    }

    suspend fun cleanupOldCompletions() {
        // Keep completions for last 30 days
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -30)
        val cutoffDate = dateFormat.format(calendar.time)
        database.itemCompletionDao().deleteCompletionsBefore(cutoffDate)
    }
}
