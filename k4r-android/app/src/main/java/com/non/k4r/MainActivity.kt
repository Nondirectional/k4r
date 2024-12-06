package com.non.k4r

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.non.k4r.module.common.ExpenditureSubmitRoute
import com.non.k4r.module.common.FeatureCatalogRoute
import com.non.k4r.module.common.FeatureCatalogScreen
import com.non.k4r.module.common.MainRoute
import com.non.k4r.module.common.MainScreen
import com.non.k4r.module.common.TodoSubmitRoute
import com.non.k4r.module.expenditure.component.ExpenditureSubmitScreen
import com.non.k4r.module.expenditure.vm.MainScreenViewModel
import com.non.k4r.module.todo.component.TodoSubmitScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = MainRoute) {
                composable<MainRoute> { backStackEntry ->
                    MainScreen(navController = navController)
                }
                composable<FeatureCatalogRoute> {
                    FeatureCatalogScreen(navController = navController)
                }
                composable<ExpenditureSubmitRoute> {
                    ExpenditureSubmitScreen(onSubmitSuccess = {
                        navController.navigate(MainRoute)
                    })
                }
                composable<TodoSubmitRoute> {
                    TodoSubmitScreen(navController = navController)
                }

            }
        }
    }
}