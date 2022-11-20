package com.playground.liquidtime

import android.app.AlertDialog
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.lifecycle.lifecycleScope
import com.playground.liquidtime.ui.theme.LiquidTimeTheme


class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<MainViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.handleEvent(AuthEvent.AppCreated)
        lifecycleScope.launchWhenResumed {
            viewModel.actions.collect { action ->
                renderActions(action)
            }
        }

        setContent {
            LiquidTimeTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                    val state by remember {
                        mutableStateOf(viewModel.authState.value)
                    }
                    AuthLayout(state, viewModel::handleEvent)
                }
            }
        }
    }

    private fun renderActions(action: AuthAction) {
        when (action) {
            AuthAction.ShowAll -> AlertDialog.Builder(this@MainActivity).setTitle("Hey").setMessage(viewModel.authState.value.toString()).show()
            AuthAction.Cancelled -> toast(R.string.cancelled)
            is AuthAction.Failure -> toast(R.string.failed_with_reason, action.reason)
            AuthAction.NewUserCreated -> toast(R.string.new_user)
            AuthAction.ShowAlreadyLoggedIn -> toast(R.string.user_already_logged_in)
        }
    }

    private fun toast(@StringRes res: Int, vararg values: Any?) = Toast.makeText(this, getString(res, *values), Toast.LENGTH_LONG).show()
}

sealed class AuthLineCfg(val caption: String, val visualTransformation: VisualTransformation, val keyboardOptions: KeyboardOptions) {
    object Email: AuthLineCfg("E-Mail", VisualTransformation.None, KeyboardOptions(autoCorrect = true, keyboardType = KeyboardType.Email, imeAction = ImeAction.Done))
    object Password: AuthLineCfg("Password", PasswordVisualTransformation(), KeyboardOptions(autoCorrect = false, keyboardType = KeyboardType.Password, imeAction = ImeAction.Done))
}

