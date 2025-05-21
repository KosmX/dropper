package dev.kosmx.dropper.compose.menu

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dev.kosmx.dropper.navigation.Screen
import dropper.composeapp.generated.resources.Res
import dropper.composeapp.generated.resources.nav_create
import org.jetbrains.compose.resources.StringResource
import kotlin.reflect.KClass

enum class NavigationEntries(
    val title: StringResource,
    val icon: ImageVector,
    val route: KClass<out Screen>
) {
    CREATE(Res.string.nav_create, Icons.Default.Add, Screen.Create::class)
}

@Composable
fun NavigationMenu(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination


    val state by remember { mutableStateOf("") }

    NavigationSuiteScaffold(
        modifier = modifier,
        navigationSuiteItems = {

        }
    ) {

    }
}