package com.java.baobaw.feature.upload.interactor

import com.java.baobaw.AppConstants
import com.java.baobaw.feature.auth.interactor.numberOnly
import com.java.baobaw.feature.auth.interactor.toBoolean
import com.java.baobaw.feature.auth.interactor.toPhoneExist
import com.java.baobaw.interactor.Interactor
import com.java.baobaw.interactor.withInteractorContext
import com.java.baobaw.model.Profile
import com.java.baobaw.util.toCompressByteArray
import dev.icerock.moko.media.Bitmap
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.rpc
import io.github.jan.supabase.storage.storage
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonPrimitive

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
                supabaseClient.storage["Profile"].let { bucket ->
                    supabaseClient.postgrest.let { postgrest ->
                        val imagePath = "$currentUser/$index.png"
                        bucket.upload(
                            imagePath,
                            bitmap.toCompressByteArray(),
                            upsert = true
                        )
                        val publicUrl = bucket.publicUrl(imagePath)
                        // function to check if image is valid and does not contain any wrong content
                        // returns true if does not contain any wrong image
                        val result = postgrest.rpc(
                            AppConstants.Queries.IS_IMAGE_APPROPRIATE,
                            mapOf("image_url" to publicUrl)
                        ).body

                        if ((result as JsonElement).jsonPrimitive.content.toBoolean()) {
                            // Retrieve the existing profile data
                            postgrest["profile"].update({ Profile::image_url_one setTo publicUrl }) {
                                Profile::user_id eq currentUser
                            }
                        }
                    }
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