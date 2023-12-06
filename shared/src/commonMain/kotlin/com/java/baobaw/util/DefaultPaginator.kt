package com.java.baobaw.util

import com.java.baobaw.feature.chat.ChatMessage

interface Paginator<Key, Item> {
    suspend fun loadNextItems()
    fun reset()
}

class DefaultPaginator<Key>(
    private val initialKey: Key,
    private inline val onLoadUpdated: (Boolean) -> Unit,
    private inline val onRequest: suspend (nextKey: Key) -> Result<Map<String, List<ChatMessage>>>,
    private inline val getNextKey: suspend (Map<String, List<ChatMessage>>) -> Key,
    private inline val onError: suspend (Throwable?) -> Unit,
    private inline val onSuccess: suspend (items: Map<String, List<ChatMessage>>, newKey: Key) -> Unit
) : Paginator<Key, Map<String, List<ChatMessage>>> {

    private var currentKey = initialKey
    private var isMakingRequest = false

    override suspend fun loadNextItems() {
        if (isMakingRequest) {
            return
        }
        isMakingRequest = true
        onLoadUpdated(true)
        val result = onRequest(currentKey)
        isMakingRequest = false
        val items = result.getOrElse {
            onError(it)
            onLoadUpdated(false)
            return
        }
        currentKey = getNextKey(items)
        onSuccess(items, currentKey)
        onLoadUpdated(false)
    }

    override fun reset() {
        currentKey = initialKey
    }

    data class ScreenState(
        val isLoading: Boolean = false,
        val items: Map<String, List<ChatMessage>> = emptyMap(),
        val error: String? = null,
        val endReached: Boolean = false,
        val page: Int = 0
    )
}
