package com.asanme.youify.model.routes

sealed class AppRoutes(val route: String) {
    object LoginRoute : AppRoutes("")
}