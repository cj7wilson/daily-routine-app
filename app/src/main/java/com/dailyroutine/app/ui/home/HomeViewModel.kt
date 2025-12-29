package com.dailyroutine.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dailyroutine.app.billing.BillingManager
import com.dailyroutine.app.data.repository.RoutineRepository
import com.dailyroutine.app.domain.model.Routine
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: RoutineRepository,
    private val billingManager: BillingManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    val isPremium: StateFlow<Boolean> = billingManager.isPremium
    
    private val _refreshTrigger = MutableStateFlow(0)

    init {
        loadRoutines()
        
        // Also observe refresh trigger
        viewModelScope.launch {
            _refreshTrigger.collect {
                loadRoutinesSnapshot()
            }
        }
    }

    private fun loadRoutines() {
        viewModelScope.launch {
            loadRoutinesSnapshot()
        }
    }
    
    private suspend fun loadRoutinesSnapshot() {
        try {
            val routines = repository.getAllRoutinesSnapshot()
            _uiState.value = if (routines.isEmpty()) {
                HomeUiState.Empty
            } else {
                HomeUiState.Success(routines)
            }
        } catch (e: Exception) {
            _uiState.value = HomeUiState.Error(e.message ?: "Unknown error")
        }
    }

    fun toggleItemCompletion(itemId: Long) {
        viewModelScope.launch {
            repository.toggleItemCompletion(itemId)
            // Refresh the list
            _refreshTrigger.value++
        }
    }

    fun deleteRoutine(routineId: Long) {
        viewModelScope.launch {
            repository.deleteRoutine(routineId)
            // Refresh the list
            _refreshTrigger.value++
        }
    }

    suspend fun canCreateMoreRoutines(): Boolean {
        val count = repository.getActiveRoutineCount()
        return billingManager.canCreateMoreRoutines(count)
    }
    
    fun refresh() {
        viewModelScope.launch {
            _refreshTrigger.value++
        }
    }
}

sealed class HomeUiState {
    object Loading : HomeUiState()
    object Empty : HomeUiState()
    data class Success(val routines: List<Routine>) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}
