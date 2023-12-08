package com.java.baobaw.feature.chat

import com.java.baobaw.cache.UserMessagesKey
import com.java.baobaw.feature.common.interactor.SeasonInteractor
import com.java.baobaw.interactor.invalidateCache
import com.java.baobaw.networkInfra.SupabaseService
import io.github.jan.supabase.postgrest.query.Count
import io.github.jan.supabase.postgrest.result.PostgrestResult
import io.ktor.http.Headers
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ChatListInteractorImplTest {

    private lateinit var supabaseService: SupabaseService
    private lateinit var seasonInteractor: SeasonInteractor
    private lateinit var chatListInteractor: ChatListInteractor
    private val expectedNumberOfMessages = 2
    private val json = Json { encodeDefaults = true }
    private lateinit var postgrestResult: PostgrestResult
    val mockResponse = List(expectedNumberOfMessages) {
        LastMessageResponse(
            id = it.toLong(),
            referenceId = "ref123",
            userIdOne = "user1",
            userIdTwo = "user2",
            creatorUserId = "user1",
            userIdOneImageUrl = "url1",
            userIdTwoImageUrl = "url2",
            userIdOneName = "User One",
            userIdTwoName = "User Two",
            message = "Message $it",
            isDeleted = false,
            isBlocked = false,
            seen = false,
            createdDate = "2023-01-01",
            messageId = it.toLong()
        )
    }
    @BeforeTest
    fun setUp() {
        supabaseService = mockk(relaxed = true)
        seasonInteractor = mockk(relaxed = true)
        chatListInteractor = ChatListInteractorImpl(supabaseService, seasonInteractor)

        // Define a mock response list of LastMessageResponse
        val mockResponseList = listOf(
            LastMessageResponse(
                id = 1,
                referenceId = "ref123",
                userIdOne = "user1",
                userIdTwo = "user2",
                creatorUserId = "user1",
                userIdOneImageUrl = "imageUrl1",
                userIdTwoImageUrl = "imageUrl2",
                userIdOneName = "User One",
                userIdTwoName = "User Two",
                message = "Message 1",
                isDeleted = false,
                isBlocked = false,
                seen = true,
                createdDate = "2023-01-01T00:00:00Z",
                messageId = 1
            ),
            LastMessageResponse(
                id = 2,
                referenceId = "ref123",
                userIdOne = "user1",
                userIdTwo = "user2",
                creatorUserId = "user2",
                userIdOneImageUrl = "imageUrl1",
                userIdTwoImageUrl = "imageUrl2",
                userIdOneName = "User One",
                userIdTwoName = "User Two",
                message = "Message 2",
                isDeleted = false,
                isBlocked = false,
                seen = false,
                createdDate = "2023-01-01T01:00:00Z",
                messageId = 2
            )
        )
        coEvery { seasonInteractor.getCurrentUserId() } returns "currentUser"
        val fakeLastMessageResponseJsonElement = json.encodeToJsonElement(ListSerializer(LastMessageResponse.serializer()), mockResponse)

        postgrestResult = PostgrestResult(
            body = fakeLastMessageResponseJsonElement,
            headers = Headers.Empty,
            postgrest = mockk(relaxed = true)
        )

        List(expectedNumberOfMessages) {
            LastMessageResponse(
                id = it.toLong(),
                referenceId = "ref123",
                userIdOne = "user1",
                userIdTwo = "user2",
                creatorUserId = "user1",
                userIdOneImageUrl = "url1",
                userIdTwoImageUrl = "url2",
                userIdOneName = "User One",
                userIdTwoName = "User Two",
                message = "Message $it",
                isDeleted = false,
                isBlocked = false,
                seen = false,
                createdDate = "2023-01-01",
                messageId = it.toLong()
            )
        }
    }

    @Test
    fun `getLastMessages fetches and paginates messages correctly`() = runTest {

        coEvery { supabaseService.select(any(), any(), any(), any(), any(), any()) } returns postgrestResult

        val result = chatListInteractor.getLastMessages(0)
        // Assertions to check the pagination and data correctness
        assertEquals(expectedNumberOfMessages, result.data.size)
        assertEquals(0, result.offset)
        // Add more assertions as needed
    }


    @Test
    fun `getLastMessagesTotalCount returns correct count`() = runTest {
        // Mock the postgrestResult to include the count() method
        val postgrestResultMock = mockk<PostgrestResult>()
        coEvery { postgrestResultMock.count() } returns 0L

        // Mock the supabaseService.select method to return the mocked postgrestResult
        coEvery { supabaseService.select(any(), count = Count.EXACT, head = false) } returns postgrestResultMock

        val count = chatListInteractor.getLastMessagesTotalCount()
        assertEquals(0L, count)
    }

    @Test
    fun `jsonElementToLastMessage converts JSON to LastMessage correctly`() = runTest {
        // Define a JSON representation of LastMessageResponse
        val jsonString = """
        {
            "id": 1,
            "reference_id": "ref123",
            "user_id_one": "user1",
            "user_id_two": "user2",
            "creator_user_id": "user1",
            "user_id_one_image_url": "imageUrl1",
            "user_id_two_image_url": "imageUrl2",
            "user_id_one_name": "User One",
            "user_id_two_name": "User Two",
            "message": "Test Message",
            "is_deleted": false,
            "is_blocked": false,
            "seen": true,
            "created_date": "2023-01-01T00:00:00Z",
            "message_id": 1
        }
    """.trimIndent()

        // Define the expected LastMessage object
        val expectedLastMessage = LastMessage(
            id = 1,
            referenceId = "ref123",
            creatorUserId = "user1",
            imageUrl = "imageUrl2",  // Assuming the current user is 'user1'
            name = "User Two",      // The name of the other user
            message = "Test Message",
            isDeleted = false,
            seen = true,
            createdDate = "2023-01-01T00:00:00Z",
            messageId = 1
        )

        // Mock the getCurrentUserId method to return "user1"
        coEvery { seasonInteractor.getCurrentUserId() } returns "user1"

        // Execute the test
        val result = chatListInteractor.jsonElementToLastMessage(jsonString)

        // Assertions
        assertTrue(result is JsonLatMessageResponse.Success)
        assertEquals(expectedLastMessage, (result as JsonLatMessageResponse.Success).lastMessage)
    }


    @Test
    fun `updateMessages updates message list correctly`() = runTest {
        // Define a new LastMessage object
        val newMessage = LastMessage(
            id = 3,
            referenceId = "ref123",
            creatorUserId = "user3",
            imageUrl = "imageUrl3",
            name = "User Three",
            message = "New Message",
            isDeleted = false,
            seen = false,
            createdDate = "2023-01-02T00:00:00Z",
            messageId = 3
        )
        coEvery { seasonInteractor.getCurrentUserId() } returns "user1"

        coEvery {
            supabaseService.select(
                any(),
                any(),
                any(),
                any(),
                any(),
                any()
            )
        } returns postgrestResult

        val postgrestResultMock = mockk<PostgrestResult>()
        coEvery { postgrestResultMock.count() } returns 0L

        // Mock the supabaseService.select method to return the mocked postgrestResult
        coEvery { supabaseService.select(any(), count = Count.EXACT, head = false) } returns postgrestResultMock

        // Execute the test
        val updatedList = chatListInteractor.updateMessages(newMessage)

        // Assertions to check if the message list is updated correctly
        assertTrue(updatedList.data.contains(newMessage))
        assertEquals(expectedNumberOfMessages + 1, updatedList.data.size) // Check if the new message was added
    }
}
