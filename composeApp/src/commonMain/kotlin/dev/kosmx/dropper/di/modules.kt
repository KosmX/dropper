package dev.kosmx.dropper.di

import dev.kosmx.dropper.compose.sesson.SessionsViewModel
import dev.kosmx.dropper.compose.upload.UploadViewModel
import dev.kosmx.dropper.data.DataAccess
import dev.kosmx.dropper.data.KtorDataAccess
import dev.kosmx.dropper.data.LocalDataStore
import dev.kosmx.dropper.data.net.provideHttpBaseClient
import io.ktor.client.HttpClient
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

expect fun getPlatformModule(): Module

val platformModule = getPlatformModule()

val viewModels = module {
    viewModel { UploadViewModel(get()) }
    viewModel { SessionsViewModel(get()) }
}

val dataModule = module {

    // these are factories, even if these could be re-used, the authentication/configuration may change, it is safer this way
    factory<HttpClient> {
        val localDataStore: LocalDataStore = get()
        provideHttpBaseClient(localDataStore.serverAddress.value!!, localDataStore.authToken.value!!)
    }
    factory<DataAccess> { KtorDataAccess(get()) }
}

fun KoinApplication.initKoin() {
    modules(platformModule, dataModule, viewModels)
}