package com.ai.tucaodiary.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "diary_entries")
data class DiaryEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    /** 用户原始吐槽内容 */
    val rawText: String,

    /** AI 生成的吐槽文案 */
    val generatedText: String,

    /** 吐槽风格 */
    val style: String,

    /** 创建时间戳 */
    val createdAt: Long = System.currentTimeMillis(),

    /** 是否已分享 */
    val isShared: Boolean = false
)
