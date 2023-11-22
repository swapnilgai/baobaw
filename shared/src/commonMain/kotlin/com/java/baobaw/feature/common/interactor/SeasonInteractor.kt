package com.java.baobaw.feature.common.interactor

import com.java.baobaw.cache.AuthSessionCacheKey
import com.java.baobaw.cache.CurrentUserCacheKey
import com.java.baobaw.interactor.CacheOption
import com.java.baobaw.interactor.Interactor
import com.java.baobaw.interactor.withInteractorContext
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.gotrue.user.UserSession

interface SeasonInteractor: Interactor {
    suspend fun getCurrentSession() : UserSession?

    suspend fun getCurrentUserId(): String?
}

class SeasonInteractorImpl(private val supabaseClient: SupabaseClient): SeasonInteractor {
    override suspend fun getCurrentSession() : UserSession? {
        return withInteractorContext(cacheOption = CacheOption(key = AuthSessionCacheKey())) {
            supabaseClient.gotrue.currentSessionOrNull()
        }
    }

    override suspend fun getCurrentUserId(): String? {
        return withInteractorContext {
            supabaseClient.gotrue.currentUserOrNull()?.id
        }
    }
}