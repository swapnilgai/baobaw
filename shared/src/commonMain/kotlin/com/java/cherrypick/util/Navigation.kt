package com.java.cherrypick.util

fun getNavigationUrl(baseRoute: String, args: List<String>): String {
    var navRoute = baseRoute
    args.forEach { value -> navRoute = navRoute.plus("/{${value}}") }
    return navRoute;
}


fun getNavigationUrlWithoutBrackets(baseRoute: String, args: List<String>) : String {
   return getNavigationUrl(baseRoute, args).replace("{", "").replace("}", "")
}