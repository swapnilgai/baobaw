package com.java.cherrypick.feature.auth.di

import com.java.cherrypick.feature.auth.interactor.AuthInteractor
import com.java.cherrypick.feature.auth.interactor.AuthInteractorImple
import com.java.cherrypick.feature.auth.presentation.AuthViewModel
import com.java.cherrypick.feature.auth.presentation.LoginViewModel
import com.java.cherrypick.feature.auth.presentation.VerifyUserViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.dsl.module

//Required this wrapping to access from ios app
class AuthKoinViewModelModule : KoinComponent {
    val authViewModel: AuthViewModel by inject()
}

val authModule = module {
    single<AuthInteractor> { AuthInteractorImple(get()) }
    single<AuthViewModel> { AuthViewModel(get()) }
    single<VerifyUserViewModel> { VerifyUserViewModel(get()) }
    single<LoginViewModel> { LoginViewModel(get()) }
}