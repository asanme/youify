package com.asanme.youify.model.routes

sealed class Routes(val route: String) {
    object SignInViewRoute : Routes("signInRoute")
    object HomeViewRoute : Routes("homeViewRoute")
}