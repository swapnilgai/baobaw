package com.java.baobaw.feature.common.di


import com.java.baobaw.feature.common.interactor.CompatibilityBatchInteractor
import com.java.baobaw.feature.common.interactor.CompatibilityBatchInteractorImpl
import com.java.baobaw.feature.common.interactor.SeasonInteractor
import com.java.baobaw.feature.common.interactor.SeasonInteractorImpl
import com.java.baobaw.feature.common.presentation.MainViewModel
import org.koin.dsl.module


val commonModule = module {
    single<SeasonInteractor> { SeasonInteractorImpl(get()) }
    single<CompatibilityBatchInteractor> { CompatibilityBatchInteractorImpl(get(), get()) }
    single<MainViewModel> { MainViewModel(get()) }
}
