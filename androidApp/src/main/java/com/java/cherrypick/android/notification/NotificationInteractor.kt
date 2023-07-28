package com.java.cherrypick.android.notification

import com.java.cherrypick.interactor.Interactor


interface Notification{
    suspend fun register(onesignalAppId: String, userId: String)
}
class AndroidNotification(): Notification, Interactor {
    override suspend fun register(onesignalAppId: String, userId: String) {
        OneSignal.setAppId(onesignalAppId)
        OneSignal.setExternalUserId(userId)
    }
}


class NotificationInteractor: Interactor {
}