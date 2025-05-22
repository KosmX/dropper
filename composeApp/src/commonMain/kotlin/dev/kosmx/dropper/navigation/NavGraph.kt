package dev.kosmx.dropper.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import dev.kosmx.dropper.compose.createContent.AuthorizeShareViewModel
import dev.kosmx.dropper.compose.createContent.CodeInput
import dev.kosmx.dropper.compose.createContent.CreateNewShareViewModel
import dev.kosmx.dropper.compose.createContent.NewSessionScreen
import dropper.composeapp.generated.resources.Res
import dropper.composeapp.generated.resources.qr_done
import org.koin.compose.koinInject

@Composable
fun NavGraph(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Create,
        typeMap = mapOf()
    ) {
        composable<Screen.Create> {
            NewSessionScreen(
                newSessionModel = CreateNewShareViewModel(
                    nav = navController,
                    data = koinInject()
                )
            )
        }

        composable<Screen.Authorize> { entry ->
            val screen: Screen.Authorize = entry.toRoute()
            if (screen.id == null) {
                CodeInput(
                    onCodeInput = { input ->

                        navController.navigate(Screen.Authorize(input)) {
                            popUpTo<Screen.Create>()
                        }
                    },
                    doneButtonText = Res.string.qr_done
                )
            } else {
                NewSessionScreen(
                    newSessionModel = AuthorizeShareViewModel(
                        id = screen.id,
                        data = koinInject(),
                        nav = navController
                    )
                )
            }
        }

        composable<Screen.Session> {
            TODO()
        }

        composable<Screen.Upload> {
            TODO()
        }

        composable<Screen.Settings> {
            TODO()
        }

        composable<Screen.DisplayLink> {
            TODO()
        }
    }
}