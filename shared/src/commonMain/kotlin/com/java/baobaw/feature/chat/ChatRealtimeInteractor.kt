package com.java.baobaw.feature.chat

import com.java.baobaw.feature.common.interactor.SeasonInteractor
import com.java.baobaw.interactor.Interactor
import com.java.baobaw.interactor.RetryOption
import com.java.baobaw.interactor.withInteractorContext
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.RealtimeChannel
import io.github.jan.supabase.realtime.createChannel
import io.github.jan.supabase.realtime.postgresChangeFlow
import io.github.jan.supabase.realtime.realtime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

enum class ChatListType {
    NOTIFICATION,
    LIST_MESSAGES,
    DETAIL_MESSAGES
}
interface ChatRealtimeInteractor: Interactor {
    suspend fun getFlowStream(tableName: String, filterString: String?, chatListType: ChatListType): Flow<PostgresAction>
    suspend fun unSubscribe(chatListType: ChatListType)
    suspend fun subscribe(chatListType: ChatListType)
    suspend fun subscribeToLastMessages(chatListType: ChatListType): Flow<LastMessage>
    suspend fun connect()
    suspend fun disconnect()
}
class ChatRealtimeInteractorImpl(
    private val supabaseClient: SupabaseClient,
    private val seasonInteractor: SeasonInteractor,
    private val chatListInteractor: ChatListInteractor
    ) : ChatRealtimeInteractor {
        private var realtimeNotificationChannel: RealtimeChannel? = null
        private var realtimeDetailMessageChannel: RealtimeChannel? = null
        private var realtimeListMessageChannel: RealtimeChannel? = null
    private suspend fun getRealtimeNotificationChannel(): RealtimeChannel {
        return withInteractorContext {
            if (realtimeNotificationChannel == null) {
                val userId = seasonInteractor.getCurrentUserId()
                val channelName = "notification:$userId"
                realtimeNotificationChannel = supabaseClient.realtime.createChannel(channelName)
            }
            realtimeNotificationChannel!!
        }
    }

    private suspend fun getRealtimeListMessagesChannel(): RealtimeChannel {
        return withInteractorContext {
            if (realtimeListMessageChannel == null) {
                val userId = seasonInteractor.getCurrentUserId()
                val channelName = "detail_messages:$userId"
                realtimeListMessageChannel = supabaseClient.realtime.createChannel(channelName)
            }
            realtimeListMessageChannel!!
        }
    }

    private suspend fun getRealtimeDetailMessagesChannel(): RealtimeChannel {
        return withInteractorContext {
            if (realtimeDetailMessageChannel == null) {
                val userId = seasonInteractor.getCurrentUserId()
                val channelName = "list_messages:$userId"
                realtimeDetailMessageChannel = supabaseClient.realtime.createChannel(channelName)
            }
            realtimeDetailMessageChannel!!
        }
    }

    private suspend fun getRealtimeChannel(chatListType: ChatListType) : RealtimeChannel =
         when(chatListType) {
            ChatListType.NOTIFICATION -> getRealtimeNotificationChannel()
            ChatListType.LIST_MESSAGES -> getRealtimeListMessagesChannel()
            ChatListType.DETAIL_MESSAGES -> getRealtimeDetailMessagesChannel()
    }

        override suspend fun getFlowStream(
            tableName: String,
            filterString: String?,
            chatListType: ChatListType
        ): Flow<PostgresAction> {
            val realtimeChannel = getRealtimeChannel(chatListType)
            val changeFlow = realtimeChannel.postgresChangeFlow<PostgresAction>(schema = "public") {
                table = tableName
                filter = filterString
            }
            return changeFlow
        }

        override suspend fun unSubscribe(chatListType: ChatListType) {
            val realtimeChannel = getRealtimeChannel(chatListType)
            realtimeChannel.let {
                it.leave()
                supabaseClient.realtime.removeChannel(it)
            }
        }

        override suspend fun subscribe(chatListType: ChatListType) {
            val realtimeChannel = getRealtimeChannel(chatListType)
            realtimeChannel.join()
        }
    override suspend fun subscribeToLastMessages(chatListType: ChatListType): Flow<LastMessage> {
       return withInteractorContext(retryOption = RetryOption(retryCount = 3, maxDelay = 10000, delayIncrementalFactor =  3.0, retryCondition = { it.getOrNull() == null })) {
          val flowStream =  getFlowStream("last_message", null, chatListType)
                .flatMapLatest { action ->
                    when (action) {
                        is PostgresAction.Insert -> {
                            when(val response = chatListInteractor.jsonElementToLastMessage(action.record.toString())){
                                is JsonLatMessageResponse.Success -> flowOf(response.lastMessage)
                                else -> emptyFlow()
                            }
                        }
                        is PostgresAction.Update -> {
                            when(val response = chatListInteractor.jsonElementToLastMessage(action.record.toString())){
                                is JsonLatMessageResponse.Success -> flowOf(response.lastMessage)
                                else -> emptyFlow()
                            }
                        }
                        else -> emptyFlow()
                    }
                }
           if(chatListType == ChatListType.NOTIFICATION) connect()
           subscribe(chatListType = chatListType)
           flowStream
        }
    }
    override suspend fun connect() {
        supabaseClient.realtime.connect()
    }
    override suspend fun disconnect() {
        supabaseClient.realtime.disconnect()
    }
}


