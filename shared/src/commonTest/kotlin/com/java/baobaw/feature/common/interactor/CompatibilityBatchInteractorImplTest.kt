import com.java.baobaw.feature.common.interactor.COMPATIBILITY_BATCH
import com.java.baobaw.feature.common.interactor.CompatibilityBatchInteractorImpl
import com.java.baobaw.feature.common.interactor.SeasonInteractor
import com.java.baobaw.networkInfra.SupabaseService
import io.github.jan.supabase.postgrest.result.PostgrestResult
import io.ktor.http.Headers
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class CompatibilityBatchInteractorImplTest {

    private lateinit var supabaseService: SupabaseService
    private lateinit var seasonInteractor: SeasonInteractor
    private lateinit var compatibilityBatchInteractor: CompatibilityBatchInteractorImpl
    private lateinit var  postgrestResult : PostgrestResult
    private lateinit var unitJsonElement: JsonElement  // Represents {}

    @BeforeTest
    fun setUp() {
        supabaseService = mockk(relaxed = true)
        seasonInteractor = mockk(relaxed = true)
        compatibilityBatchInteractor = CompatibilityBatchInteractorImpl(supabaseService, seasonInteractor)
        unitJsonElement = JsonObject(emptyMap())
        postgrestResult = PostgrestResult(
            body = unitJsonElement, // Set the body to the JsonElement
            headers = Headers.Empty,
            postgrest = mockk(relaxed = true)
        )
    }

    @Test
    fun `initCompatibilityBatch should successfully call SupabaseService and return Unit`() = runTest {
        coEvery { supabaseService.rpc(COMPATIBILITY_BATCH) } returns postgrestResult

        val result = compatibilityBatchInteractor.initCompatibilityBatch()

        coVerify(exactly = 1) { supabaseService.rpc(COMPATIBILITY_BATCH) }
        assertTrue { result is Unit } // Explicitly check that result is Unit
    }

    @Test
    fun `initCompatibilityBatch should catch specific exception when throwException is false`() = runTest {
        // Simulate a failure in the supabaseService.rpc call
        coEvery { supabaseService.rpc(COMPATIBILITY_BATCH) } throws RuntimeException("Simulated service failure")

        val exception = assertFailsWith<IllegalStateException> {
            // Call initCompatibilityBatch. Expecting it to throw an IllegalStateException
            compatibilityBatchInteractor.initCompatibilityBatch()
        }

        // Verify that the exception message is as expected
        assertEquals("State is not valid", exception.message)

        // Verify that the rpc method was called the expected number of times (retry logic)
        coVerify(exactly = 4) { supabaseService.rpc(COMPATIBILITY_BATCH) }
    }

    @Test
    fun `initCompatibilityBatch should retry on failure`() = runTest {
        var attempts = 0
        coEvery { supabaseService.rpc(COMPATIBILITY_BATCH) } answers {
            if (++attempts < 3) throw RuntimeException("Temporary failure")
            postgrestResult
        }

        compatibilityBatchInteractor.initCompatibilityBatch()

        assertEquals(3, attempts)
    }

}
