package dev.kosmx.dropper.compose.createContent

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dropper.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

enum class DurationUnit(val convertInt: (Int) -> Duration) {
    SECONDS({ it.seconds }),
    MINUTES({ it.minutes }),
    HOURS({ it.hours }),
    DAYS({ it.days }),
    WEEKs({ (it*7).days }),
}

@OptIn(ExperimentalTime::class)
@Composable
fun NewSessionScreen(
    newSessionModel: CreateSessionViewModel,
    modifier: Modifier = Modifier
) {
    val state by newSessionModel.loading.collectAsState()
    val createMode = newSessionModel.createMode

    val scrollState = rememberScrollState()

    var sessionState by remember { mutableStateOf(newSessionModel.initialShareSession) }

    // TOOD verify
    var timeLength: String by remember { mutableStateOf("30") }
    var timeUnit: DurationUnit by remember { mutableStateOf(DurationUnit.MINUTES) }

    Column(
        modifier = modifier.padding(8.dp)
            .width(500.dp)
            .scrollable(
                scrollState,
                orientation = Orientation.Horizontal
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            stringResource(if (createMode) Res.string.new_create_new else Res.string.new_authorize_new),
            fontSize = 24.sp
        )
        HorizontalDivider()

        OutlinedTextField(
            modifier = modifier.fillMaxWidth().padding(8.dp),
            value = sessionState.name ?: "",
            label = { Text(stringResource(Res.string.new_name)) },
            onValueChange = {
                sessionState = sessionState.copy(name = it.lineSequence().firstOrNull() ?: "")
            },
            maxLines = 1,
        )

        // instead of a full date-picker, I'll just create an int input and a metric select next to each other

        HorizontalDivider()

        OutlinedTextField(
            modifier = modifier.fillMaxWidth().padding(8.dp),
            value = timeLength,
            label = { Text(stringResource(Res.string.new_timeout)) },
            onValueChange = {
                timeLength = it
            },
            maxLines = 1,
            isError = timeLength.toIntOrNull() == null,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            )
        )

        val units = listOf(DurationUnit.MINUTES, DurationUnit.HOURS, DurationUnit.DAYS)

        SingleChoiceSegmentedButtonRow (
            modifier = modifier.fillMaxWidth().padding(8.dp)
        ) {
            units.forEachIndexed { index, unit ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = units.size
                    ),
                    onClick = { timeUnit = units[index] },
                    selected = units[index] == timeUnit,
                    label = { Text(unit.name.lowercase()) }
                )
            }
        }

        HorizontalDivider()

        SingleChoiceSegmentedButtonRow(
            modifier = modifier.fillMaxWidth().padding(8.dp)
        ) {
            val size = 3
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(
                    0, size
                ),
                onClick = { sessionState = sessionState.copy(allowMultipleFiles = !sessionState.allowMultipleFiles) },
                selected = sessionState.allowMultipleFiles,
                label = { Text(stringResource(Res.string.new_multiple)) }
            )

            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(
                    1, size
                ),
                onClick = {
                    sessionState = sessionState.copy(
                        uploadLimit = -sessionState.uploadLimit
                    )
                },
                selected = sessionState.uploadLimit == -1,
                label = { Text(stringResource(Res.string.new_reupload)) }
            )

            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(
                    2, size
                ),
                onClick = { sessionState = sessionState.copy(overrideReupload = !sessionState.overrideReupload) },
                selected = sessionState.overrideReupload && sessionState.uploadLimit == -1,
                enabled = sessionState.uploadLimit == -1,
                label = { Text(stringResource(Res.string.new_delreupload)) }
            )
        }

        HorizontalDivider()

        Button(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            onClick = {
                newSessionModel.createSession(
                    sessionState.copy(
                        expirationDate = Clock.System.now() + timeUnit.convertInt(
                            timeLength.toInt()
                        )
                    )
                )
            },
            enabled = timeLength.toIntOrNull() != null && !state,
        ) {
            AnimatedContent(
                state,
                label = ":D"
            ) { loading ->
                if (loading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                } else {
                    Text(stringResource(if (createMode) Res.string.new_done_create else Res.string.new_done_authorize))
                }
            }
        }

    }
}