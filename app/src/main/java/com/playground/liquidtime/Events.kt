package com.playground.liquidtime

sealed class AuthEvent {
    object AppCreated: AuthEvent()
    data class Username(val userName: String): AuthEvent()
    data class Password(val password: String): AuthEvent()
    object Okay: AuthEvent()
    object GetOut: AuthEvent()
}