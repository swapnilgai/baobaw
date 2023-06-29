package com.java.cherrypick.feature.auth.di

import com.java.cherrypick.feature.auth.interactor.AuthInteractor
import com.java.cherrypick.feature.auth.interactor.AuthInteractorImple
import com.java.cherrypick.feature.auth.presentation.AuthViewModel
import org.koin.dsl.module

val authModule = module {
    single<AuthInteractor> { AuthInteractorImple(get()) }
    single<AuthViewModel> { AuthViewModel(get()) }
}