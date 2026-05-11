package com.ai.tucaodiary.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ai.tucaodiary.data.local.TokenManager
import com.ai.tucaodiary.data.remote.*
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    var token by mutableStateOf<String?>(TokenManager.token)
    var user by mutableStateOf<UserInfo?>(null)
    var isVip by mutableStateOf(TokenManager.isVip)
    var vipExpire by mutableStateOf(TokenManager.vipExpire)
    var balance by mutableStateOf(TokenManager.balance)
    var dailyCount by mutableStateOf(0)
    var freeLimit by mutableStateOf(3)
    var isLoading by mutableStateOf(false)
    var error by mutableStateOf("")
    var isLoggedIn by mutableStateOf(TokenManager.token != null)

    val isAdmin get() = user?.role == "admin"

    fun loadUser() {
        if (token == null) return
        viewModelScope.launch {
            try {
                val r = RetrofitClient.api.me()
                if (r.isSuccessful) {
                    r.body()?.let { u ->
                        user = UserInfo(u.id, u.username, u.role, u.isVip, u.vipExpire, u.balance, u.dailyCount)
                        TokenManager.saveUser(user!!)
                        isVip = u.isVip; vipExpire = u.vipExpire; balance = u.balance; dailyCount = u.dailyCount
                    }
                }
            } catch (_: Exception) {}
        }
    }

    fun loadConfig() {
        viewModelScope.launch {
            try {
                val r = RetrofitClient.api.getConfig()
                if (r.isSuccessful) r.body()?.let { freeLimit = it.freeLimit }
            } catch (_: Exception) {}
        }
    }

    fun login(username: String, password: String) {
        isLoading = true; error = ""
        viewModelScope.launch {
            try {
                val r = RetrofitClient.api.login(LoginRequest(username, password))
                if (r.isSuccessful) {
                    r.body()?.let {
                        token = it.token; user = it.user
                        TokenManager.token = it.token; TokenManager.saveUser(it.user)
                        RetrofitClient.token = it.token
                        isVip = it.user.isVip; balance = it.user.balance
                        isLoggedIn = true
                        loadConfig()
                    }
                } else {
                    error = try { com.google.gson.Gson().fromJson(r.errorBody()?.string(), Map::class.java)["error"] as? String ?: "" } catch (_: Exception) { "登录失败" }
                }
            } catch (_: Exception) { error = "网络错误，请检查连接" }
            isLoading = false
        }
    }

    fun register(username: String, password: String) {
        isLoading = true; error = ""
        viewModelScope.launch {
            try {
                val r = RetrofitClient.api.register(RegisterRequest(username, password))
                if (r.isSuccessful) {
                    r.body()?.let {
                        token = it.token; user = it.user
                        TokenManager.token = it.token; TokenManager.saveUser(it.user)
                        RetrofitClient.token = it.token
                        isVip = it.user.isVip; balance = it.user.balance
                        isLoggedIn = true
                    }
                } else {
                    error = try { com.google.gson.Gson().fromJson(r.errorBody()?.string(), Map::class.java)["error"] as? String ?: "" } catch (_: Exception) { "注册失败" }
                }
            } catch (_: Exception) { error = "网络错误，请检查连接" }
            isLoading = false
        }
    }

    fun logout() {
        TokenManager.clear(); RetrofitClient.token = null
        token = null; user = null; isLoggedIn = false; isVip = false
    }
}
