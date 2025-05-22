package dev.kosmx.dropper.compose.createContent

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dropper.composeapp.generated.resources.Res
import dropper.composeapp.generated.resources.qr_back
import org.jetbrains.compose.resources.stringResource

@Composable
fun DisplayLinkScreen(
    publicID: String,
    privateID: String,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier.width(500.dp).height(500.dp)
        ) {
            Text("QR display helye :D")
        }

        TextField(
            value = "https:// TODO, session: $publicID, key: $privateID",
            readOnly = true,
            onValueChange = {}
        )
        // TODO copy button

        Button(
            onClick = onBack
        ) {
            Text(stringResource(Res.string.qr_back))
        }
    }
}