package com.java.baobaw.feature.common.interactor

import com.java.baobaw.interactor.Interactor
import com.java.baobaw.interactor.RetryOption
import com.java.baobaw.interactor.withInteractorContext
import com.java.baobaw.networkInfra.SupabaseService

val COMPATIBILITY_BATCH  = "test"

interface CompatibilityBatchInteractor: Interactor {
   suspend fun initCompatibilityBatch()
}
class CompatibilityBatchInteractorImpl(private val supabaseService: SupabaseService, private val seasonInteractor: SeasonInteractor): CompatibilityBatchInteractor {
    override suspend fun initCompatibilityBatch() {
        withInteractorContext(
            retryOption = RetryOption(
            retryCount = 3,
            throwException = false
        )) {
            supabaseService.rpc(COMPATIBILITY_BATCH)
        }
    }
}


