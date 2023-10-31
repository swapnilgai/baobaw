package com.java.baobaw.feature.common.interactor

import com.java.baobaw.interactor.Interactor
import com.java.baobaw.interactor.RetryOption
import com.java.baobaw.interactor.withInteractorContext
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.rpc


val COMPATIBILITY_BATCH  = "phone_exists"

interface CompatibilityBatchInteractor: Interactor {
   suspend fun initCompatibilityBatch()
}
class CompatibilityBatchInteractorImpl(private val supabaseClient: SupabaseClient, private val seasonInteractor: SeasonInteractor): CompatibilityBatchInteractor {
    override suspend fun initCompatibilityBatch() {
        return withInteractorContext(retryOption = RetryOption(
            retryCount = 3,
            throwException = false
        )) {
            supabaseClient.postgrest.rpc(COMPATIBILITY_BATCH)
        }
    }
}


