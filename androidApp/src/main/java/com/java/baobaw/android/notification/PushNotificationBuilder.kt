package com.java.baobaw.android.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class PushNotificationBuilder(
    private val resources: Resources,
    private val context: Context
    ) {

    fun build(notificationModel: NotificationModel): NotificationSender? {

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) return null


        val body = notificationModel.body(resources)
        val notificationBuilder = NotificationCompat.Builder(context, notificationModel.channel.id(resources))

        return NotificationSender {
            createChannel(notificationModel)
            NotificationManagerCompat.from(context).notify(
                notificationModel.id,
                notificationBuilder.build()
            )
        }
    }

    private fun createChannel(notificationModel: NotificationModel){
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val channel = NotificationChannel(
                notificationModel.channel.id(resources),
                resources.getString(notificationModel.channel.descriptionRes),
                NotificationManager.IMPORTANCE_HIGH
        )

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

}


fun interface NotificationSender{
    fun send()
}