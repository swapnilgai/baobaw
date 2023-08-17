package com.java.cherrypick.di

import com.java.cherrypick.executor.MainDispatcher
import com.java.cherrypick.util.Preferences
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    single { MainDispatcher() }
    single { Preferences() }
}