package com.java.cherrypick.interactor

import android.os.Build
import android.util.LruCache
import androidx.annotation.RequiresApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import kotlin.coroutines.coroutineContext

interface Interactor

@RequiresApi(Build.VERSION_CODES.HONEYCOMB_MR1)
private val cache : LruCache<String, Any> = LruCache(100)

@RequiresApi(Build.VERSION_CODES.HONEYCOMB_MR1)
suspend fun <T> Interactor.withInteractorContext(
    cacheKey: String ? = null,
    block: suspend CoroutineScope.() -> T
) : T? {

    val isCacheAllowed = coroutineContext[CacheCoroutineContextElement]?.setCache
    val context = InteractorDispatcherProvider.dispatcher

    return withContext(context) {
        val cacheResult: T? = if (isCacheAllowed == true && cacheKey !=null) cache.get(cacheKey) as T ? else null
        return@withContext if (cacheResult != null){
            cacheResult
        } else{
            var blockResult : T ? = null
            try {
                blockResult = coroutineScope {
                    block()
                }
                if(cacheKey!=null) cache.put(cacheKey, blockResult)
            } catch (e: Exception){
                throw e.toInteractorException()
            }
            return@withContext blockResult
        }
    }
}
