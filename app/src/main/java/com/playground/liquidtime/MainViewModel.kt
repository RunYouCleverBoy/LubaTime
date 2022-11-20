package com.playground.liquidtime

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.system.exitProcess
import kotlin.time.Duration.Companion.seconds

class MainViewModel: ViewModel() {
    private val authRepo = AuthRepo()
    val authState = MutableStateFlow(AuthState(user = "", pass = ""))
    private val actionsMutable = MutableSharedFlow<AuthAction>()
    val actions = actionsMutable as SharedFlow<AuthAction>

    fun handleEvent(event: AuthEvent) {
        when (event) {
            AuthEvent.AppCreated -> authRepo.create()
            is AuthEvent.Password -> authState.update { it.copy(pass = event.password) }
            is AuthEvent.Username -> authState.update { it.copy(user = event.userName) }
            AuthEvent.GetOut -> onLogout()
            AuthEvent.Okay -> onAuth()
        }
    }

    private fun onAuth() {
        viewModelScope.launch {
            val result = with(authState.value) {
                authRepo.signUp(email = user, password = pass)
            }

            when (result) {
                AuthRepo.AuthenticationResult.AlreadyLoggedIn -> emit(AuthAction.ShowAlreadyLoggedIn)
                AuthRepo.AuthenticationResult.Cancelled -> emit(AuthAction.Cancelled)
                is AuthRepo.AuthenticationResult.Failure -> emit(AuthAction.Failure(result.reason))
                is AuthRepo.AuthenticationResult.NewUser -> emit(AuthAction.NewUserCreated)
            }

        }
    }

    private fun onLogout() {
        viewModelScope.launch {
            kotlin.runCatching { authRepo.signOut() }.onFailure {
                emit(AuthAction.Failure(it as? Exception))
            }
            delay(10.seconds)
            exitProcess(-1)
        }
    }

    private fun emit(action: AuthAction) {
        viewModelScope.launch {
            actionsMutable.emit(action)
        }
    }

}