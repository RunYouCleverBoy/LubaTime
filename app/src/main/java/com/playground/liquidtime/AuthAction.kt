package com.playground.liquidtime

sealed class AuthAction {
    object ShowAlreadyLoggedIn : AuthAction()

    object Cancelled : AuthAction()

    data class Failure(val reason: Exception?) : AuthAction()

    object NewUserCreated : AuthAction()

    object ShowAll: AuthAction()
}
