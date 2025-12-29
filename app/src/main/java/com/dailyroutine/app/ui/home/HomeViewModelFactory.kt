package com.dailyroutine.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dailyroutine.app.billing.BillingManager
import com.dailyroutine.app.data.repository.RoutineRepository

class HomeViewModelFactory(
    private val repository: RoutineRepository,
    private val billingManager: BillingManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(repository, billingManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
