package com.java.baobaw.feature.upload.interactor

import com.java.baobaw.AppConstants
import com.java.baobaw.feature.auth.interactor.toBoolean
import com.java.baobaw.feature.common.interactor.SeasonInteractor
import com.java.baobaw.interactor.Interactor
import com.java.baobaw.interactor.withInteractorContext
import com.java.baobaw.model.Profile
import com.java.baobaw.networkInfra.SupabaseService
import com.java.baobaw.util.BitmapProcessor
import dev.icerock.moko.media.Bitmap
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonPrimitive
interface ImageUploadInteractor {
    suspend fun imageUpload(bitmap: Bitmap, index: Int): String

    suspend fun deleteImage(index: Int)
}

//https://github.com/hlnstepanova/kmpizza-repo/blob/448e5f78c89863c843ea3d62fd8e8ea5b674a90c/shared/src/commonMain/kotlin/dev/tutorial/kmpizza/viewmodel/RecipeDetailsViewModel.kt
//https://github.com/icerockdev/moko-media

class ImageUploadInteractorImpl(private val supabaseService: SupabaseService, private val seasonInteractor: SeasonInteractor, val bitmapProcessor: BitmapProcessor): ImageUploadInteractor, Interactor{
    override suspend fun imageUpload(bitmap: Bitmap, index: Int): String {
       return withInteractorContext {
           seasonInteractor.getCurrentUserId()?.let { currentUser ->
               val imagePath = "$currentUser/$index.png"
               val bucket = "Profile"
               supabaseService.bucketUpload(
                   bucket,
                   imagePath,
                   bitmapProcessor.toCompressByteArray(bitmap),
                   upsert = true
               )
               val publicUrl = supabaseService.bucketPublicUrl(bucket, imagePath)

               val result = supabaseService.rpc(
                   AppConstants.Queries.IS_IMAGE_APPROPRIATE,
                   kotlinx.serialization.json.Json.encodeToJsonElement(mapOf("image_url" to publicUrl)) as JsonObject
               ).body

               if ((result as JsonElement).jsonPrimitive.content.toBoolean()) {
                   // Retrieve the existing profile data
                   supabaseService.tableUpdate(
                       "profile",
                       update =  { Profile::image_url_one setTo publicUrl }
                   ) { Profile::user_id eq currentUser }
               }
               return@withInteractorContext publicUrl
           }
           return@withInteractorContext ""
       }
    }


//    io.github.jan.supabase.exceptions.NotFoundRestException: Unknown error
//    URL: https://cherrypick-int-main-1c9d4e6.d2.zuplo.dev/rest/v1/profile
//    Headers: [Authorization=[Bearer eyJhbGciOiJIUzI1NiIsImtpZCI6IlBzMzhyUkJZZzJ5cGN2R2siLCJ0eXAiOiJKV1QifQ.eyJhdWQiOiJhdXRoZW50aWNhdGVkIiwiZXhwIjoxNzAwMTExOTU0LCJpYXQiOjE3MDAxMDgzNTQsImlzcyI6Imh0dHBzOi8vZWFkeGFqdXVkcHlwc2l2ZGhqdHUuc3VwYWJhc2UuY28vYXV0aC92MSIsInN1YiI6Ijc2YzFjMWVmLWVjNDgtNGJjYi05MDgxLWQyYzUyZWRiODY2MSIsImVtYWlsIjoic3dhQHlhaG9vLmNvbSIsInBob25lIjoiIiwiYXBwX21ldGFkYXRhIjp7InByb3ZpZGVyIjoiZW1haWwiLCJwcm92aWRlcnMiOlsiZW1haWwiXX0sInVzZXJfbWV0YWRhdGEiOnt9LCJyb2xlIjoiYXV0aGVudGljYXRlZCIsImFhbCI6ImFhbDEiLCJhbXIiOlt7Im1ldGhvZCI6InBhc3N3b3JkIiwidGltZXN0YW1wIjoxNzAwMTA0NzA5fV0sInNlc3Npb25faWQiOiJjNWM5OTMxZi05YzFlLTRlOTEtOWQ5Zi0yOTExODkwNDk5OTcifQ.OXpmtiWSjU-9XYnB26sHeDaWGgsEB7AYguTB2JFA16s], Prefer=[return=representation], Content-Profile=[public], apikey=[eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InhobHVmY2RpY2NhZHhqcmpqc3ZhIiwicm9sZSI6ImFub24iLCJpYXQiOjE2ODk4MTY3MzMsImV4cCI6MjAwNTM5MjczM30.XdTptbARC1pIBgFn5_XC_OUPnZGoipBPmWSq5I5YH18], X-Client-Info=[supabase-kt/1.4.6], Accept=[application/json], Accept-Charset=[UTF-8]]
//    Http Method: PATCH
    override suspend fun deleteImage(index: Int) {
        return withInteractorContext {
            val currentUser = seasonInteractor.getCurrentUserId()
            supabaseService.bucketDelete("$currentUser/$index.png")
        }
    }
}
