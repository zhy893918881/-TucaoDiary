package com.ai.tucaodiary.data.remote

import com.google.gson.annotations.SerializedName

// ========== 认证 ==========
data class LoginRequest(val username: String, val password: String)
data class RegisterRequest(val username: String, val password: String)
data class AuthResponse(val token: String, val user: UserInfo)

// ========== 用户 ==========
data class UserInfo(
    val id: Int, val username: String, val role: String,
    @SerializedName("is_vip") val isVip: Boolean,
    @SerializedName("vip_expire") val vipExpire: Long = 0,
    val balance: Double = 0.0,
    @SerializedName("daily_count") val dailyCount: Int = 0
)
data class MeResponse(val id: Int, val username: String, val role: String,
    @SerializedName("is_vip") val isVip: Boolean,
    @SerializedName("vip_expire") val vipExpire: Long,
    val balance: Double, @SerializedName("daily_count") val dailyCount: Int)

// ========== 配置 ==========
data class ConfigResponse(@SerializedName("apiUrl") val apiUrl: String?,
    @SerializedName("aiModel") val aiModel: String?,
    @SerializedName("freeLimit") val freeLimit: Int)

data class AdminConfigResponse(val apiKey: String, val apiUrl: String,
    val aiModel: String, val freeLimit: Int, val paymentQr: String = "")

data class AdminConfigRequest(val apiKey: String? = null, val apiUrl: String? = null,
    val aiModel: String? = null, val freeLimit: Int? = null, val paymentQr: String? = null)

// ========== AI生成 ==========
data class GenerateRequest(val text: String, val style: String)

// ========== 支付 ==========
data class PaymentPlan(val id: Int, val name: String, val price: Double,
    @SerializedName("vip_days") val vipDays: Int, val desc: String, val tag: String? = null)

data class PaymentPlansResponse(val plans: List<PaymentPlan>,
    @SerializedName("hasQr") val hasQr: Boolean,
    @SerializedName("qrCode") val qrCode: String = "")

data class PayRequest(@SerializedName("plan_id") val planId: Int)
data class PayResponse(val success: Boolean, val message: String,
    @SerializedName("vip_days") val vipDays: Int, val expire: Long)

// ========== 管理员 ==========
data class RechargeCode(val id: Int, val code: String, val amount: Double,
    @SerializedName("vip_days") val vipDays: Int,
    @SerializedName("used_by") val usedBy: String = "",
    @SerializedName("used_at") val usedAt: Int = 0)
data class GenCodeRequest(val amount: Double? = null, val vipDays: Int? = null, val count: Int = 1)
data class GenCodeResponse(val codes: List<String>)
data class RechargeRequest(val code: String)

data class PaymentRecord(val id: Int, @SerializedName("user_id") val userId: Int,
    val username: String, @SerializedName("plan_name") val planName: String,
    val amount: Double, @SerializedName("vip_days") val vipDays: Int,
    val status: String, @SerializedName("created_at") val createdAt: Int)

data class UserRecord(val id: Int, val username: String, val role: String,
    @SerializedName("is_vip") val isVip: Int,
    @SerializedName("vip_expire") val vipExpire: Long,
    val balance: Double, @SerializedName("daily_count") val dailyCount: Int)

data class SimpleResponse(val success: Boolean, val message: String? = null)
