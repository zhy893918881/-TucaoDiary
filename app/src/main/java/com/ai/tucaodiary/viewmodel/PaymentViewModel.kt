package com.ai.tucaodiary.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ai.tucaodiary.data.remote.*
import kotlinx.coroutines.launch

class PaymentViewModel : ViewModel() {
    var plans by mutableStateOf<List<PaymentPlan>>(emptyList())
    var hasQr by mutableStateOf(false)
    var qrCode by mutableStateOf("")
    var selectedPlan by mutableStateOf<PaymentPlan?>(null)
    var showQrDialog by mutableStateOf(false)
    var isLoading by mutableStateOf(false)
    var error by mutableStateOf("")
    var paySuccess by mutableStateOf("")

    fun loadPlans() {
        isLoading = true
        viewModelScope.launch {
            try {
                val r = RetrofitClient.api.getPlans()
                if (r.isSuccessful) {
                    r.body()?.let { plans = it.plans; hasQr = it.hasQr; qrCode = it.qrCode }
                }
            } catch (_: Exception) { error = "加载失败" }
            isLoading = false
        }
    }

    fun selectPlan(plan: PaymentPlan) {
        selectedPlan = plan
        showQrDialog = true
        error = ""
    }

    fun confirmPayment(onSuccess: (String) -> Unit) {
        val plan = selectedPlan ?: return
        isLoading = true; error = ""
        viewModelScope.launch {
            try {
                val r = RetrofitClient.api.pay(PayRequest(plan.id))
                if (r.isSuccessful) {
                    r.body()?.let {
                        paySuccess = it.message
                        showQrDialog = false
                        onSuccess(it.message)
                    }
                } else {
                    error = "支付确认失败"
                }
            } catch (_: Exception) { error = "网络错误" }
            isLoading = false
        }
    }
}
