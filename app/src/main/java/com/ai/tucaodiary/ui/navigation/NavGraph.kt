package com.ai.tucaodiary.ui.navigation

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ai.tucaodiary.ui.screens.*
import com.ai.tucaodiary.viewmodel.*

@Composable
fun NavGraph() {
    val nav = rememberNavController()
    val authVm: AuthViewModel = viewModel()
    val homeVm: HomeViewModel = viewModel()
    val paymentVm: PaymentViewModel = viewModel()
    val adminVm: AdminViewModel = viewModel()

    LaunchedEffect(authVm.isLoggedIn) { nav.popBackStack() }

    NavHost(nav, startDestination = if (authVm.isLoggedIn) "home" else "login") {
        composable("login") { LoginScreen(authVm, onRegister = { nav.navigate("register") }) }
        composable("register") { RegisterScreen(authVm, onLogin = { nav.navigate("login") }) }
        composable("home") { HomeScreen(authVm, homeVm, onAdmin = { nav.navigate("admin") }, onPayment = { nav.navigate("payment") }) }
        composable("payment") { PaymentScreen(paymentVm, onBack = { nav.popBackStack() }) }
        composable("admin") { AdminScreen(adminVm, onBack = { nav.popBackStack() }) }
    }
}
