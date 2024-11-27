package com.non.k4r

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.non.k4r.module.common.K4rRoutes.ExpenditureSubmitRoute
import com.non.k4r.module.common.K4rRoutes.MainRoute
import com.non.k4r.module.common.MainScreen
import com.non.k4r.module.expenditure.ExpenditureSubmitScreen


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = MainRoute) {
                composable<MainRoute> {
                    MainScreen(onNavigateToExpenditureSubmit = {
                        navController.navigate(route = ExpenditureSubmitRoute)
                    })
                }
                composable<ExpenditureSubmitRoute> { ExpenditureSubmitScreen() }
            }
        }
    }
}