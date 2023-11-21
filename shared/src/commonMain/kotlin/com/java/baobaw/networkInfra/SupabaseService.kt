package com.java.baobaw.networkInfra

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.PostgrestFilterDSL
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.gotrue.user.UserInfo
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Count
import io.github.jan.supabase.postgrest.query.PostgrestFilterBuilder
import io.github.jan.supabase.postgrest.query.PostgrestUpdate
import io.github.jan.supabase.postgrest.query.Returning
import io.github.jan.supabase.postgrest.result.PostgrestResult
import io.github.jan.supabase.postgrest.rpc
import io.github.jan.supabase.realtime.RealtimeChannel
import io.github.jan.supabase.realtime.createChannel
import io.github.jan.supabase.realtime.realtime
import io.github.jan.supabase.storage.storage
import kotlinx.serialization.json.JsonElement


interface SupabaseService {
    suspend fun currentUserOrNull(): UserInfo?
    suspend fun tableUpdate(
        tableName: String,
        update: PostgrestUpdate.() -> Unit = {},
        returning: Returning = Returning.REPRESENTATION,
        count: Count? = null,
        filter: PostgrestFilterBuilder.() -> Unit = {}
    ): PostgrestResult

    suspend fun rpc(
        function: String,
        parameters: JsonElement? = null,
        head: Boolean = false,
        count: Count? = null,
        filter: PostgrestFilterBuilder.() -> Unit = {}
    ): PostgrestResult

    suspend fun bucketUpload(bucket: String, path: String, data: ByteArray, upsert: Boolean = false): String
    suspend fun bucketDelete(path: String): Unit
    fun bucketPublicUrl(bucket: String, path: String): String

    suspend fun select(
        tableName: String,
        columns: Columns = Columns.ALL,
        head: Boolean = false,
        count: Count? = null,
        single: Boolean = false,
        filter: @PostgrestFilterDSL PostgrestFilterBuilder.() -> Unit = {}
    ): PostgrestResult

    fun getMessageRealtimeChannel(): RealtimeChannel

    suspend fun realTimeConnect()

    fun realTimeDisconnect()

    suspend fun realtimeRemoveChannel(realtimeChannel: RealtimeChannel)

}

class SupabaseServiceImpl(private val supabaseClient: SupabaseClient): SupabaseService {

    private val realtimeChannel: RealtimeChannel = supabaseClient.realtime.createChannel("messages")
    override suspend fun tableUpdate(
        tableName: String,
        update: PostgrestUpdate.() -> Unit,
        returning: Returning,
        count: Count?,
        filter: PostgrestFilterBuilder.() -> Unit
    ): PostgrestResult = supabaseClient.postgrest[tableName].update(update = update, filter = filter)

    override suspend fun rpc(
        function: String,
        parameters: JsonElement?,
        head: Boolean,
        count: Count?,
        filter: PostgrestFilterBuilder.() -> Unit
    ): PostgrestResult = when(parameters) {
           null -> supabaseClient.postgrest.rpc(function) { filter }
           else -> supabaseClient.postgrest.rpc(function, parameters) { filter }
    }

    override suspend fun bucketUpload(bucket: String, path: String, data: ByteArray, upsert: Boolean): String {
        return supabaseClient.storage[bucket].upload(path, data, upsert)
    }

    override  fun bucketPublicUrl(bucket: String, path: String): String {
        return supabaseClient.storage[bucket].publicUrl(path)
    }

    override suspend fun bucketDelete(path: String): Unit {
        return supabaseClient.storage[path].delete(path)
    }

    override suspend fun currentUserOrNull(): UserInfo? = supabaseClient.gotrue.currentUserOrNull()

    override suspend fun select(
        tableName: String,
        columns: Columns,
        head: Boolean,
        count: Count?,
        single: Boolean,
        filter: PostgrestFilterBuilder.() -> Unit
    ): PostgrestResult {
        return supabaseClient.postgrest[tableName].select(columns = columns, head = head, count = count, single = single, filter = filter)
    }

    override fun getMessageRealtimeChannel(): RealtimeChannel = realtimeChannel
    override suspend fun realTimeConnect() {
        supabaseClient.realtime.connect()
    }

    override fun realTimeDisconnect() {
        supabaseClient.realtime.disconnect()
    }

    override suspend fun realtimeRemoveChannel(realtimeChannel: RealtimeChannel) {
        supabaseClient.realtime.removeChannel(realtimeChannel)
    }

}