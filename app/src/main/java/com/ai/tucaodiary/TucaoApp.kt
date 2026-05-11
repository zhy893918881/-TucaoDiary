package com.ai.tucaodiary

import android.app.Application
import com.ai.tucaodiary.data.local.TokenManager
import com.ai.tucaodiary.data.remote.RetrofitClient

class TucaoApp : Application() {
    override fun onCreate() {
        super.onCreate()
        TokenManager.init(this)
        TokenManager.token?.let {
            RetrofitClient.token = it
        }
    }
}
