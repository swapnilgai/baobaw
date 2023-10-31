package com.java.baobaw.feature.common.presentation

import com.java.baobaw.feature.common.interactor.CompatibilityBatchInteractor
import com.java.baobaw.interactor.interactorLaunch
import com.java.baobaw.presentationInfra.BaseViewModel

class MainViewModel(private val compatibilityBatchInteractor: CompatibilityBatchInteractor) : BaseViewModel<Unit>(initialContent = Unit) {
    init {
        initCompatibilityBatchInBackground()
    }

    private fun initCompatibilityBatchInBackground() {
        viewModelScope.interactorLaunch {
            compatibilityBatchInteractor.initCompatibilityBatch()
        }
    }
}