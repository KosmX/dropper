package dev.kosmx.dropper.compose.createContent

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dropper.composeapp.generated.resources.Res
import dropper.composeapp.generated.resources.qr_token_input
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun CodeInput(
    onCodeInput: (String) -> Unit,
    doneButtonText: StringResource
) {

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier.width(760.dp).verticalScroll(scrollState),
    ) {
        var textInput by remember { mutableStateOf("") }

        Box(
            modifier = Modifier.width(500.dp).height(500.dp)
        ) {
            Text("QR scanner helye.")
        }

        HorizontalDivider()

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            value = textInput,
            label = { Text(stringResource(Res.string.qr_token_input)) },
            onValueChange = {
                textInput = it
            },
            maxLines = 1,
        )

        Button(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            onClick = {
                onCodeInput(textInput)
            },
        ) {
            Text(stringResource(doneButtonText))
        }
    }
}