package com.java.baobaw.feature.upload.di

import com.java.baobaw.feature.upload.interactor.ImageUploadInteractor
import com.java.baobaw.feature.upload.interactor.ImageUploadInteractorImpl
import com.java.baobaw.feature.upload.presentation.ImageSelectionViewModel
import org.koin.dsl.module


val uploadModule = module {
    single<ImageUploadInteractor> { ImageUploadInteractorImpl(get(), get()) }
    single<ImageSelectionViewModel> { ImageSelectionViewModel(get()) }
}