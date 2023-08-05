package com.java.cherrypick.feature.upload.interactor

import com.java.cherrypick.interactor.Interactor
import com.java.cherrypick.interactor.withInteractorContext
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.storage.storage

interface ImageUploadInteractor {
    suspend fun imageUpload(byteArray: ByteArray)
}

//https://github.com/hlnstepanova/kmpizza-repo/blob/448e5f78c89863c843ea3d62fd8e8ea5b674a90c/shared/src/commonMain/kotlin/dev/tutorial/kmpizza/viewmodel/RecipeDetailsViewModel.kt
//https://github.com/icerockdev/moko-media

class ImageUploadInteractorImpl(private val supabaseClient: SupabaseClient): ImageUploadInteractor, Interactor{
    override suspend fun imageUpload(byteArray: ByteArray) {
        withInteractorContext {
            supabaseClient.gotrue.currentUserOrNull()?.id?.let {
                val bucket = supabaseClient.storage["user"]
                bucket.upload("myIcon.png", byteArray, upsert = false)
            }
        }
    }

}