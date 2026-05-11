package com.ai.tucaodiary.data.remote

import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    // 认证
    @POST("auth/login") suspend fun login(@Body req: LoginRequest): Response<AuthResponse>
    @POST("auth/register") suspend fun register(@Body req: RegisterRequest): Response<AuthResponse>
    @GET("auth/me") suspend fun me(): Response<MeResponse>

    // 配置
    @GET("config") suspend fun getConfig(): Response<ConfigResponse>
    @GET("admin/config") suspend fun getAdminConfig(): Response<AdminConfigResponse>
    @PUT("admin/config") suspend fun updateConfig(@Body req: AdminConfigRequest): Response<SimpleResponse>

    // AI
    @POST("ai/generate") suspend fun generate(@Body req: GenerateRequest): Response<Map<String,Any>>

    // 支付
    @GET("payment/plans") suspend fun getPlans(): Response<PaymentPlansResponse>
    @POST("payment/pay") suspend fun pay(@Body req: PayRequest): Response<PayResponse>
    @PUT("admin/payment-qr") suspend fun uploadQr(@Body body: Map<String,String>): Response<SimpleResponse>

    // 充值码
    @POST("recharge") suspend fun recharge(@Body req: RechargeRequest): Response<SimpleResponse>
    @POST("admin/gen-code") suspend fun genCode(@Body req: GenCodeRequest): Response<GenCodeResponse>

    // 管理员
    @GET("admin/codes") suspend fun getCodes(): Response<List<RechargeCode>>
    @GET("admin/users") suspend fun getUsers(): Response<List<UserRecord>>
    @GET("admin/payments") suspend fun getPayments(): Response<List<PaymentRecord>>
    @GET("admin/payment-qr") suspend fun getPaymentQr(): Response<Map<String,String>>
}
