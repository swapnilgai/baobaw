package com.java.cherrypick.feature.upload.di

import com.java.cherrypick.feature.upload.interactor.ImageUploadInteractor
import com.java.cherrypick.feature.upload.interactor.ImageUploadInteractorImpl
import com.java.cherrypick.feature.upload.presentation.ImageSelectionViewModel
import org.koin.dsl.module


val uploadModule = module {
    single<ImageUploadInteractor> { ImageUploadInteractorImpl(get()) }
    single<ImageSelectionViewModel> { ImageSelectionViewModel(get()) }
}
