package dev.kosmx.dropper.compose.menu

import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.DevicesOther
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import androidx.window.core.layout.WindowWidthSizeClass
import dev.kosmx.dropper.navigation.NavGraph
import dev.kosmx.dropper.navigation.Screen
import dropper.composeapp.generated.resources.Res
import dropper.composeapp.generated.resources.nav_create
import dropper.composeapp.generated.resources.nav_sessions
import dropper.composeapp.generated.resources.nav_uploads
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import kotlin.reflect.KClass

enum class NavigationEntries(
    val title: StringResource,
    val icon: ImageVector,
    val isSelected: (Screen) -> Boolean,
    val onClickRoute: () -> Screen,
) {
    CREATE(Res.string.nav_create, Icons.Default.Add, { it is Screen.Create || it is Screen.Authorize }, { Screen.Create() }),
    SESSIONS(Res.string.nav_sessions, Icons.Default.DevicesOther, Screen.Session::class, { Screen.Session() }),
    UPLOADS(Res.string.nav_uploads, Icons.Default.CloudDone, Screen.Upload::class, { Screen.Upload() }),

    ;
    constructor(title: StringResource, icon: ImageVector, klass: KClass<out Screen>, onClickRoute: () -> Screen): this(title, icon, { klass.isInstance(it) }, onClickRoute)
}

@Composable
fun NavigationMenu(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.toRoute<Screen>()


    val windowWidthSizeClass = currentWindowAdaptiveInfo().windowSizeClass.windowWidthSizeClass

    NavigationSuiteScaffold(
        modifier = modifier,
        navigationSuiteItems = {
            NavigationEntries.entries.forEach { entry ->
                item(
                    selected = currentDestination?.let { entry.isSelected(currentDestination) } ?: false,
                    onClick = {
                        navController.navigate(entry.onClickRoute()) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = entry.icon,
                            contentDescription = null
                        )
                    },
                    label = { Text(stringResource(entry.title)) }
                )
            }
        },
        layoutType = if (windowWidthSizeClass == WindowWidthSizeClass.MEDIUM)
            NavigationSuiteType.NavigationDrawer
        else
            NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(
                currentWindowAdaptiveInfo()
            )
    ) {
        NavGraph(navController)
    }
}
