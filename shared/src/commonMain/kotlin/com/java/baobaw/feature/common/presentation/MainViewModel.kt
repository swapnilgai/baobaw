package com.java.baobaw.feature.common.presentation

import com.java.baobaw.feature.chat.ChatDetailInteractor
import com.java.baobaw.feature.chat.ChatListInteractor
import com.java.baobaw.feature.chat.ChatRealtimeInteractor
import com.java.baobaw.feature.common.interactor.CompatibilityBatchInteractor
import com.java.baobaw.interactor.clearAll
import com.java.baobaw.interactor.interactorLaunch
import com.java.baobaw.presentationInfra.BaseViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.SessionStatus
import io.github.jan.supabase.gotrue.gotrue
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class MainViewModel(private val compatibilityBatchInteractor: CompatibilityBatchInteractor,
                    private val supabaseClient: SupabaseClient,
                    private val chatRealtimeInteractor: ChatRealtimeInteractor,
                    private val chatListInteractor: ChatListInteractor,
                    private val chatDetailInteractor: ChatDetailInteractor
) : BaseViewModel<Unit>(initialContent = Unit) {
    private var lastBackgroundTime: Long = 0
    init {
       // initCompatibilityBatchInBackground()
    }
    private fun loadInitialData() {
        // API calls that need to be made after authentication
        // Example: Fetch user profile, messages, etc.
    }

    fun observeSessionStatus() {
        viewModelScope.launch {
            supabaseClient.gotrue.sessionStatus.collect {
                when (it) {
                    is SessionStatus.Authenticated -> subscribeToChat()
                    SessionStatus.LoadingFromStorage -> println("Loading from storage")
                    SessionStatus.NetworkError -> println("Network error")
                    SessionStatus.NotAuthenticated -> println("Not authenticated")
                }
            }
        }
    }

    private fun subscribeToChat() {
        viewModelScope.interactorLaunch {
            setLoading()
            val currentTime = Clock.System.now().epochSeconds
            if(currentTime - lastBackgroundTime > 600) // clear cache if app is in background for more than 10 minutes
            chatRealtimeInteractor.clearAll()
            chatRealtimeInteractor.subscribeToLastMessages()
                .onEach { newMessage ->
                    chatListInteractor.updateMessages(newMessage)
                    chatDetailInteractor.updateMessages(newMessage)
                }.launchIn(this)
                setContent { getContent()  }
        }
    }

    fun initCompatibilityBatchInBackground() {
        viewModelScope.interactorLaunch {
            compatibilityBatchInteractor.initCompatibilityBatch()
        }
    }
    fun cancelSubscribeToChat() {

    }

    override suspend fun clearViewModel() {
        viewModelScope.launch {
            chatRealtimeInteractor.unSubscribe()
            chatRealtimeInteractor.disconnect()
            lastBackgroundTime = Clock.System.now().epochSeconds
        }.join()
    }
}