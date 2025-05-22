package dev.kosmx.dropper.compose.upload

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.PaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import dev.kosmx.dropper.compose.components.UploadCard
import dev.kosmx.dropper.compose.components.UploadEntry
import dev.kosmx.dropper.data.ShareSession
import dropper.composeapp.generated.resources.Res
import dropper.composeapp.generated.resources.search
import dropper.composeapp.generated.resources.upload_list
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun UploadListScreen(
    nav: NavHostController,
    uploadViewModel: UploadViewModel = koinInject()
) {
    val searchString by uploadViewModel.search.collectAsState()

    val listState by uploadViewModel.state.collectAsState()
    val entryList by listState.uploadList.collectAsState()

    val selectedEntry by uploadViewModel.selected.collectAsState()

    val scaffoldNavigator = rememberListDetailPaneScaffoldNavigator<ShareSession>()

    Column {
        Text(stringResource(Res.string.upload_list), fontSize = 30.sp)

        ListDetailPaneScaffold(
            value = scaffoldNavigator.scaffoldValue,
            directive = PaneScaffoldDirective.Default,
            listPane = {
                AnimatedPane {
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        value = searchString,
                        label = { Text(stringResource(Res.string.search)) },
                        onValueChange = {
                            uploadViewModel.updateSearchString(it)
                        },
                        maxLines = 1,
                    )
                    HorizontalDivider()

                    val columnState = rememberLazyListState()

                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        state = columnState
                    ) {
                        itemsIndexed(items = entryList, key = { idx, it -> it.id }) { i, upload ->
                            SideEffect {
                                if (i > entryList.size / 2) {
                                    listState.loadMoreEntries()
                                }
                            }

                            UploadCard(
                                upload,
                                modifier = Modifier.clickable {
                                    uploadViewModel.select(upload)
                                },
                                backgroundColor = if (upload.id == selectedEntry?.id) {
                                    MaterialTheme.colors.primary
                                } else {
                                    MaterialTheme.colors.secondary
                                }
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            },
            detailPane = {
                AnimatedPane {
                    selectedEntry?.let { it ->
                        UploadEntry(it)
                    }
                }
            },
            modifier = Modifier.width(1024.dp).fillMaxWidth(),
        )
    }
}