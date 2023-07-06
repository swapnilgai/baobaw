package com.java.cherrypick.di

import com.java.cherrypick.executor.MainDispatcher
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    single { MainDispatcher() }
}
