package com.java.baobaw.feature.chatt_detail

import com.java.baobaw.feature.common.interactor.SeasonInteractor
import com.java.baobaw.interactor.Interactor
import com.java.baobaw.interactor.withInteractorContext
import com.java.baobaw.networkInfra.SupabaseService
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.RealtimeChannel
import io.github.jan.supabase.realtime.postgresChangeFlow
import io.github.jan.supabase.realtime.realtime
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.Json

interface ChatInteractor : Interactor {
    suspend fun getMessages(referenceId: String, minRange: Long = 0, maxRange: Long = 20): List<ChatMessage>
    suspend fun getMessagesStream(referenceId: String): Flow<PostgresAction>

    suspend fun joinMessageStream()

    suspend fun jsonElementToChatMessage(jsonString: String): ChatMessage
}
class ChatInteractorImpl(private val supabaseService: SupabaseService, private val seasonInteractor: SeasonInteractor,
                         private val realtimeChannel: RealtimeChannel, val supabaseClient: SupabaseClient) : ChatInteractor {

    override suspend fun getMessages(referenceId: String, minRange: Long, maxRange: Long): List<ChatMessage> = withInteractorContext {
        val messages : List<ChatMessage> = supabaseService.select("messages") {
            eq("reference_id", referenceId)
            range(minRange, maxRange)
            order("created_date", Order.ASCENDING)
        }.decodeList()

        // Set isUserCreated property
        val currentUserId = seasonInteractor.getCurrentUserId()
        messages.map { it.copy(isUserCreated = it.creatorUserId == currentUserId) }
   }

    override suspend fun getMessagesStream(referenceId: String): Flow<PostgresAction> {
        val changeFlow = realtimeChannel.postgresChangeFlow<PostgresAction>(schema = "public") {
            table = "messages"
            filter = "reference_id=eq.$referenceId"
        }
        return changeFlow
    }

    override suspend fun joinMessageStream() {
        supabaseClient.realtime.connect()
        realtimeChannel.join()
    }

    override suspend fun jsonElementToChatMessage(jsonString: String): ChatMessage = withInteractorContext {
            Json.decodeFromString<ChatMessage>(jsonString)
        }
}



//
//fun getConversation(referenceId: String) {
//
//    val channel = supabaseClient.realtime.createChannel(referenceId)
//
//    val changeFlow = channel.postgresChangeFlow<PostgresAction>(schema = "public") {
//        table = "messages"
//        filter = "reference_id=eq.$referenceId"
//    }
//
//    viewModelScope.launch {
//        changeFlow.onEach { action ->
//            when (action) {
//                is PostgresAction.Delete -> println("******************Deleted: ${action.oldRecord}")
//                is PostgresAction.Insert -> println("******************Inserted: ${action.record}")
//                is PostgresAction.Select -> println("******************Selected: ${action.record}")
//                is PostgresAction.Update -> println("******************Updated: ${action.oldRecord} with ${action.record}")
//            }
//        }.launchIn(this)
//
//        supabaseClient.realtime.connect()
//        channel.join()
//    }
//}
//
//fun subscribeToMessagesForCurrentUser() {
//    viewModelScope.launch {
//        val city = async { supabaseClient.postgrest["last_message"].select().body }
//        val res = city.await()
//
//        val currentUser =  supabaseClient.gotrue.currentUserOrNull()
//        val channel = supabaseClient.realtime.createChannel(currentUser?.id ?: "messages")
//        val changeFlow = channel.postgresChangeFlow<PostgresAction>(schema = "public") {
//            table = "last_message"
//        }
//
//
//        changeFlow.onEach { action ->
//            when (action) {
//                is PostgresAction.Delete -> println("******************Last_Deleted: ${action.oldRecord}")
//                is PostgresAction.Insert -> println("******************Last_Inserted: ${action.record}")
//                is PostgresAction.Select -> println("******************Last_Selected: ${action.record}")
//                is PostgresAction.Update -> println("******************Last_Updated: ${action.oldRecord} with ${action.record}")
//            }
//        }.launchIn(this)
//
//        supabaseClient.realtime.connect()
//        channel.join()
//    }
//}
//((auth.uid() = user_id_one) OR (auth.uid() = user_id_two))

//    fun subscribeToMessagesForCurrentUser(currentUserId: String) {
//        val channel = supabaseClient.realtime.createChannel("messages") {
//            // Optional config
//        }
//
//        val changeFlow = channel.postgresChangeFlow<PostgresAction>(schema = "public") {
//            table = "messages"
//            // No server-side filter for user_ids, filter will be done client-side
//        }
//
//        viewModelScope.launch {
//            changeFlow.collect { action ->
//                when (action) {
//                    is PostgresAction.Insert, is PostgresAction.Update -> {
//                        val message = action.record as? Message
//                        if (message != null && currentUserId in message.user_ids) {
//                            println("Relevant Message: ${message.last_message}")
//                        }
//                    }
//                    // Other actions can be handled as needed
//                }
//            }
//            supabaseClient.realtime.connect()
//            channel.join()
//        }
//    }

