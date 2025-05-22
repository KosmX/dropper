package dev.kosmx.dropper.compose.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.kosmx.dropper.data.Upload
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class, ExperimentalMaterial3Api::class)
@Composable
fun UploadCard(
    upload: Upload,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = backgroundColor,
        contentColor = MaterialTheme.colors.onBackground,
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Column {
                Text("Uploaded at ${Instant.fromEpochSeconds(upload.uploadDate)}")
                HorizontalDivider()
                Text("${upload.files.size} files")
                HorizontalMultiBrowseCarousel(
                    state = rememberCarouselState { upload.files.size },
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(top = 16.dp, bottom = 16.dp),
                    preferredItemWidth = 186.dp,
                    itemSpacing = 8.dp,
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) { i ->
                    val file = upload.files[i]
                    Column {
                        Text(
                            text = file.name,
                            fontWeight = FontWeight.Bold,
                        )
                        HorizontalDivider()
                        Text("date: $${Instant.fromEpochSeconds(file.fileDate)}")
                        Text(file.sizeAsText())
                    }
                }
            }
        }
    }
}