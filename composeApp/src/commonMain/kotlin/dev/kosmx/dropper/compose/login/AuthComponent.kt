package dev.kosmx.dropper.compose.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import dev.kosmx.dropper.Util
import dev.kosmx.dropper.data.KtorDataAccess
import dev.kosmx.dropper.data.LocalDataStore
import dev.kosmx.dropper.data.net.provideHttpBaseClient
import dropper.composeapp.generated.resources.Res
import dropper.composeapp.generated.resources.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject



@Composable
fun AuthComponent(
    authRepo: LocalDataStore = koinInject(),
    content: @Composable () -> Unit,
) {
    val token by authRepo.authToken.collectAsState()
    val server by authRepo.serverAddress.collectAsState()
    if (server == null || token == null) {
        // login screen

        var typedServer by remember { mutableStateOf(server ?: "https://") }
        var typedText by remember { mutableStateOf(token ?: "") }
        var warning: String? by remember { mutableStateOf(null) }

        // current check-token job, or null if there is none
        var job: Job? by remember { mutableStateOf(null) }

        fun setToken(server: String, t: String) {
            val server = server.trimEnd('/') + "/"
            if (job == null) {
                job = MainScope().launch {
                    val ok = withContext(Util.IO) {
                        try {
                            val repo = KtorDataAccess(provideHttpBaseClient(server, t))
                            val test = repo.ping()
                            if (test.status == HttpStatusCode.OK && test.bodyAsText() == "owo") {
                                true
                            } else {
                                warning = if (test.status == HttpStatusCode.NotFound) "No dropper server on address" else if (test.status == HttpStatusCode.Unauthorized) "Incorrect token" else test.status.description
                                false
                            }
                        } catch (e: Throwable) {
                            warning = e.message ?: e.toString()
                            false
                        }
                    }

                    if (ok) {
                        // and this will trigger the whole auth screen to recompose without login content, and with app content
                        authRepo.setServerAddress(server)
                        authRepo.setToken(t)
                    }
                    job = null // :D
                }
            }
        }

        Column(
            modifier = Modifier.padding(8.dp).fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {


            OutlinedTextField(
                modifier = Modifier.width(560.dp)
                    .padding(8.dp),
                value = typedServer,
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Home, contentDescription = "Server")
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Uri,
                    imeAction = ImeAction.Next,
                ),
                shape = RoundedCornerShape(8.dp),
                onValueChange = {
                    val lines = it.lines()
                    typedServer = lines.first()
                    if (lines.size > 1) {
                        // next ?
                    }

                },
            )

            OutlinedTextField(
                modifier = Modifier.width(560.dp)
                    .padding(8.dp),
                value = typedText,
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = "Token")
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Ascii,
                    imeAction = ImeAction.Done,
                ),
                shape = RoundedCornerShape(8.dp),
                onValueChange = {
                    val lines = it.lines()
                    typedText = lines.first()
                    if (lines.size > 1) {
                        setToken(typedServer, typedText)
                    }

                },
            )

            Button(
                modifier = Modifier.width(TextFieldDefaults.MinWidth).padding(8.dp),
                onClick = {
                    setToken(typedServer, typedText)
                },
                enabled = job == null
            ) {
                Icon(imageVector = Icons.Default.Done, contentDescription = "Done")
                Text(stringResource(Res.string.login_done))
            }

            if (warning != null) {
                Text("Failed to connect, error: $warning", color = Color.Red)
            }
        }


    } else {
        // if token already exists, just go forward and assume it is valid.
        content()
    }
}