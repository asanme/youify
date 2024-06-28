package com.asanme.youify.model.routes

sealed class Routes(val route: String) {
    data object SignInViewRoute : Routes("signInRoute")
    data object HomeViewRoute : Routes("homeViewRoute")
}