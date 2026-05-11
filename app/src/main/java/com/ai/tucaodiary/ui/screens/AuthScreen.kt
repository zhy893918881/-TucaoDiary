package com.ai.tucaodiary.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.ai.tucaodiary.ui.theme.Orange
import com.ai.tucaodiary.ui.theme.Pink
import com.ai.tucaodiary.ui.theme.TextSub
import com.ai.tucaodiary.viewmodel.AuthViewModel

@Composable
fun LoginScreen(vm: AuthViewModel, onRegister: () -> Unit) {
    var user by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var showPass by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text("🔥 吐槽日记", style = MaterialTheme.typography.headlineLarge, color = Orange)
        Spacer(Modifier.height(4.dp))
        Text("把怨气变成段子", style = MaterialTheme.typography.bodyMedium, color = TextSub)
        Spacer(Modifier.height(40.dp))

        OutlinedTextField(user, { user = it }, label = { Text("用户名") }, singleLine = true,
            modifier = Modifier.fillMaxWidth(), colors = fieldColors())
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(pass, { pass = it }, label = { Text("密码") }, singleLine = true,
            visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = { IconButton(onClick = { showPass = !showPass }) { Icon(if (showPass) Icons.Filled.Visibility else Icons.Filled.VisibilityOff, "") } },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
            modifier = Modifier.fillMaxWidth(), colors = fieldColors())

        if (vm.error.isNotBlank()) {
            Spacer(Modifier.height(8.dp))
            Text(vm.error, color = Pink, style = MaterialTheme.typography.bodyMedium)
        }

        Spacer(Modifier.height(24.dp))
        Button(
            { if (!vm.isLoading) vm.login(user, pass) },
            Modifier.fillMaxWidth().height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Orange),
            enabled = !vm.isLoading
        ) { if (vm.isLoading) CircularProgressIndicator(Modifier.size(22.dp), color = androidx.compose.ui.graphics.Color.White) else Text("登 录", style = MaterialTheme.typography.labelLarge) }

        Spacer(Modifier.height(16.dp))
        TextButton(onRegister) { Text("还没有账号？", color = TextSub); Text(" 去注册", color = Orange) }
    }
}

@Composable
fun RegisterScreen(vm: AuthViewModel, onLogin: () -> Unit) {
    var user by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text("🔐 注册", style = MaterialTheme.typography.headlineLarge, color = Orange)
        Spacer(Modifier.height(32.dp))

        OutlinedTextField(user, { user = it }, label = { Text("用户名(至少2位)") }, singleLine = true, modifier = Modifier.fillMaxWidth(), colors = fieldColors())
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(pass, { pass = it }, label = { Text("密码(至少4位)") }, singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(), colors = fieldColors())

        if (vm.error.isNotBlank()) { Spacer(Modifier.height(8.dp)); Text(vm.error, color = Pink, style = MaterialTheme.typography.bodyMedium) }

        Spacer(Modifier.height(24.dp))
        Button({ vm.register(user, pass) }, Modifier.fillMaxWidth().height(52.dp), colors = ButtonDefaults.buttonColors(containerColor = Orange), enabled = !vm.isLoading) { Text("注 册", style = MaterialTheme.typography.labelLarge) }

        Spacer(Modifier.height(16.dp))
        TextButton(onLogin) { Text("已有账号？", color = TextSub); Text(" 去登录", color = Orange) }
    }
}

@Composable
fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Orange, unfocusedBorderColor = TextSub,
    focusedLabelColor = Orange, unfocusedLabelColor = TextSub,
    cursorColor = Orange
)
