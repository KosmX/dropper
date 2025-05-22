package dev.kosmx.dropper.compose.sesson

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.kosmx.dropper.Util
import dev.kosmx.dropper.data.DataAccess
import dev.kosmx.dropper.data.ShareSession
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.seconds

/**
 * Any change in search will update the whole list, recomposition.
 * If search isn't updated, just query more
 */
class SearchState(
    val search: String,
    private val data: DataAccess,
    private val scope: CoroutineScope
) {
    private val sessions = MutableStateFlow<List<ShareSession>>(emptyList())
    val sessionList: StateFlow<List<ShareSession>>
        get() = sessions

    private var reachedEnd = false


    private val loadingLock = Mutex()

    /**
     * Get more entries from server
     */
    fun loadMoreEntries() {
        scope.launch {
            if (reachedEnd) return@launch // nothing to do
            val lock = loadingLock.tryLock()
            if (lock) {
                try {
                    val entries = withContext(Util.IO) {
                        val current = sessionList.value.size
                        data.getSessions(page = current / 32, nameContains = search.takeIf { it.isNotBlank() })
                    }
                    if (entries.size != 32) {
                        reachedEnd = true
                    }
                    sessions.update { old -> old + entries }
                } finally {
                    loadingLock.unlock()
                }
            }
        }
    }
}

@OptIn(FlowPreview::class)
class SessionsViewModel(
    val data: DataAccess,
): ViewModel() {


    private val mutableSearchState = MutableStateFlow(SearchState("", data, viewModelScope))
    val state: StateFlow<SearchState>
        get() = mutableSearchState

    private val activeSelected = MutableStateFlow<ShareSession?>(null)
    val selected: StateFlow<ShareSession?>
        get() = activeSelected


    private val searchState = MutableStateFlow("")
    val search: StateFlow<String>
        get() = searchState

    fun updateSearchString(update: String) {
        searchState.value = update.trim()
    }

    fun select(shareSession: ShareSession) {
        activeSelected.value = shareSession
    }

    init {
        viewModelScope.launch {
            search.debounce(1.seconds).collectLatest { search ->
                mutableSearchState.value = SearchState(search, data, viewModelScope)
            }
        }
    }
}