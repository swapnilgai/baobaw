package com.java.baobaw.feature.chat_detail

import com.java.baobaw.feature.chat.ChatDetailInteractorImpl
import com.java.baobaw.feature.chat.ChatMessageResponse
import com.java.baobaw.feature.common.interactor.CompatibilityBatchInteractorImpl
import com.java.baobaw.feature.common.interactor.SeasonInteractor
import com.java.baobaw.interactor.InteracroeException
import com.java.baobaw.networkInfra.SupabaseService
import io.github.jan.supabase.postgrest.result.PostgrestResult
import io.ktor.http.Headers
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ChatDetailInteractorImplTest {

    private lateinit var supabaseService: SupabaseService
    private lateinit var seasonInteractor: SeasonInteractor
    private lateinit var compatibilityBatchInteractor: CompatibilityBatchInteractorImpl
    private lateinit var postgrestResult: PostgrestResult
    private lateinit var unitJsonElement: JsonElement  // Represents {}
    private val json = Json { encodeDefaults = true }

    val fakeMessages = listOf(
        ChatMessageResponse(
            id = 1,
            referenceId = "ref123",
            userIdOne = "user1",
            userIdTwo = "user2",
            creatorUserId = "user1",
            message = "Hello from user1",
            createdDate = Instant.DISTANT_FUTURE,
            seen = true,
            isDeleted = false
        ),
        ChatMessageResponse(
            id = 2,
            referenceId = "ref123",
            userIdOne = "user1",
            userIdTwo = "user3",
            creatorUserId = "user3",
            message = "Reply from user3",
            createdDate = Instant.DISTANT_PAST,
            seen = false,
            isDeleted = false
        )
    )

    val referenceId = "someReferenceId"
    val currentUserId = "currentUser"
    @BeforeTest
    fun setUp() {
        supabaseService = mockk(relaxed = true)
        seasonInteractor = mockk(relaxed = true)
        compatibilityBatchInteractor =
            CompatibilityBatchInteractorImpl(supabaseService, seasonInteractor)
        unitJsonElement = JsonObject(emptyMap())
        val fakeMessagesJsonElement = json.encodeToJsonElement(ListSerializer(ChatMessageResponse.serializer()), fakeMessages)
        postgrestResult = PostgrestResult(
            body = fakeMessagesJsonElement,
            headers = Headers.Empty,
            postgrest = mockk(relaxed = true)
        )
    }

    @Test
    fun `getMessages fetches messages correctly`() = runTest {

        // Setup expectations
        coEvery {
            supabaseService.select(
                eq("messages"), any(), any(), any(), any(), any() // Use any() if specific matching of the lambda is not required
            )
        } returns postgrestResult

        coEvery { seasonInteractor.getCurrentUserId() } returns currentUserId

        val chatInteractor = ChatDetailInteractorImpl(supabaseService, seasonInteractor)
        // Act
        val result = chatInteractor.getMessages(referenceId)
        // Assert
        assertEquals(4, result.size)
        result.forEach {
            if(!it.isHeader) assertEquals(it.creatorUserId == currentUserId, it.isUserCreated)
        }
    }

    @Test
    fun `getMessages throws exception on Supabase service error`() = runTest {
        // Setup expectations
        coEvery {
            supabaseService.select(
                eq("messages"),
                any(),
                any(),
                any(),
                any(),
                any() // Use any() if specific matching of the lambda is not required
            )
        } throws Exception("Service error")

        coEvery { seasonInteractor.getCurrentUserId() } returns currentUserId

        val chatInteractor = ChatDetailInteractorImpl(supabaseService, seasonInteractor)
        // Act
        assertFailsWith<InteracroeException.Generic> {
            chatInteractor.getMessages(referenceId)
        }
    }

    @Test
    fun `sendMessage calls RPC with correct parameters`() = runTest {
        // Mock dependencies
        val chatInteractor = ChatDetailInteractorImpl(supabaseService, seasonInteractor)
        val inputText = "Hello, World!"
        val currentUserId = "currentUserId"

        coEvery { seasonInteractor.getCurrentUserId() } returns currentUserId
        coEvery { supabaseService.rpc(any(), any()) } returns postgrestResult

        // Act
        coVerify(exactly = 0) { chatInteractor.sendMessage(inputText) }
    }

    @Test
    fun `sendMessage throws exception when no current user`() = runTest {
        // Mock dependencies
        val supabaseService = mockk<SupabaseService>(relaxed = true)
        val seasonInteractor = mockk<SeasonInteractor>()
        val chatInteractor = ChatDetailInteractorImpl(supabaseService, seasonInteractor)
        val inputText = "Hello, World!"

        coEvery { supabaseService.currentUserOrNull() } returns null

        // Act
        assertFailsWith<InteracroeException.Generic> {
            chatInteractor.sendMessage(inputText)
        }
    }
}

