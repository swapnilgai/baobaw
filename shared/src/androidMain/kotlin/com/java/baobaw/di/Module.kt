package com.java.baobaw.di

import com.java.baobaw.executor.MainDispatcher
import com.java.baobaw.util.Preferences
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    single { com.java.baobaw.executor.MainDispatcher() }
    single { com.java.baobaw.util.Preferences() }
}