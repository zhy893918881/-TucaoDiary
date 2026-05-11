package com.ai.tucaodiary.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ai.tucaodiary.data.remote.*
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    var rawText by mutableStateOf("")
    var selectedStyle by mutableStateOf("阴阳怪气")
    var generatedText by mutableStateOf("")
    var isGenerating by mutableStateOf(false)
    var error by mutableStateOf("")

    val styles = listOf(
        "阴阳怪气" to "🙂", "暴躁老哥" to "🤬", "梗王附体" to "😂",
        "文艺丧" to "🥀", "自嘲大师" to "🤡"
    )

    fun generate(onDone: () -> Unit = {}) {
        if (rawText.isBlank()) { error = "说点什么再吐槽喵~"; return }
        isGenerating = true; error = ""; generatedText = ""
        viewModelScope.launch {
            try {
                val r = RetrofitClient.api.generate(GenerateRequest(rawText, selectedStyle))
                if (r.isSuccessful) {
                    val content = r.body()?.get("content") as? String ?: ""
                    generatedText = content
                    if (content.isBlank()) error = "AI返回空内容"
                } else {
                    error = try { com.google.gson.Gson().fromJson(r.errorBody()?.string(), Map::class.java)["error"] as? String ?: "生成失败" } catch (_: Exception) { "生成失败" }
                }
            } catch (_: Exception) { error = "网络错误" }
            isGenerating = false
            if (generatedText.isNotBlank()) onDone()
        }
    }
}
