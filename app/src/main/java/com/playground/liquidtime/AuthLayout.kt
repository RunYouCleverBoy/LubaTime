package com.playground.liquidtime

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.playground.liquidtime.ui.theme.LiquidTimeTheme

@Composable
fun AuthLayout(authState: AuthState, onEvent: (AuthEvent) -> Unit = {}) {
    Column {
        TextLine(AuthLineCfg.Email, authState.user) { onEvent(AuthEvent.Username(it)) }
        TextLine(AuthLineCfg.Password, authState.pass) { onEvent(AuthEvent.Password(it)) }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Absolute.SpaceBetween
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Button(onClick = { onEvent(AuthEvent.GetOut) }) {
                Text("Bug off")
            }
            Spacer(modifier = Modifier.weight(1f))
            Button(onClick = { onEvent(AuthEvent.Okay) }) {
                Text("OKAY")
            }
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun TextLine(config: AuthLineCfg, value: String, onChange: (newVal: String) -> Unit) {
    var text: String by remember {
        mutableStateOf(value)
    }

    Row {
        val modifier = Modifier
            .align(Alignment.CenterVertically)
            .padding(horizontal = 10.dp)
        Text(config.caption, modifier.width(100.dp))
        TextField(
            value = text,
            visualTransformation = config.visualTransformation,
            keyboardOptions = config.keyboardOptions,
            modifier = modifier,
            onValueChange = {
                text = it
                onChange.invoke(it)
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    LiquidTimeTheme {
        AuthLayout(AuthState("Preview", "Pass"))
    }
}