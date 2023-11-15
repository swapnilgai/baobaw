package com.java.baobaw.feature.upload.interactor

import com.java.baobaw.AppConstants
import com.java.baobaw.feature.auth.interactor.toBoolean
import com.java.baobaw.feature.common.interactor.SeasonInteractor
import com.java.baobaw.interactor.Interactor
import com.java.baobaw.interactor.RetryOption
import com.java.baobaw.interactor.withInteractorContext
import com.java.baobaw.model.Profile
import com.java.baobaw.networkInfra.SupabaseService
import com.java.baobaw.util.toCompressByteArray
import dev.icerock.moko.media.Bitmap
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.rpc
import io.github.jan.supabase.storage.storage
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonPrimitive

//data class (val bucket_id: String, val user_id: string)

interface ImageUploadInteractor {
    suspend fun imageUpload(bitmap: Bitmap, index: Int): String

    suspend fun deleteImage(index: Int)
}

//https://github.com/hlnstepanova/kmpizza-repo/blob/448e5f78c89863c843ea3d62fd8e8ea5b674a90c/shared/src/commonMain/kotlin/dev/tutorial/kmpizza/viewmodel/RecipeDetailsViewModel.kt
//https://github.com/icerockdev/moko-media

class ImageUploadInteractorImpl(private val supabaseService: SupabaseService, private val seasonInteractor: SeasonInteractor): ImageUploadInteractor, Interactor{
    override suspend fun imageUpload(bitmap: Bitmap, index: Int): String {
       return  withInteractorContext {
           val currentUser = seasonInteractor.getCurrentUserId()
           val imagePath = "$currentUser/$index.png"
           supabaseService.bucketUpload(
               imagePath,
               bitmap.toCompressByteArray(),
               upsert = true
           )
           val publicUrl = supabaseService.bucketPublicUrl(imagePath)

           supabaseService.tableUpdate("profile", { Profile::image_url_one setTo publicUrl }) {
               Profile::user_id eq currentUser
           }

           val result = supabaseService.rpc(
               AppConstants.Queries.IS_IMAGE_APPROPRIATE,
               mapOf("image_url" to publicUrl)
           ).body

           if ((result as JsonElement).jsonPrimitive.content.toBoolean()) {
               // Retrieve the existing profile data
               supabaseService.tableUpdate("profile", { Profile::image_url_one setTo publicUrl }) {
                   Profile::user_id eq currentUser
               }
           }
           return@withInteractorContext publicUrl
       }
    }

    override suspend fun deleteImage(index: Int) {
        return withInteractorContext {
            val currentUser = seasonInteractor.getCurrentUserId()
            supabaseService.bucketDelete("$currentUser/$index.png")
        }
    }
}
