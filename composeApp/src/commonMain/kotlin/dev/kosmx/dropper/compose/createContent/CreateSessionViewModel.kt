package dev.kosmx.dropper.compose.createContent

import androidx.lifecycle.ViewModel
import androidx.navigation.NavGraph
import dev.kosmx.dropper.Util
import dev.kosmx.dropper.data.DataAccess
import dev.kosmx.dropper.data.ShareSession
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.ExperimentalTime

abstract class CreateSessionViewModel: ViewModel() {

    /**
     * The initial Share session, with some data.
     */
    @OptIn(ExperimentalTime::class)
    open val initialShareSession: ShareSession
        get() = ShareSession(
            id = 0,
            publicID = "uninitialized",
            name = "unnamed",
            expirationDateString = (Clock.System.now() + 1.days).toString()
        )

    abstract val createMode: Boolean

    protected val actualIsLoading = MutableStateFlow(false)

    /**
     * Whether server api is in progress, or the client is still waiting for user-input
     */
    val loading: StateFlow<Boolean>
        get() = actualIsLoading

    /**
     * Create a new share
     * this session should be a copy of [initialShareSession]
     */
    abstract fun createSession(session: ShareSession)

    /**
     * Cancel the creation action explicitly (step back)
     */
    abstract fun cancel()
}

/**
 * Create a brand-new view session
 */
class CreateNewShareViewModel(
    val nav: NavGraph,
    private val data: DataAccess,
): CreateSessionViewModel() {
    @OptIn(ExperimentalTime::class)

    override fun createSession(session: ShareSession) {
        if (actualIsLoading.value) return
        MainScope().launch {
            actualIsLoading.value = true
            val result: ShareSession = withContext(Util.IO) {
                data.createSession(session)
            }

            TODO("Display new session code")
        }
    }

    override fun cancel() {
        // TODO
    }

    override val createMode: Boolean
        get() = true
}

class AuthorizeShareViewModel(
    val id: String,
    val navGraph: NavGraph
): CreateSessionViewModel() {
    override val createMode: Boolean
        get() = false

    override fun createSession(session: ShareSession) {
        TODO("Not yet implemented")
    }

    override fun cancel() {
        TODO("Not yet implemented")
    }
}

