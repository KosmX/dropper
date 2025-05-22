package dev.kosmx.dropper.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraph
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import androidx.savedstate.read

@Composable
fun NavGraph(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Create(),
    ) {
        composable<Screen.Create> {
            TODO()
        }

        composable<Screen.Authorize> { entry ->
            val screen: Screen.Authorize = entry.toRoute()
            TODO()
        }

        composable<Screen.Session> {
            TODO()
        }

        composable<Screen.Upload> {
            TODO()
        }

        composable<Screen.Session> {
            TODO()
        }
    }
}