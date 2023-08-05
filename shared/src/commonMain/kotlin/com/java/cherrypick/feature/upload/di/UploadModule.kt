package com.java.cherrypick.feature.upload.di

import com.java.cherrypick.feature.upload.presentation.ImageSelectionViewModel
import org.koin.dsl.module


val uploadModule = module {
    single<ImageSelectionViewModel> { ImageSelectionViewModel() }
}
