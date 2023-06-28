package com.java.cherrypick.presentation

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

interface UiChannel<ContentT> {
    suspend fun observe(scope: CoroutineScope): ReceiveChannel<UiEvent<out ContentT>>
    fun setContent(content: ContentT)
    fun getContent(): ContentT
    fun setLoading()
    fun showDialog(message: String)
}

class UiChannelImpl<ContentT>(initialContent: ContentT): UiChannel<ContentT> {
    private val loadingChannel = Channel<UiEvent.Loading>(capacity = CONFLATED)
    private val errorChannel = Channel<UiEvent.Error>(capacity = CONFLATED)
    private val contentChannel = ConflatedBroadcastChannel(
        UiEvent.Content(value = initialContent)
    )
    override suspend fun observe(scope: CoroutineScope): ReceiveChannel<UiEvent<out ContentT>> {
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

    override fun setContent(content: ContentT) {
        contentChannel.trySend(UiEvent.Content(content))
    }
    override fun getContent(): ContentT = contentChannel.value.value

    override fun setLoading() {
        loadingChannel.trySend(UiEvent.Loading)
    }

    override fun showDialog(message: String) {
        errorChannel.trySend(UiEvent.Error(message))
    }
}