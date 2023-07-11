package com.java.cherrypick.util

fun getNavigationUrl(baseRoute: String, args: List<String>): String {
    var navRoute = baseRoute
    args.forEach { value -> navRoute = navRoute.plus("/{${value}}") }
    return navRoute;
}