package com.ai.tucaodiary.data.local

import android.content.Context
import android.content.SharedPreferences

object TokenManager {
    private lateinit var prefs: SharedPreferences
    fun init(ctx: Context) { prefs = ctx.getSharedPreferences("tucao", Context.MODE_PRIVATE) }
    var token: String?
        get() = prefs.getString("token", null)
        set(v) = prefs.edit().putString("token", v).apply()
    var username: String?
        get() = prefs.getString("username", null)
        set(v) = prefs.edit().putString("username", v).apply()
    var role: String?
        get() = prefs.getString("role", null)
        set(v) = prefs.edit().putString("role", v).apply()
    var isVip: Boolean
        get() = prefs.getBoolean("isVip", false)
        set(v) = prefs.edit().putBoolean("isVip", v).apply()
    var vipExpire: Long
        get() = prefs.getLong("vipExpire", 0L)
        set(v) = prefs.edit().putLong("vipExpire", v).apply()
    var balance: Double
        get() = prefs.getString("balance", "0")?.toDoubleOrNull() ?: 0.0
        set(v) = prefs.edit().putString("balance", v.toString()).apply()
    fun clear() { prefs.edit().clear().apply() }
    fun saveUser(u: com.ai.tucaodiary.data.remote.UserInfo) {
        username = u.username; role = u.role
        isVip = u.isVip; vipExpire = u.vipExpire; balance = u.balance
    }
}
