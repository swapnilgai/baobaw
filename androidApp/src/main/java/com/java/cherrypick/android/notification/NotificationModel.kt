package com.java.cherrypick.android.notification

import android.content.res.Resources
import androidx.annotation.StringRes


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
}


enum class NotificationChannel(@StringRes val descriptionRes: Int){
    DEFAULT(descriptionRes = com.java.cherrypick.R.string.ok){
        override fun id(resources: Resources): String {
            return resources.getString(com.java.cherrypick.R.string.ok)
        }
    };

    open fun id(resources: Resources) = name
}

private fun getNotificationId() = ID_COUNTER++

private var ID_COUNTER = 1