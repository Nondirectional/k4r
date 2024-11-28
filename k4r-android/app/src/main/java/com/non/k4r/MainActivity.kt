package com.non.k4r

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.non.k4r.module.common.ExpenditureSubmitRoute
import com.non.k4r.module.common.MainRoute
import com.non.k4r.module.common.MainScreen
import com.non.k4r.module.common.db.AppDatabase
import com.non.k4r.module.common.entity.RecordEntity
import com.non.k4r.module.expenditure.ExpenditureSubmitScreen
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = AppDatabase.getDatabase(this)
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

        lifecycleScope.launch {
            // initialize logic here
        }
    }
}