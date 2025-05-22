package dev.kosmx.dropper.compose.createContent

import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import dev.kosmx.dropper.Util
import dev.kosmx.dropper.data.DataAccess
import dev.kosmx.dropper.data.ShareSession
import dev.kosmx.dropper.navigation.Screen
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.ExperimentalTime

abstract class CreateSessionViewModel(
    protected val nav: NavController,
): ViewModel() {

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
    open fun cancel() {
        nav.popBackStack()
    }

    abstract fun navigate()
}

/**
 * Create a brand-new view session
 */
class CreateNewShareViewModel(
    nav: NavController,
    private val data: DataAccess,
): CreateSessionViewModel(nav) {
    @OptIn(ExperimentalTime::class)

    override fun createSession(session: ShareSession) {
        MainScope().launch {
            if (actualIsLoading.value) return@launch
            actualIsLoading.value = true
            val result: ShareSession = withContext(Util.IO) {
                data.createSession(session)
            }

            nav.navigate(Screen.DisplayLink(result))
        }
    }

    override fun navigate() {
        nav.navigate(Screen.Authorize(null))
    }

    override val createMode: Boolean
        get() = true
}

class AuthorizeShareViewModel(
    val id: String,
    private val data: DataAccess,
    nav: NavController
): CreateSessionViewModel(nav) {
    override val createMode: Boolean
        get() = false

    override val initialShareSession: ShareSession
        get() = super.initialShareSession.copy(publicID = id)

    override fun createSession(session: ShareSession) {
        MainScope().launch {
            if (actualIsLoading.value) return@launch
            actualIsLoading.value = true

            val result = withContext(Util.IO) {
                data.insertSession(session.copy(publicID = id))
            }

            nav.navigate(Screen.Session(result))
        }
    }

    override fun navigate() {
        throw IllegalStateException()
    }
}

