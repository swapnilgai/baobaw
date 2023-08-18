package com.java.cherrypick.feature.location.interactor

import com.java.cherrypick.interactor.Interactor
import com.java.cherrypick.interactor.withInteractorContext
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.postgrest.postgrest

interface LocationInteractor {
    suspend fun updateLocation(lat: String, lan: String)
}

class LocationInteractorImpl(private val supabaseClient: SupabaseClient): LocationInteractor, Interactor {
    override suspend fun updateLocation(lat: String, lan: String) {
        withInteractorContext {
            val point = Point(lat, lan)
            supabaseClient.gotrue.currentUserOrNull()?.id?.let {
                val result =  supabaseClient.postgrest["location"].insert(
                    Location(
                        userId = it,
                        location = point.toString(),
                        allowSearch = true
                    )
                )
            }
        }
    }
}

