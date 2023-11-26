package com.java.baobaw.feature.chat

import com.java.baobaw.feature.common.interactor.SeasonInteractor
import com.java.baobaw.interactor.Interactor
import com.java.baobaw.interactor.interactorLaunch
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
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking

interface ChatRealtimeInteractor: Interactor {
    suspend fun getFlowStream(tableName: String, filterString: String?): Flow<PostgresAction>
    suspend fun unSubscribe()
    suspend fun subscribe()
    suspend fun subscribeToNewMessages(): Flow<LastMessage>

}
class ChatRealtimeInteractorImpl(
    private val supabaseClient: SupabaseClient,
    val seasonInteractor: SeasonInteractor,
    private val chatListInteractor: ChatListInteractor
    ) : ChatRealtimeInteractor {
        private var realtimeChannel: RealtimeChannel? = null
        private suspend fun getRealtimeChannelForUser(): RealtimeChannel {
            return withInteractorContext {
                if (realtimeChannel == null) {
                    val userId = seasonInteractor.getCurrentUserId()
                    val channelName = "messages:$userId"
                    realtimeChannel = supabaseClient.realtime.createChannel(channelName)
                }
                realtimeChannel!!
            }
        }

        override suspend fun getFlowStream(
            tableName: String,
            filterString: String?
        ): Flow<PostgresAction> {
            val realtimeChannel = getRealtimeChannelForUser()
            val changeFlow = realtimeChannel.postgresChangeFlow<PostgresAction>(schema = "public") {
                table = tableName
                filter = filterString
            }
            return changeFlow
        }

        override suspend fun unSubscribe() {
            getRealtimeChannelForUser().let {
                it.leave()
                supabaseClient.realtime.disconnect()
                supabaseClient.realtime.removeChannel(it)
            }
        }

        override suspend fun subscribe() {
            supabaseClient.realtime.connect()
            getRealtimeChannelForUser().join()
        }
    override suspend fun subscribeToNewMessages(): Flow<LastMessage> {
        return getFlowStream("last_message", null)
            .flatMapConcat { action ->
                when (action) {
                    is PostgresAction.Insert -> {
                        flowOf(chatListInteractor.jsonElementToLastMessage(action.record.toString()))
                    }
                    is PostgresAction.Update -> {
                        flowOf(chatListInteractor.jsonElementToLastMessage(action.record.toString()))
                    }
                    else -> emptyFlow()
                }
            }
    }
}


