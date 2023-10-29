package com.java.baobaw.android.notification

import android.content.res.Resources
import androidx.annotation.StringRes
import com.google.firebase.messaging.RemoteMessage
import java.util.Locale


sealed class NotificationModel {

    open val id = getNotificationId()
    open val channel =  NotificationChannel.DEFAULT

    abstract fun title(resources: Resources): String

    abstract fun body(resources: Resources): String

    data class Marketing(private val title: String, private val body: String): NotificationModel() {
        override fun title(resources: Resources) = title

        override fun body(resources: Resources) = body
    }

    data class Messaging(private val title: String, private val body: String): NotificationModel() {
        override fun title(resources: Resources) = title

        override fun body(resources: Resources) = body
    }

    data class Match(private val title: String, private val body: String): NotificationModel() {
        override fun title(resources: Resources) = title

        override fun body(resources: Resources) = body
    }

   companion object {
        fun from(remoteMessage: RemoteMessage): NotificationModel? {
            val data = remoteMessage.data
            val title = data["title"].orEmpty()
            val body = data["alert"].orEmpty()
            val type = data["type"].orEmpty().lowercase(Locale.US)
            val deeplinkUrl = data["deeplink"].orEmpty()
            if(title.isEmpty() && body.isEmpty()) return null

           return when(type){
                "marketing" -> Marketing(title = title, body = body)
                "match" -> Messaging(title = title, body = body)
                else -> Match(title = title, body = body)
            }
        }
    }
}


enum class NotificationChannel(@StringRes val descriptionRes: Int){
    DEFAULT(descriptionRes = com.java.baobaw.R.string.ok){
        override fun id(resources: Resources): String {
            return resources.getString(com.java.baobaw.R.string.ok)
        }
    };

    open fun id(resources: Resources) = name
}

private fun getNotificationId() = ID_COUNTER++

private var ID_COUNTER = 1

