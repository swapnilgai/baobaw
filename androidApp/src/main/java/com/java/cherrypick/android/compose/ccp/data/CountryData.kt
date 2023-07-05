package com.java.cherrypick.android.compose.ccp.data

import java.util.Locale


data class CountryData(
    private var cCodes: String,
    val countryPhoneCode: String = "+90",
    val cNames:String = "tr",
) {
    val countryCode = cCodes.lowercase(Locale.getDefault())
}
