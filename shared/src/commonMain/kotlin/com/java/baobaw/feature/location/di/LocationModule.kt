package com.java.baobaw.feature.location.di

import com.java.baobaw.feature.location.interactor.LocationInteractor
import com.java.baobaw.feature.location.interactor.LocationInteractorImpl
import org.koin.dsl.module


val locationModule = module {
    single<LocationInteractor> { LocationInteractorImpl(get()) }
}
