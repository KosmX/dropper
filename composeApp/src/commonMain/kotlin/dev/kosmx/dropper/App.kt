package dev.kosmx.dropper

import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.kosmx.dropper.compose.login.AuthComponent
import dev.kosmx.dropper.compose.menu.NavigationMenu
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
        AuthComponent {
            NavigationMenu(modifier = Modifier.safeDrawingPadding())
        }
    }
}