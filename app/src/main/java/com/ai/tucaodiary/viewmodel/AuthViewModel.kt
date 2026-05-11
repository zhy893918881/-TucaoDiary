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
        if (username.isBlank() || password.isBlank()) { error = "请输入用户名和密码"; return }
        isLoading = true; error = ""
        viewModelScope.launch {
            try {
                val r = RetrofitClient.api.login(LoginRequest(username, password))
                if (r.isSuccessful) {
                    val resp = r.body()
                    if (resp == null || resp.user == null) {
                        error = "登录失败：服务器返回数据异常"
                        isLoading = false
                        return@launch
                    }
                    token = resp.token; user = resp.user
                    TokenManager.token = resp.token; TokenManager.saveUser(resp.user)
                    RetrofitClient.token = resp.token
                    isVip = resp.user.isVip; vipExpire = resp.user.vipExpire; balance = resp.user.balance
                    isLoggedIn = true
                    loadConfig()
                } else {
                    val msg = try { 
                        val errBody = r.errorBody()?.string()
                        com.google.gson.Gson().fromJson(errBody, Map::class.java)["error"] as? String
                    } catch (_: Exception) { null }
                    error = msg ?: "登录失败 (${r.code()})"
                }
            } catch (e: Exception) { 
                error = "网络错误，请检查连接"
                android.util.Log.e("AuthVM", "login failed", e)
            }
            isLoading = false
        }
    }

    fun register(username: String, password: String) {
        if (username.length < 2) { error = "用户名至少2位"; return }
        if (password.length < 4) { error = "密码至少4位"; return }
        isLoading = true; error = ""
        viewModelScope.launch {
            try {
                val r = RetrofitClient.api.register(RegisterRequest(username, password))
                if (r.isSuccessful) {
                    val resp = r.body()
                    if (resp == null || resp.user == null) {
                        error = "注册失败：服务器返回数据异常"
                        isLoading = false
                        return@launch
                    }
                    token = resp.token; user = resp.user
                    TokenManager.token = resp.token; TokenManager.saveUser(resp.user)
                    RetrofitClient.token = resp.token
                    isVip = resp.user.isVip; vipExpire = resp.user.vipExpire; balance = resp.user.balance
                    isLoggedIn = true
                } else {
                    val msg = try { 
                        val errBody = r.errorBody()?.string()
                        com.google.gson.Gson().fromJson(errBody, Map::class.java)["error"] as? String
                    } catch (_: Exception) { null }
                    error = msg ?: "注册失败 (${r.code()})"
                }
            } catch (e: Exception) { 
                error = "网络错误，请检查连接"
                android.util.Log.e("AuthVM", "register failed", e)
            }
            isLoading = false
        }
    }

    fun logout() {
        TokenManager.clear(); RetrofitClient.token = null
        token = null; user = null; isLoggedIn = false; isVip = false
    }
}
