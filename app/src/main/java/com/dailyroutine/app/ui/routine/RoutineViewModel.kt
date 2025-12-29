package com.dailyroutine.app.ui.routine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dailyroutine.app.data.repository.RoutineRepository
import com.dailyroutine.app.domain.model.ChecklistItem
import com.dailyroutine.app.domain.model.Routine
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class RoutineViewModel(
    private val repository: RoutineRepository
) : ViewModel() {

    private val _routine = MutableStateFlow<Routine?>(null)
    val routine: StateFlow<Routine?> = _routine.asStateFlow()

    fun loadRoutine(routineId: Long) {
        viewModelScope.launch {
            repository.getRoutineWithItems(routineId)
                .collect { routine ->
                    _routine.value = routine
                }
        }
    }

    fun createRoutine(name: String, onSuccess: (Long) -> Unit) {
        viewModelScope.launch {
            val id = repository.createRoutine(name)
            onSuccess(id)
        }
    }

    fun addItem(routineId: Long, description: String) {
        viewModelScope.launch {
            val routine = _routine.value
            val orderIndex = routine?.items?.size ?: 0
            repository.addItemToRoutine(routineId, description, orderIndex)
        }
    }

    fun deleteItem(itemId: Long) {
        viewModelScope.launch {
            repository.deleteItem(itemId)
        }
    }

    fun updateRoutine(routine: Routine) {
        viewModelScope.launch {
            repository.updateRoutine(routine)
        }
    }
}
