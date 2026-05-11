package com.ai.tucaodiary.data.repository

import com.ai.tucaodiary.data.local.DiaryDao
import com.ai.tucaodiary.data.local.entity.DiaryEntry
import com.ai.tucaodiary.data.remote.*
import kotlinx.coroutines.flow.Flow
import java.util.*

class DiaryRepository(
    private val diaryDao: DiaryDao
) {
    fun getAllEntries(): Flow<List<DiaryEntry>> = diaryDao.getAllEntries()

    suspend fun getTodayCount(): Int {
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return diaryDao.getTodayCount(cal.timeInMillis)
    }

    suspend fun generateTucao(
        rawText: String,
        style: String,
        baseUrl: String,
        apiKey: String,
        model: String
    ): Result<String> {
        return try {
            val service = RetrofitClient.getApiService(baseUrl, apiKey)
            val systemPrompt = StylePrompts.buildSystemPrompt(style, rawText)

            val request = ChatRequest(
                model = model,
                messages = listOf(
                    ChatMessage("system", "你是一个专业的吐槽文案生成助手。"),
                    ChatMessage("user", systemPrompt)
                )
            )

            val response = service.generateText(request)

            val content = response.choices?.firstOrNull()?.message?.content
            if (content != null) {
                Result.success(content)
            } else {
                Result.failure(Exception("AI 返回了空内容，请重试"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveEntry(rawText: String, generatedText: String, style: String): Long {
        return diaryDao.insertEntry(
            DiaryEntry(
                rawText = rawText,
                generatedText = generatedText,
                style = style
            )
        )
    }

    suspend fun deleteEntry(entry: DiaryEntry) {
        diaryDao.deleteEntry(entry)
    }

    suspend fun markShared(id: Long) {
        val entry = diaryDao.getEntryById(id) ?: return
        diaryDao.updateEntry(entry.copy(isShared = true))
    }
}
