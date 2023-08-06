package com.java.cherrypick.feature.upload.interactor

import com.java.cherrypick.interactor.Interactor
import com.java.cherrypick.interactor.withInteractorContext
import dev.icerock.moko.media.Bitmap
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.exceptions.NotFoundRestException
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.storage.storage

//data class (val bucket_id: String, val user_id: string)

interface ImageUploadInteractor {
    suspend fun imageUpload(bitmap: Bitmap, index: Int)

    suspend fun deleteImage(index: Int)
}

//https://github.com/hlnstepanova/kmpizza-repo/blob/448e5f78c89863c843ea3d62fd8e8ea5b674a90c/shared/src/commonMain/kotlin/dev/tutorial/kmpizza/viewmodel/RecipeDetailsViewModel.kt
//https://github.com/icerockdev/moko-media

class ImageUploadInteractorImpl(private val supabaseClient: SupabaseClient): ImageUploadInteractor, Interactor{
    override suspend fun imageUpload(bitmap: Bitmap, index: Int) {
       return withInteractorContext {
            supabaseClient.gotrue.currentUserOrNull()?.id?.let { currentUser ->
                try {
                    supabaseClient.storage.retrieveBucketById(currentUser)
                } catch (e: NotFoundRestException){
                    supabaseClient.storage.createBucket(currentUser)
                } finally {
                    supabaseClient.storage[currentUser].upload("$index.png", bitmap.toByteArray(), upsert = true)
                    }
            }
        }
    }

    override suspend fun deleteImage(index: Int) {
        return withInteractorContext {
            supabaseClient.gotrue.currentUserOrNull()?.id?.let { currentUser ->
                supabaseClient.storage[currentUser].delete("$index.png")
            }
        }
    }

}