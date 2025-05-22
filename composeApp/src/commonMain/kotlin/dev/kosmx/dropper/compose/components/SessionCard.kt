package dev.kosmx.dropper.compose.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.kosmx.dropper.data.ShareSession
import dropper.composeapp.generated.resources.Res
import dropper.composeapp.generated.resources.new_delreupload
import dropper.composeapp.generated.resources.new_multiple
import org.jetbrains.compose.resources.stringResource
import kotlin.math.absoluteValue
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class, ExperimentalMaterialApi::class)
@Composable
fun SessionCard(
    session: ShareSession,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = backgroundColor,
        contentColor = MaterialTheme.colors.onBackground
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = session.name ?: "Unnamed",
                maxLines = 1,
                style = TextStyle.Default.copy(
                    fontWeight = if (session.name == null) FontWeight.Normal else FontWeight.Bold,
                    fontStyle = if (session.name == null) FontStyle.Italic else FontStyle.Normal,
                    fontSize = 32.sp
                )
            )
            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(session.publicID)
                    val status: String = when {
                        session.expirationDate < Clock.System.now() -> "Expired"
                        session.privateID != null -> "Shareable"
                        else -> "Normal"
                    }
                    Text(status)
                }
                Column {
                    val time: String = when {
                        session.expirationDate + 1.days > Clock.System.now() -> {
                            session.expirationDateString
                        }

                        session.expirationDate - 1.days > Clock.System.now() -> {
                            val diff = session.expirationDate - Clock.System.now()
                            if (diff.inWholeMinutes.absoluteValue > 120) "${diff.inWholeHours} hours"
                            else "${diff.inWholeMinutes} minutes"
                        }

                        else -> session.expirationDateString
                    }

                    Text(time)

                    SingleChoiceSegmentedButtonRow(
                        modifier = modifier.padding(8.dp).clickable(
                            enabled = false,
                            onClick = {}
                        )
                    ) {
                        val size = 3
                        SegmentedButton(
                            shape = SegmentedButtonDefaults.itemShape(
                                0, size
                            ),
                            selected = session.allowMultipleFiles,
                            label = { Text(stringResource(Res.string.new_multiple)) },
                            onClick = {}
                        )

                        SegmentedButton(
                            shape = SegmentedButtonDefaults.itemShape(
                                1, size
                            ),
                            onClick = { },
                            selected = session.uploadLimit != 0,
                            label = {
                                Text(
                                    if (session.uploadLimit != 0) {
                                        "${session.uploadLimit} remaining upload(s)"
                                    } else "No remaining upload"
                                )
                            }
                        )

                        SegmentedButton(
                            shape = SegmentedButtonDefaults.itemShape(
                                2, size
                            ),
                            onClick = { },
                            selected = session.overrideReupload,
                            label = { Text(stringResource(Res.string.new_delreupload)) }
                        )
                    }
                }
            }
        }
    }
}