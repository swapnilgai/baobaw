package com.java.baobaw.android.compose.ccp.data.utils

import com.java.baobaw.android.compose.ccp.data.CountryData


fun List<CountryData>.searchCountry(key: String): List<CountryData> {
    return this.filter { countryData ->  countryData.countryPhoneCode.lowercase().contains(key) }
}