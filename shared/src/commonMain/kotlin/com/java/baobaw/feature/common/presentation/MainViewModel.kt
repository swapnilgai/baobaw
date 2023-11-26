package com.java.baobaw.feature.common.presentation

import com.java.baobaw.feature.chat.ChatListInteractor
import com.java.baobaw.feature.chat.ChatRealtimeInteractor
import com.java.baobaw.feature.chat.ChatRealtimeInteractorImpl
import com.java.baobaw.feature.common.interactor.CompatibilityBatchInteractor
import com.java.baobaw.interactor.interactorLaunch
import com.java.baobaw.presentationInfra.BaseViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.SessionStatus
import io.github.jan.supabase.gotrue.gotrue
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class MainViewModel(private val compatibilityBatchInteractor: CompatibilityBatchInteractor,
                    private val supabaseClient: SupabaseClient,
                    private val chatRealtimeInteractor: ChatRealtimeInteractor,
                    private val chatListInteractor: ChatListInteractor
) : BaseViewModel<Unit>(initialContent = Unit) {
    init {
       // initCompatibilityBatchInBackground()
        observeSessionStatus()
    }
    private fun loadInitialData() {
        // API calls that need to be made after authentication
        // Example: Fetch user profile, messages, etc.
    }

    fun observeSessionStatus() {
        viewModelScope.launch {
            supabaseClient.gotrue.sessionStatus.collect {
                when (it) {
                    is SessionStatus.Authenticated -> loadChat()
                    SessionStatus.LoadingFromStorage -> println("Loading from storage")
                    SessionStatus.NetworkError -> println("Network error")
                    SessionStatus.NotAuthenticated -> println("Not authenticated")
                }
            }
        }
    }

    fun loadChat() {
        viewModelScope.interactorLaunch {
            chatRealtimeInteractor.subscribeToNewMessages()
                .onEach { newMessage ->
                    chatListInteractor.invalidateMessageCache()
                }.launchIn(this)
            chatRealtimeInteractor.subscribe()
        }
    }

    fun initCompatibilityBatchInBackground() {
        viewModelScope.interactorLaunch {
            compatibilityBatchInteractor.initCompatibilityBatch()
        }
    }


}