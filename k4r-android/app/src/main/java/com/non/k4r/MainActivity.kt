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
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import com.non.k4r.module.common.ChatRoute
import com.non.k4r.module.common.ExpenditureSubmitRoute
import com.non.k4r.module.common.FeatureCatalogRoute
import com.non.k4r.module.common.FeatureCatalogScreen
import com.non.k4r.module.common.MainRoute
import com.non.k4r.module.common.MainScreen
import com.non.k4r.module.common.SettingsRoute
import com.non.k4r.module.common.SettingsScreen
import com.non.k4r.module.common.TodoSubmitRoute
import com.non.k4r.module.chat.ChatRoute
import com.non.k4r.module.expenditure.component.ExpenditureSubmitScreen
import com.non.k4r.module.expenditure.vm.MainScreenViewModel
import com.non.k4r.module.todo.component.TodoSubmitScreen
import com.non.k4r.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = MainRoute) {
                    composable<MainRoute>(
                        enterTransition = { slideInHorizontally(animationSpec = tween(400)) { it } },
                        exitTransition = { slideOutHorizontally(animationSpec = tween(400)) { -it } }
                    ) { backStackEntry ->
                        MainScreen(navController = navController)
                    }
                    composable<FeatureCatalogRoute>(
                        enterTransition = { slideInHorizontally(animationSpec = tween(400)) { it } },
                        exitTransition = { slideOutHorizontally(animationSpec = tween(400)) { -it } }
                    ) {
                        FeatureCatalogScreen(navController = navController)
                    }
                    composable<ExpenditureSubmitRoute>(
                        enterTransition = { slideInHorizontally(animationSpec = tween(400)) { it } },
                        exitTransition = { slideOutHorizontally(animationSpec = tween(400)) { -it } }
                    ) {
                        ExpenditureSubmitScreen(onSubmitSuccess = {
                            navController.navigate(MainRoute)
                        })
                    }
                    composable<TodoSubmitRoute>(
                        enterTransition = { slideInHorizontally(animationSpec = tween(400)) { it } },
                        exitTransition = { slideOutHorizontally(animationSpec = tween(400)) { -it } }
                    ) {
                        TodoSubmitScreen(navController = navController)
                    }
                    composable<ChatRoute>(
                        enterTransition = { slideInHorizontally(animationSpec = tween(400)) { it } },
                        exitTransition = { slideOutHorizontally(animationSpec = tween(400)) { -it } }
                    ) {
                        ChatRoute()
                    }
                    composable<SettingsRoute>(
                        enterTransition = { slideInHorizontally(animationSpec = tween(400)) { it } },
                        exitTransition = { slideOutHorizontally(animationSpec = tween(400)) { -it } }
                    ) {
                        SettingsScreen(navController = navController)
                    }

                }
            }
        }
    }
}