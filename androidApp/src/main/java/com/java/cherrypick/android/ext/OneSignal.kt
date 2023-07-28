package com.java.cherrypick.android.ext

import android.content.Context
import com.onesignal.OneSignal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun intiOneSignal(onesignalAppId: String, userId: String, context: Context){
    withContext(Dispatchers.Unconfined) {
        //OneSignal.initWithContext()
        OneSignal.setAppId(onesignalAppId)
        OneSignal.setExternalUserId(userId)
    }
}