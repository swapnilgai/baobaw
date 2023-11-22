import com.java.baobaw.AppConstants
import com.java.baobaw.feature.common.interactor.SeasonInteractor
import com.java.baobaw.feature.upload.interactor.ImageUploadInteractorImpl
import com.java.baobaw.interactor.InteracroeException
import com.java.baobaw.networkInfra.SupabaseService
import com.java.baobaw.util.BitmapProcessor
import dev.icerock.moko.media.Bitmap
import io.github.jan.supabase.postgrest.result.PostgrestResult
import io.ktor.http.Headers
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlin.test.assertTrue

class ImageUploadInteractorImplTest {

    private lateinit var interactor: ImageUploadInteractorImpl
    private val mockSupabaseService = mockk<SupabaseService>()
    private val mockSeasonInteractor = mockk<SeasonInteractor>()
    private val bitmap = mockk<Bitmap>()
    private val mockBitmapProcessor = mockk<BitmapProcessor>()

    @BeforeTest
    fun setUp() {
        interactor = ImageUploadInteractorImpl(mockSupabaseService, mockSeasonInteractor, mockBitmapProcessor)
    }

    @Test
    fun testSuccessfulImageUpload() = runTest {
        // Arrange
        val index = 0
        val expectedUrl = "http://example.com/image.png"
        val jsonResponse = Json.parseToJsonElement("true")
        val postgrestResult = PostgrestResult(
            body = jsonResponse, // Set the body to the JsonElement
            headers = Headers.Empty,
            postgrest = mockk(relaxed = true)
        )
        val bucketArr = ByteArray(10)
        val currentUser = "user123"
        val imagePath = "$currentUser/$index.png"
        val bucket = "Profile"
        val json = Json.encodeToJsonElement(mapOf("image_url" to expectedUrl)) as JsonObject

        coEvery { mockBitmapProcessor.toCompressByteArray(bitmap) } returns bucketArr
        coEvery { mockSeasonInteractor.getCurrentUserId() } returns currentUser
        coEvery { mockSupabaseService.bucketUpload(bucket, imagePath, bucketArr, true) } returns expectedUrl
        coEvery { mockSupabaseService.bucketPublicUrl(bucket, imagePath) } returns expectedUrl
        coEvery { mockSupabaseService.rpc(AppConstants.Queries.IS_IMAGE_APPROPRIATE, json) } returns postgrestResult
        coEvery { mockSupabaseService.tableUpdate(any(), any(), any(), any(), any()) } returns postgrestResult

        val result = interactor.imageUpload(bitmap, index)

        // Assert
        assertEquals(expectedUrl, result)
        coVerify(exactly = 1) { mockSupabaseService.tableUpdate(any(), any(), any(), any(), any()) }
    }

    @Test
    fun testFailureInImageUpload() = runTest {
        // Arrange
        val index = 0
        coEvery { mockSeasonInteractor.getCurrentUserId() } returns "user123"
        coEvery { mockSupabaseService.bucketUpload(any(), any(), any()) } throws RuntimeException()

        // Act & Assert
        assertFailsWith<InteracroeException.Generic> {
            interactor.imageUpload(bitmap, index)
        }
    }

    @Test
    fun testImageAppropriatenessCheckFails() = runTest {
        // Arrange
        val index = 0
        val expectedUrl = "http://example.com/image.png"
        val jsonResponse = Json.parseToJsonElement("false")
        val postgrestResult = PostgrestResult(
            body = jsonResponse, // Set the body to the JsonElement
            headers = Headers.Empty,
            postgrest = mockk(relaxed = true)
        )
        val bucketArr = ByteArray(10)
        val currentUser = "user123"
        val imagePath = "$currentUser/$index.png"
        val bucket = "Profile"
        val json = Json.encodeToJsonElement(mapOf("image_url" to expectedUrl)) as JsonObject

        coEvery { mockBitmapProcessor.toCompressByteArray(bitmap) } returns bucketArr
        coEvery { mockSeasonInteractor.getCurrentUserId() } returns currentUser
        coEvery { mockSupabaseService.bucketUpload(bucket, imagePath, bucketArr, true) } returns expectedUrl
        coEvery { mockSupabaseService.bucketPublicUrl(bucket, imagePath) } returns expectedUrl
        coEvery { mockSupabaseService.rpc(AppConstants.Queries.IS_IMAGE_APPROPRIATE, json) } returns postgrestResult
        coEvery { mockSupabaseService.tableUpdate(any(), any(), any(), any(), any()) } returns postgrestResult
        // Act
        interactor.imageUpload(bitmap, index)

        // Assert
        coVerify(exactly = 0) { mockSupabaseService.tableUpdate(any(), any(), any(), any(), any()) }
    }


    @Test
    fun testNetworkErrorDuringRpcCall() = runTest {
        // Arrange
        val index = 0
        coEvery { mockBitmapProcessor.toCompressByteArray(bitmap) } returns ByteArray(10)
        coEvery { mockSeasonInteractor.getCurrentUserId() } returns "user123"
        coEvery { mockSupabaseService.bucketUpload(any(), any(), any()) } returns "http://example.com/image.png"
        coEvery { mockSupabaseService.bucketPublicUrl(any(), any()) } returns "http://example.com/image.png"
        coEvery { mockSupabaseService.rpc(any(), any()) } throws RuntimeException("Network error")

        // Act & Assert
        assertFailsWith<InteracroeException.Generic> {
            interactor.imageUpload(bitmap, index)
        }
    }

    @Test
    fun testInvalidUserId() = runTest {
        // Arrange
        val index = 0
        coEvery { mockSeasonInteractor.getCurrentUserId() } returns null

        // Act & Assert
        val result = interactor.imageUpload(bitmap, index)

        // Assert
        assertTrue(result.isEmpty())
        coVerify(exactly = 0) { mockSupabaseService.bucketUpload(any(), any(), any()) }
    }
//
//
//    @Test
//    fun testSuccessfulImageDeletion() = runTest {
//        // Arrange
//        val index = 0
//        coEvery { mockSeasonInteractor.getCurrentUserId() } returns "user123"
//        coEvery { mockSupabaseService.bucketDelete(any()) } just Runs
//
//        // Act
//        interactor.deleteImage(index)
//
//        // Assert
//        coVerify { mockSupabaseService.bucketDelete(any()) }
//    }
//
//    @Test
//    fun testFailureInImageDeletion() = runTest {
//        // Arrange
//        val index = 0
//        coEvery { mockSeasonInteractor.getCurrentUserId() } returns "user123"
//        coEvery { mockSupabaseService.bucketDelete(any()) } throws RuntimeException()
//    }
}
