package com.java.cherrypick.presentationInfra

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.CONFLATED
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.isActive
import kotlinx.coroutines.selects.select

interface UiChannel<State> {
    suspend fun observe(scope: CoroutineScope): ReceiveChannel<UiEvent<out State>>
    fun setContent(content: State)
    fun getContent(): State
    fun setLoading()
    fun showDialog(message: String)
}

class UiChannelImpl<State>(initialState: State): UiChannel<State> {
    private val loadingChannel = Channel<UiEvent.Loading>(capacity = CONFLATED)
    private val errorChannel = Channel<UiEvent.Error>(capacity = CONFLATED)
    private val contentChannel = ConflatedBroadcastChannel(
        UiEvent.Content(value = initialState)
    )
    override suspend fun observe(scope: CoroutineScope): ReceiveChannel<UiEvent<out State>> {
        return scope.produce(Dispatchers.Main, capacity = UNLIMITED ) {
            val contentChannelSub = contentChannel.openSubscription()
            invokeOnClose { contentChannelSub.cancel() }
            while(isActive){
                select<Unit>{
                    errorChannel.onReceive{ send(it) }
                    loadingChannel.onReceive{ send(it) }
                    contentChannelSub.onReceive{ send(it) }
                }
            }
            close()
        }
    }

    override fun setContent(content: State) {
        contentChannel.trySend(UiEvent.Content(content)).isSuccess
    }
    override fun getContent(): State = contentChannel.value.value

    override fun setLoading() {
        loadingChannel.trySend(UiEvent.Loading).isSuccess
    }

    override fun showDialog(message: String) {
        errorChannel.trySend(UiEvent.Error(message)).isSuccess
    }
}