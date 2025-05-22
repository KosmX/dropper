package dev.kosmx.dropper.compose.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import dev.kosmx.dropper.Util
import dev.kosmx.dropper.data.DataAccess
import dev.kosmx.dropper.data.ShareSession
import dev.kosmx.dropper.data.Upload
import dev.kosmx.dropper.navigation.Screen
import dropper.composeapp.generated.resources.Res
import dropper.composeapp.generated.resources.session_files
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import kotlin.time.ExperimentalTime
import kotlin.time.Instant


@OptIn(ExperimentalTime::class, ExperimentalMaterial3Api::class)
@Composable
fun SessionEntry(
    session: ShareSession,
    nav: NavHostController,
    data: DataAccess = koinInject()
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier.fillMaxWidth().padding(8.dp)
    ) {
        SessionCard(session, backgroundColor = MaterialTheme.colorScheme.background)

        HorizontalDivider()

        Text(stringResource(Res.string.session_files))
        HorizontalDivider()

        var uploadsState by remember { mutableStateOf<List<Upload>?>(null)}

        LaunchedEffect(Unit) {
            launch {
                val results = withContext(Util.IO) { data.getUploads(session.id) }

                uploadsState = results
            }
        }

        AnimatedContent(uploadsState) { uploads ->
            if (uploads == null) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(uploads, { it.id }) { upload ->
                        UploadCard(
                            upload,
                            modifier = Modifier.clickable {
                                nav.navigate(Screen.Upload(upload.id))
                            },
                            backgroundColor = MaterialTheme.colorScheme.background
                        )
                    }
                }
            }
        }
    }
}