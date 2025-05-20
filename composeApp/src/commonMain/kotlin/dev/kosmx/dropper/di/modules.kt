package dev.kosmx.dropper.di

import dev.kosmx.dropper.data.LocalDataStore
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module

expect fun getPlatformModule(): Module

val platformModule = getPlatformModule()

val dataModule = module {

}

fun KoinApplication.initKoin() {
    modules(platformModule, dataModule)
}