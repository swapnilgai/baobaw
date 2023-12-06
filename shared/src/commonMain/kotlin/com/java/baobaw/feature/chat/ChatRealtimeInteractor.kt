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
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withTimeoutOrNull

interface ChatRealtimeInteractor: Interactor {
    suspend fun getFlowStream(
        tableName: String,
        filterString: String?
    ): Flow<PostgresAction>

    suspend fun unSubscribe()
    suspend fun subscribe()
    suspend fun subscribeToLastMessages(): Flow<LastMessage>
    suspend fun connect()
    suspend fun disconnect()
    suspend fun isConnected(): Boolean
}
class ChatRealtimeInteractorImpl(
    private val supabaseClient: SupabaseClient,
    private val seasonInteractor: SeasonInteractor,
    private val chatListInteractor: ChatListInteractor) : ChatRealtimeInteractor {

    private val TIMEOUT_DURATION = 5000L // 5 seconds
    private var realtimeNotificationChannel: RealtimeChannel? = null

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

    private suspend fun getRealtimeChannel() : RealtimeChannel = getRealtimeNotificationChannel()

    override suspend fun getFlowStream(
            tableName: String,
            filterString: String?
        ): Flow<PostgresAction> = withInteractorContext(retryOption = RetryOption(retryCount = 5, maxDelay = 10000, delayIncrementalFactor =  2.0, retryCondition = { it.getOrNull() == null }, objectToReturn = emptyFlow())) {
            val realtimeChannel = getRealtimeChannel()
            val changeFlow = realtimeChannel.postgresChangeFlow<PostgresAction>(schema = "public") {
                table = tableName
                filter = filterString
            }
            changeFlow
        }

        override suspend fun unSubscribe() {
            val realtimeChannel = getRealtimeChannel()
            realtimeChannel.let {
                it.leave()
                supabaseClient.realtime.removeChannel(it)
            }
        }

    override suspend fun subscribe() {
        withInteractorContext(retryOption = RetryOption(retryCount = 5, maxDelay = 10000, delayIncrementalFactor =  2.0, objectToReturn = Unit)) {
            val realtimeChannel = getRealtimeChannel()
            realtimeChannel.join()
        }
    }
    override suspend fun subscribeToLastMessages(): Flow<LastMessage> {
       return withInteractorContext(retryOption = RetryOption(retryCount = 5, maxDelay = 10000, delayIncrementalFactor =  2.0, retryCondition = { it.getOrNull() == null }, objectToReturn = emptyFlow())) {
           val flowStream =  getFlowStream("last_message", null)
                .flatMapMerge { action ->
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
           connect()
           subscribe()
           flowStream
        }
    }
    override suspend fun connect() {
        withInteractorContext(retryOption = RetryOption(retryCount = 5, maxDelay = 10000, delayIncrementalFactor = 8.0, objectToReturn = Unit)) {
            withTimeoutOrNull(TIMEOUT_DURATION) {
                supabaseClient.realtime.connect()
            }
        }
    }

    override suspend fun disconnect() {
        supabaseClient.realtime.disconnect()
    }

   override suspend fun isConnected(): Boolean {
        val realtimeChannel = getRealtimeChannel()
        return realtimeChannel.status.value == RealtimeChannel.Status.JOINED || realtimeChannel.status.value == RealtimeChannel.Status.JOINING
    }
}


