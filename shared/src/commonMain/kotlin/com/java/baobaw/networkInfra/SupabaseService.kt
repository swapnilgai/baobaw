package com.java.baobaw.networkInfra

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.gotrue.user.UserInfo
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Count
import io.github.jan.supabase.postgrest.query.PostgrestFilterBuilder
import io.github.jan.supabase.postgrest.query.PostgrestUpdate
import io.github.jan.supabase.postgrest.query.Returning
import io.github.jan.supabase.postgrest.result.PostgrestResult
import io.github.jan.supabase.postgrest.rpc
import io.github.jan.supabase.storage.storage


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
        parameters: Any? = null,
        head: Boolean = false,
        count: Count? = null,
        filter: PostgrestFilterBuilder.() -> Unit = {}
    ): PostgrestResult

    suspend fun bucketUpload(path: String, data: ByteArray, upsert: Boolean = false): String
    suspend fun bucketDelete(path: String): Unit
    fun bucketPublicUrl(path: String): String
}
class SupabaseServiceImpl(private val supabaseClient: SupabaseClient): SupabaseService {

    override suspend fun tableUpdate(
        tableName: String,
        update: PostgrestUpdate.() -> Unit,
        returning: Returning,
        count: Count?,
        filter: PostgrestFilterBuilder.() -> Unit
    ): PostgrestResult = supabaseClient.postgrest[tableName].update(update) { filter }

    override suspend fun rpc(
        function: String,
        parameters: Any?,
        head: Boolean,
        count: Count?,
        filter: PostgrestFilterBuilder.() -> Unit
    ): PostgrestResult = when(parameters) {
           null -> supabaseClient.postgrest.rpc(function) { filter }
           else -> supabaseClient.postgrest.rpc(function, parameters) { filter }
    }

    override suspend fun bucketUpload(path: String, data: ByteArray, upsert: Boolean): String {
        return supabaseClient.storage[path].upload(path, data, upsert)
    }

    override fun bucketPublicUrl(path: String): String {
        return supabaseClient.storage[path].publicUrl(path)
    }

    override suspend fun bucketDelete(path: String): Unit {
        return supabaseClient.storage[path].delete(path)
    }

    override suspend fun currentUserOrNull(): UserInfo? = supabaseClient.gotrue.currentUserOrNull()
}