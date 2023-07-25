package com.java.cherrypick.android.notification

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PushNotificationService: FirebaseMessagingService(), KoinComponent {


    private val pushNotificationBuilder: PushNotificationBuilder by inject()
    override fun onNewToken(token: String) {

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        Log.i("pushNotification token: ", token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        NotificationModel.from(message)?.let {
            pushNotificationBuilder.build(it)?.send()
        }
    }
}