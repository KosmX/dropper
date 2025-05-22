package dev.kosmx.dropper.compose.sesson

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Button
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
import androidx.navigation.NavHostController
import dev.kosmx.dropper.compose.components.SessionCard
import dev.kosmx.dropper.compose.components.SessionEntry
import dev.kosmx.dropper.data.ShareSession
import dev.kosmx.dropper.navigation.Screen
import dropper.composeapp.generated.resources.Res
import dropper.composeapp.generated.resources.search
import dropper.composeapp.generated.resources.update_session
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun SessionListScreen(
    nav: NavHostController,
    sessionsViewModel: SessionsViewModel = koinInject()
) {
    val searchString by sessionsViewModel.search.collectAsState()

    val listState by sessionsViewModel.state.collectAsState()
    val entryList by listState.sessionList.collectAsState()

    val selectedEntry by sessionsViewModel.selected.collectAsState()

    val scaffoldNavigator = rememberListDetailPaneScaffoldNavigator<ShareSession>()

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
                        sessionsViewModel.updateSearchString(it)
                    },
                    maxLines = 1,
                )
                HorizontalDivider()

                val columnState = rememberLazyListState()

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    state = columnState
                ) {
                    itemsIndexed(items = entryList, key = { idx, it -> it.id }) { i, session ->
                        SideEffect {
                            if (i > entryList.size/2) {
                                listState.loadMoreEntries()
                            }
                        }

                        SessionCard(
                            session = session,
                            modifier = Modifier.clickable {
                                sessionsViewModel.select(session)
                            },
                            backgroundColor =
                                if (selectedEntry?.id == session.id) {
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
                if (selectedEntry != null) {

                    SessionEntry(selectedEntry!!, nav)

                    Button(
                        onClick = {
                            nav.navigate(Screen.Update(selectedEntry!!.id))
                        }
                    ) {
                        Text(stringResource(Res.string.update_session))
                    }
                }
            }
        },
        modifier = Modifier.width(1024.dp).fillMaxWidth(),
    )
}