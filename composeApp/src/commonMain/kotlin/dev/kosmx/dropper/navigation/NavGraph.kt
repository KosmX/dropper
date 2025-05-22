package dev.kosmx.dropper.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import dev.kosmx.dropper.compose.createContent.*
import dev.kosmx.dropper.compose.sesson.SessionListScreen
import dev.kosmx.dropper.compose.upload.UploadListScreen
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
            val screen = it.toRoute<Screen.Session>()
            SessionListScreen(
                navController,
                // TODO select entry based on path
            )
        }

        composable<Screen.Upload> {
            UploadListScreen(
                navController
            )
        }

        composable<Screen.Settings> {
            TODO()
        }

        composable<Screen.DisplayLink> {
            val screen = it.toRoute<Screen.DisplayLink>()
            DisplayLinkScreen(publicID = screen.publicID, screen.privateID) {
                navController.popBackStack()
            }
        }
    }
}