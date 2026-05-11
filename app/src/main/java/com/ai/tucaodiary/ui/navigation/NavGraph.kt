package com.ai.tucaodiary.ui.navigation

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ai.tucaodiary.data.local.TokenManager
import com.ai.tucaodiary.data.remote.RetrofitClient
import com.ai.tucaodiary.ui.screens.*
import com.ai.tucaodiary.ui.theme.Orange
import com.ai.tucaodiary.viewmodel.*

@Composable
fun NavGraph() {
    val nav = rememberNavController()
    val authVm: AuthViewModel = viewModel()
    val homeVm: HomeViewModel = viewModel()
    val paymentVm: PaymentViewModel = viewModel()
    val adminVm: AdminViewModel = viewModel()

    val start = remember {
        RetrofitClient.token = TokenManager.token
        if (TokenManager.token != null) "home" else "login"
    }

    NavHost(nav, startDestination = start) {
        composable("login") { LoginScreen(authVm, onRegister = { nav.navigate("register") }) }
        composable("register") { RegisterScreen(authVm, onLogin = { nav.popBackStack() }) }
        composable("home") { HomeScreen(authVm, homeVm, onAdmin = { nav.navigate("admin") }, onPayment = { nav.navigate("payment") }) }
        composable("payment") { PaymentScreen(paymentVm, onBack = { nav.popBackStack() }) }
        composable("admin") { AdminScreen(adminVm, onBack = { nav.popBackStack() }) }
    }
}
