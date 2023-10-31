package com.java.baobaw.feature.auth.di

import com.java.baobaw.feature.auth.interactor.AuthInteractor
import com.java.baobaw.feature.auth.interactor.AuthInteractorImple
import com.java.baobaw.feature.auth.presentation.AuthViewModel
import com.java.baobaw.feature.auth.presentation.LoginViewModel
import com.java.baobaw.feature.auth.presentation.PermissionViewModel
import com.java.baobaw.feature.auth.presentation.ResetPasswordViewModel
import com.java.baobaw.feature.auth.presentation.VerifyUserViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.dsl.module

//Required this wrapping to access from ios app
class AuthKoinViewModelModule : KoinComponent {
    val authViewModel: AuthViewModel by inject()
}

val authModule = module {
    single<AuthInteractor> { AuthInteractorImple(get(), get()) }
    single<AuthViewModel> { AuthViewModel(get(), get(), get()) }
    single<VerifyUserViewModel> { VerifyUserViewModel(get(), get()) }
    single<LoginViewModel> { LoginViewModel(get(), get()) }
    single<ResetPasswordViewModel> { ResetPasswordViewModel(get()) }
}

val permissionModule = module {
    single<PermissionViewModel> { PermissionViewModel(get()) }
}
