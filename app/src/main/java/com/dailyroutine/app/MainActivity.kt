package com.dailyroutine.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.dailyroutine.app.billing.BillingManager
import com.dailyroutine.app.data.local.DailyRoutineDatabase
import com.dailyroutine.app.data.repository.RoutineRepository
import com.dailyroutine.app.ui.home.HomeScreen
import com.dailyroutine.app.ui.home.HomeViewModel
import com.dailyroutine.app.ui.routine.CreateRoutineDialog
import com.dailyroutine.app.ui.routine.RoutineDetailScreen
import com.dailyroutine.app.ui.routine.RoutineViewModel
import com.dailyroutine.app.ui.theme.DailyRoutineTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    
    private lateinit var repository: RoutineRepository
    private lateinit var billingManager: BillingManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val database = DailyRoutineDatabase.getDatabase(applicationContext)
        repository = RoutineRepository(database)
        billingManager = BillingManager(applicationContext)
        
        setContent {
            DailyRoutineTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DailyRoutineApp(repository, billingManager)
                }
            }
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        billingManager.destroy()
    }
}

@Composable
fun DailyRoutineApp(
    repository: RoutineRepository,
    billingManager: BillingManager
) {
    val navController = rememberNavController()
    var showCreateDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            val viewModel: HomeViewModel = viewModel(
                factory = HomeViewModelFactory(repository, billingManager)
            )
            
            HomeScreen(
                viewModel = viewModel,
                onNavigateToRoutine = { routineId ->
                    navController.navigate("routine/$routineId")
                },
                onCreateRoutine = {
                    coroutineScope.launch {
                        if (viewModel.canCreateMoreRoutines()) {
                            showCreateDialog = true
                        } else {
                            // Show premium upgrade dialog
                            // For now, just show the create dialog anyway
                            showCreateDialog = true
                        }
                    }
                }
            )
        }
        
        composable(
            route = "routine/{routineId}",
            arguments = listOf(navArgument("routineId") { type = NavType.LongType })
        ) { backStackEntry ->
            val routineId = backStackEntry.arguments?.getLong("routineId") ?: return@composable
            val viewModel: RoutineViewModel = viewModel(
                factory = RoutineViewModelFactory(repository)
            )
            
            RoutineDetailScreen(
                routineId = routineId,
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
    
    if (showCreateDialog) {
        val routineViewModel: RoutineViewModel = viewModel(
            factory = RoutineViewModelFactory(repository)
        )
        
        CreateRoutineDialog(
            onDismiss = { showCreateDialog = false },
            onConfirm = { name ->
                routineViewModel.createRoutine(name) { routineId ->
                    showCreateDialog = false
                    navController.navigate("routine/$routineId")
                }
            }
        )
    }
}
