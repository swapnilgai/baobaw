package com.java.baobaw.util

fun getNavigationUrl(baseRoute: String, args: List<Any>): String {
    var navRoute = baseRoute
    args.forEach { value -> navRoute = navRoute.plus("/{${value}}") }
    return navRoute;
}


fun getNavigationUrlWithoutBrackets(baseRoute: String, args: List<Any>) : String {
   return getNavigationUrl(baseRoute, args).replace("{", "").replace("}", "")
}