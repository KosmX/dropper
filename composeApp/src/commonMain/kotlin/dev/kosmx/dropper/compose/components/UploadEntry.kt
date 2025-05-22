package dev.kosmx.dropper.compose.components

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.kosmx.dropper.data.Upload
import dropper.composeapp.generated.resources.Res
import dropper.composeapp.generated.resources.upload
import org.jetbrains.compose.resources.stringResource
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalTime::class, ExperimentalUuidApi::class, ExperimentalStdlibApi::class)
@Composable
fun UploadEntry(
    upload: Upload
) {
    // TODO a lot of operations

    Text(
        text = stringResource(Res.string.upload),
        fontWeight = FontWeight.Bold,
    )
    Text("Upload date: ${Instant.fromEpochSeconds(upload.uploadDate)}")

    HorizontalDivider(modifier = Modifier.fillMaxWidth())

    LazyColumn(
        modifier = Modifier.fillMaxWidth()
    ) {
        items(upload.files, { it.id }) { file ->
            Box(
                modifier = Modifier.padding(8.dp).fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.combinedClickable(onClick = {
                        // TODO download?
                    }, onLongClick = {
                        // TODO context menu including delete, download...
                    })
                ) {
                    Row {
                        Text(file.name, fontWeight = FontWeight.Bold)
                        Text(file.sizeAsText())
                    }
                    Row {
                        Text(Instant.fromEpochSeconds(file.fileDate).toString())
                        Text(text = file.hash.toHexString(), fontSize = 14.sp, fontWeight = FontWeight.Thin)
                    }
                    Button(
                        onClick = {
                            // TODO Download, this is not easy to do on multiplatform
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = "Download"
                        )
                    }
                }
            }
        }
    }
}