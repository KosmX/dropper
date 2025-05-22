package dev.kosmx.dropper.compose.upload

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.kosmx.dropper.Util
import dev.kosmx.dropper.data.DataAccess
import dev.kosmx.dropper.data.Upload
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
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
    private val uploads = MutableStateFlow<List<Upload>>(emptyList())
    val uploadList: StateFlow<List<Upload>>
        get() = uploads

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
                        val current = uploads.value.size
                        data.getUploads(page = current / 32, contentNameContains = search.takeIf { it.isNotBlank() })
                    }
                    if (entries.size != 32) {
                        reachedEnd = true
                    }
                    uploads.update { old -> old + entries }
                } finally {
                    loadingLock.unlock()
                }
            }
        }
    }

    init {
        loadMoreEntries()
    }
}

@OptIn(FlowPreview::class)
class UploadViewModel(
    val data: DataAccess,
): ViewModel() {


    private val mutableSearchState = MutableStateFlow(SearchState("", data, viewModelScope))
    val state: StateFlow<SearchState>
        get() = mutableSearchState

    private val activeSelected = MutableStateFlow<Upload?>(null)
    val selected: StateFlow<Upload?>
        get() = activeSelected


    private val searchState = MutableStateFlow("")
    val search: StateFlow<String>
        get() = searchState

    fun updateSearchString(update: String) {
        searchState.value = update.trim()
    }

    fun select(upload: Upload) {
        activeSelected.value = upload
    }

    init {
        viewModelScope.launch {
            search.debounce(1.seconds).collectLatest { search ->
                mutableSearchState.value = SearchState(search, data, viewModelScope)
            }
        }
    }
}