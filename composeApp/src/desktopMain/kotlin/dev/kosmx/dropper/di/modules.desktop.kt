package dev.kosmx.dropper.di

import dev.kosmx.dropper.data.LocalDataStore
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun getPlatformModule(): Module = module {
    single { LocalDataStore() }
}