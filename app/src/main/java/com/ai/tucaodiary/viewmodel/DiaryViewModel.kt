package com.ai.tucaodiary.viewmodel

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ai.tucaodiary.data.local.AppDatabase
import com.ai.tucaodiary.data.local.entity.DiaryEntry
import com.ai.tucaodiary.data.repository.DiaryRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class DiaryViewModel(
    private val database: AppDatabase,
    private val context: Context
) : ViewModel() {

    private val repository = DiaryRepository(database.diaryDao())

    // === UI State ===
    private val _rawText = MutableStateFlow("")
    val rawText: StateFlow<String> = _rawText

    private val _selectedStyle = MutableStateFlow("阴阳怪气")
    val selectedStyle: StateFlow<String> = _selectedStyle

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating

    private val _generatedText = MutableStateFlow<String?>(null)
    val generatedText: StateFlow<String?> = _generatedText

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _todayCount = MutableStateFlow(0)
    val todayCount: StateFlow<Int> = _todayCount

    val allEntries: Flow<List<DiaryEntry>> = repository.getAllEntries()

    // === Settings ===
    val apiKey: StateFlow<String> = context.dataStore.data
        .map { it[API_KEY_KEY] ?: "" }
        .stateIn(viewModelScope, SharingStarted.Lazily, "")

    val apiUrl: StateFlow<String> = context.dataStore.data
        .map { it[API_URL_KEY] ?: "https://api.openai.com/v1" }
        .stateIn(viewModelScope, SharingStarted.Lazily, "https://api.openai.com/v1")

    val aiModel: StateFlow<String> = context.dataStore.data
        .map { it[AI_MODEL_KEY] ?: "gpt-3.5-turbo" }
        .stateIn(viewModelScope, SharingStarted.Lazily, "gpt-3.5-turbo")

    val isPremium: StateFlow<Boolean> = context.dataStore.data
        .map { it[PREMIUM_KEY] ?: false }
        .stateIn(viewModelScope, SharingStarted.Lazily, false)

    // === Freemium ===
    val canGenerate: StateFlow<Boolean> = combine(_todayCount, isPremium) { count, premium ->
        premium || count < 3
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), true)

    val freeTimesLeft: StateFlow<Int> = combine(_todayCount, isPremium) { count, premium ->
        if (premium) Int.MAX_VALUE else (3 - count).coerceAtLeast(0)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 3)

    init {
        refreshTodayCount()
    }

    fun setRawText(text: String) { _rawText.value = text }
    fun setSelectedStyle(style: String) { _selectedStyle.value = style }
    fun clearError() { _error.value = null }
    fun clearGeneratedText() { _generatedText.value = null }

    fun refreshTodayCount() {
        viewModelScope.launch {
            _todayCount.value = repository.getTodayCount()
        }
    }

    /** 生成吐槽文案 */
    fun generateTucao() {
        val text = _rawText.value.trim()
        if (text.isEmpty()) {
            _error.value = "说点什么再让我帮你改喵～"
            return
        }

        val key = apiKey.value
        if (key.isEmpty()) {
            _error.value = "先去设置里填一下 AI API Key 喵～"
            return
        }

        if (!canGenerate.value) {
            _error.value = "今天免费次数用完了喵！明天再来～"
            return
        }

        viewModelScope.launch {
            _isGenerating.value = true
            _error.value = null
            _generatedText.value = null

            val result = repository.generateTucao(
                rawText = text,
                style = _selectedStyle.value,
                baseUrl = apiUrl.value,
                apiKey = key,
                model = aiModel.value
            )

            result.fold(
                onSuccess = { generated ->
                    _generatedText.value = generated
                    // 自动保存到日记
                    repository.saveEntry(text, generated, _selectedStyle.value)
                    refreshTodayCount()
                },
                onFailure = { e ->
                    _error.value = "生成失败：${e.message ?: "未知错误"}"
                }
            )

            _isGenerating.value = false
        }
    }

    fun getEntryById(id: Long, callback: (DiaryEntry?) -> Unit) {
        viewModelScope.launch {
            callback(database.diaryDao().getEntryById(id))
        }
    }

    fun markShared(id: Long) {
        viewModelScope.launch {
            repository.markShared(id)
        }
    }

    fun deleteEntry(entry: DiaryEntry) {
        viewModelScope.launch {
            repository.deleteEntry(entry)
        }
    }

    /** 更新设置 */
    fun updateApiKey(key: String) {
        viewModelScope.launch {
            context.dataStore.edit { it[API_KEY_KEY] = key }
        }
    }

    fun updateApiUrl(url: String) {
        viewModelScope.launch {
            context.dataStore.edit { it[API_URL_KEY] = url }
        }
    }

    fun updateAiModel(model: String) {
        viewModelScope.launch {
            context.dataStore.edit { it[AI_MODEL_KEY] = model }
        }
    }

    fun setPremium(premium: Boolean) {
        viewModelScope.launch {
            context.dataStore.edit { it[PREMIUM_KEY] = premium }
        }
    }

    companion object {
        private val API_KEY_KEY = stringPreferencesKey("api_key")
        private val API_URL_KEY = stringPreferencesKey("api_url")
        private val AI_MODEL_KEY = stringPreferencesKey("ai_model")
        private val PREMIUM_KEY = booleanPreferencesKey("is_premium")

        fun factory(database: AppDatabase, context: Context): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return DiaryViewModel(database, context.applicationContext) as T
                }
            }
        }
    }
}
