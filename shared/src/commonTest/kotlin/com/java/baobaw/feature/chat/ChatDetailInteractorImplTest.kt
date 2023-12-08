package com.java.baobaw.feature.chat

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
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.serialization.json.encodeToJsonElement
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ChatDetailInteractorImplTest {

//    private lateinit var supabaseService: SupabaseService
//    private lateinit var seasonInteractor: SeasonInteractor
//    private lateinit var chatInteractor: ChatDetailInteractorImpl
//    private lateinit var postgrestResultPast: PostgrestResult
//    private val json = Json { encodeDefaults = true }
//    private lateinit var fakeMessagesPast : List<ChatMessageResponse>
//    private lateinit var fakeMessagesCurrent : List<ChatMessageResponse>
//    private lateinit var postgrestResultCurrent: PostgrestResult

    private lateinit var supabaseService: SupabaseService
    private lateinit var seasonInteractor: SeasonInteractor
    private lateinit var chatInteractor: ChatDetailInteractorImpl
    private lateinit var postgrestResultPast: PostgrestResult
    private val json = Json { encodeDefaults = true }
    private lateinit var fakeMessagesPast : List<ChatMessageResponse>
    private lateinit var fakeMessagesCurrent : List<ChatMessageResponse>
    private lateinit var postgrestResultCurrent: PostgrestResult

    @BeforeTest
    fun setUp() {
        supabaseService = mockk(relaxed = true)
        seasonInteractor = mockk(relaxed = true)
        chatInteractor = ChatDetailInteractorImpl(supabaseService, seasonInteractor)
        coEvery { seasonInteractor.getCurrentUserId() } returns "currentUser"

        fakeMessagesPast = listOf(
            ChatMessageResponse(
                id = 1,
                referenceId = "ref123",
                userIdOne = "user1",
                userIdTwo = "user2",
                creatorUserId = "user1",
                message = "Hello from user1",
                createdDate = Instant.DISTANT_PAST,
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

        fakeMessagesCurrent = listOf(
            ChatMessageResponse(
                id = 1,
                referenceId = "ref123",
                userIdOne = "user1",
                userIdTwo = "user2",
                creatorUserId = "user1",
                message = "Hello from user1",
                createdDate = Clock.System.now(),
                seen = true,
                isDeleted = false
            )
        )

        val fakeMessagesPastJsonElement = json.encodeToJsonElement(ListSerializer(ChatMessageResponse.serializer()), fakeMessagesPast)
        val fakeMessagesCurrentJsonElement = json.encodeToJsonElement(ListSerializer(ChatMessageResponse.serializer()), fakeMessagesCurrent)

        postgrestResultPast = PostgrestResult(
            body = fakeMessagesPastJsonElement,
            headers = Headers.Empty,
            postgrest = mockk(relaxed = true)
        )

        postgrestResultCurrent = PostgrestResult(
            body = fakeMessagesCurrentJsonElement,
            headers = Headers.Empty,
            postgrest = mockk(relaxed = true)
        )
    }


    @Test
    fun `getMessages fetches messages correctly`() = runTest {
        val dateRef = Instant.DISTANT_PAST.toChatHeaderReadableDate()
        val pagedResponse = PagedResponse(
            data = mapOf(dateRef to fakeMessagesPast.map { it.toChatMessage("currentUser") }),
            totalLoadedRecords = fakeMessagesPast.size,
            isEnded = false
        )

        coEvery { supabaseService.select(any(), any(), any(), any(), any(), any()) } returns PostgrestResult(
            body = Json.encodeToJsonElement(pagedResponse),
            headers = Headers.Empty,
            postgrest = mockk(relaxed = true)
        )

        val result = chatInteractor.getMessages("ref123")
        assertEquals(1, result.data.size)
        assertEquals("Hello from user1", result.data[dateRef]?.first()?.message)
        assertEquals(fakeMessagesPast.size, result.totalLoadedRecords)
        assertFalse(result.isEnded)
    }
    @Test
    fun `getMessages with invalid referenceId returns empty data`() = runTest {
        // Mocking the return value to an empty PagedResponse
        coEvery { supabaseService.select(any(), any(), any(), any(), any(), any()) } returns PostgrestResult(
            body = Json.encodeToJsonElement(PagedResponse()),
            headers = Headers.Empty,
            postgrest = mockk(relaxed = true)
        )

        val result = chatInteractor.getMessages("invalidRef")
        assertTrue(result.data.isEmpty())
        assertEquals(0, result.totalLoadedRecords)
        assertTrue(result.isEnded)
    }


    @Test
    fun `sendMessage calls RPC with correct parameters`() = runTest {
        val chatMessageRequest = ChatMessageRequest(
            creatorUserId = "user1",
            otherUserId = "user2",
            message = "Hello"
        )
        coEvery { supabaseService.rpc(any(), any()) } returns postgrestResultPast

        chatInteractor.sendMessage(chatMessageRequest)

        coVerify { supabaseService.rpc("insert_message", any()) }
    }

    @Test
    fun `sendMessage with invalid request does not call RPC`() = runTest {
        val invalidRequest = ChatMessageRequest("", "", null)

        chatInteractor.sendMessage(invalidRequest)

        coVerify(exactly = 0) { supabaseService.rpc("insert_message", any()) }
    }

    @Test
    fun `sendMessage handles service exceptions correctly`() = runTest {
        val request = ChatMessageRequest("user1", "user2", "Hello")
        coEvery { supabaseService.rpc(any(), any()) } throws RuntimeException("Service error")

        assertFailsWith<InteracroeException.Generic> {
            chatInteractor.sendMessage(request)
        }
    }

    @Test
    fun `jsonElementToChatMessage converts correctly`() = runBlocking {
        val jsonString = json.encodeToString(ChatMessageResponse.serializer(), fakeMessagesPast[0])

        val expectedChatMessage = fakeMessagesPast[0].toChatMessage("currentUser")

        val chatMessage = chatInteractor.jsonElementToChatMessage(jsonString)
        assertEquals(expectedChatMessage, chatMessage)
    }

    @Test
    fun `jsonElementToChatMessage with invalid JSON throws error`() = runTest {
        val invalidJsonString = "invalidJson"

        assertFailsWith<InteracroeException> {
            chatInteractor.jsonElementToChatMessage(invalidJsonString)
        }
    }

    @Test
    fun `getChatMessageRequest creates request correctly`() = runBlocking {
        val inputText = "Hello, World!"
        val referenceId = "currentUser:user2"
        val request = chatInteractor.getChatMessageRequest(inputText, referenceId)
        assertEquals("currentUser", request.creatorUserId)
        assertEquals("user2", request.otherUserId)
        assertEquals(inputText, request.message)
    }

    @Test
    fun `getChatMessageRequest with invalid referenceId throws error`() = runTest {
        val inputText = "Hello, World!"
        val invalidReferenceId = "invalid"

        assertFailsWith<InteracroeException> {
            chatInteractor.getChatMessageRequest(inputText, invalidReferenceId)
        }
    }

    @Test
    fun `addTempMessage adds temporary message correctly`() = runBlocking {
        val request = ChatMessageRequest("user1", "user2", "Temporary message")
        val referenceId = fakeMessagesCurrent[0].createdDate.toChatHeaderReadableDate()
        val tempChatMessage = request.toChatMessage("user1", referenceId)
        val pagedResponse = PagedResponse(mapOf(referenceId to listOf(tempChatMessage)), 1, false)

        coEvery { supabaseService.select(any(), any(), any(), any(), any(), any()) } returns postgrestResultCurrent

        val updatedPagedResponse = chatInteractor.addTempMessage(request, referenceId)
        assertTrue(updatedPagedResponse.data[referenceId]!!.contains(tempChatMessage))
    }

    @Test
    fun `addTempMessage should add message when new date wanted to add`() = runBlocking {
        val request = ChatMessageRequest("user1", "user2", "Temporary message")
        val referenceId = Clock.System.now().toChatHeaderReadableDate()
        val tempChatMessage = request.toChatMessage("user1", referenceId)
        val pagedResponse = PagedResponse(mapOf(referenceId to listOf(tempChatMessage)), 1, false)

        coEvery { supabaseService.select(any(), any(), any(), any(), any(), any()) } returns postgrestResultPast

        val updatedPagedResponse = chatInteractor.addTempMessage(request, referenceId)
        assertTrue(updatedPagedResponse.data[referenceId]?.contains(tempChatMessage) ?: false)
    }

    @Test
    fun `addTempMessage handles exceptions correctly`() = runTest {
        val request = ChatMessageRequest("user1", "user2", "Temporary message")
        val referenceId = "testRefId"

        coEvery { supabaseService.select(any(), any(), any(), any(), any(), any()) }throws RuntimeException("Test exception")

        assertFailsWith<InteracroeException.Generic> {
            chatInteractor.addTempMessage(request, referenceId)
        }
    }
}

