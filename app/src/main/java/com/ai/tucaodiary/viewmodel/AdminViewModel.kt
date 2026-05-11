package com.ai.tucaodiary.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ai.tucaodiary.data.remote.*
import kotlinx.coroutines.launch

class AdminViewModel : ViewModel() {
    var apiKey by mutableStateOf("")
    var apiUrl by mutableStateOf("https://api.deepseek.com/v1")
    var aiModel by mutableStateOf("deepseek-chat")
    var freeLimit by mutableStateOf("3")
    var paymentQr by mutableStateOf("")
    var users by mutableStateOf<List<UserRecord>>(emptyList())
    var codes by mutableStateOf<List<RechargeCode>>(emptyList())
    var payments by mutableStateOf<List<PaymentRecord>>(emptyList())
    var isLoading by mutableStateOf(false)
    var error by mutableStateOf("")
    var success by mutableStateOf("")

    fun loadAll() {
        isLoading = true
        viewModelScope.launch {
            try {
                val cfg = RetrofitClient.api.getAdminConfig()
                if (cfg.isSuccessful) cfg.body()?.let {
                    apiKey = it.apiKey; apiUrl = it.apiUrl; aiModel = it.aiModel
                    freeLimit = it.freeLimit.toString(); paymentQr = it.paymentQr
                }
                users = RetrofitClient.api.getUsers().body() ?: emptyList()
                codes = RetrofitClient.api.getCodes().body() ?: emptyList()
                payments = RetrofitClient.api.getPayments().body() ?: emptyList()
            } catch (_: Exception) { error = "加载失败" }
            isLoading = false
        }
    }

    fun saveConfig() {
        isLoading = true; error = ""; success = ""
        viewModelScope.launch {
            try {
                val r = RetrofitClient.api.updateConfig(AdminConfigRequest(
                    apiKey = apiKey, apiUrl = apiUrl,
                    aiModel = aiModel, freeLimit = freeLimit.toIntOrNull()
                ))
                if (r.isSuccessful) success = "保存成功" else error = "保存失败"
            } catch (_: Exception) { error = "网络错误" }
            isLoading = false
        }
    }

    fun uploadQr(base64Qr: String) {
        isLoading = true; error = ""; success = ""
        viewModelScope.launch {
            try {
                val r = RetrofitClient.api.uploadQr(mapOf("qrCode" to base64Qr))
                if (r.isSuccessful) {
                    paymentQr = base64Qr
                    success = "收款码已更新"
                } else error = "上传失败"
            } catch (_: Exception) { error = "网络错误" }
            isLoading = false
        }
    }

    fun genCode(amount: Double?, vipDays: Int?, count: Int) {
        isLoading = true; error = ""; success = ""
        viewModelScope.launch {
            try {
                val r = RetrofitClient.api.genCode(GenCodeRequest(amount, vipDays, count))
                if (r.isSuccessful) {
                    r.body()?.let { success = "生成成功: " + it.codes.joinToString() }
                    loadAll()
                } else error = "生成失败"
            } catch (_: Exception) { error = "网络错误" }
            isLoading = false
        }
    }
}
