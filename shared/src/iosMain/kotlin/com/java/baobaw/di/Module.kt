package com.java.baobaw.di

import com.java.baobaw.executor.MainDispatcher
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    single { MainDispatcher() }
}
