package com.java.cherrypick.feature.location.di

import com.java.cherrypick.feature.location.interactor.LocationInteractor
import com.java.cherrypick.feature.location.interactor.LocationInteractorImpl
import org.koin.dsl.module


val locationModule = module {
    single<LocationInteractor> { LocationInteractorImpl(get()) }
}
