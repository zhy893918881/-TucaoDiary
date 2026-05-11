package com.ai.tucaodiary.ui.components

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

/**
 * 将吐槽文案渲染成精美图片，一键分享
 */
object ShareImageGenerator {

    fun generateAndShare(context: Context, text: String, style: String) {
        val bitmap = createShareBitmap(text, style)
        val file = File(context.cacheDir, "tucao_share.jpg")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 95, out)
        }

        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/jpeg"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_TEXT, text)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(shareIntent, "分享吐槽日记"))
    }

    private fun createShareBitmap(text: String, style: String): Bitmap {
        val width = 1080
        val padding = 60f
        val cardPadding = 50f

        // 计算文字高度
        val textPaint = Paint().apply {
            isAntiAlias = true
            color = 0xFFEAEAEA.toInt()
            textSize = 42f
            typeface = Typeface.DEFAULT
        }

        // 简单的文字换行计算
        val lines = wrapText(text, textPaint, width - padding * 2 - cardPadding * 2)
        val lineHeight = 58f
        val textHeight = lines.size * lineHeight

        val headerHeight = 160f
        val footerHeight = 100f
        val totalHeight = headerHeight + textHeight + footerHeight + cardPadding * 3

        val bitmap = Bitmap.createBitmap(width, totalHeight.toInt(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // 背景
        val bgPaint = Paint().apply { color = 0xFF1A1A2E.toInt() }
        canvas.drawRect(0f, 0f, width.toFloat(), totalHeight, bgPaint)

        // 卡片背景
        val cardPaint = Paint().apply {
            color = 0xFF16213E.toInt()
            isAntiAlias = true
        }
        canvas.drawRoundRect(
            padding, padding,
            width - padding, totalHeight - padding,
            30f, 30f, cardPaint
        )

        var y = padding + cardPadding

        // Header - 风格标签
        val tagPaint = Paint().apply {
            color = 0xFFFF6B35.toInt()
            textSize = 28f
            typeface = Typeface.DEFAULT_BOLD
            isAntiAlias = true
        }
        canvas.drawText("#$style 吐槽日记", padding + cardPadding, y + 30f, tagPaint)

        y += 80f

        // 分隔线
        val linePaint = Paint().apply {
            color = 0x33FF6B35.toInt()
            strokeWidth = 2f
        }
        canvas.drawLine(
            padding + cardPadding, y,
            width - padding - cardPadding, y,
            linePaint
        )

        y += 30f

        // 正文
        val bodyPaint = Paint().apply {
            isAntiAlias = true
            color = 0xFFEAEAEA.toInt()
            textSize = 40f
            typeface = Typeface.DEFAULT
        }
        for (line in lines) {
            canvas.drawText(line, padding + cardPadding, y, bodyPaint)
            y += lineHeight
        }

        y += cardPadding

        // Footer - 水印
        val footerPaint = Paint().apply {
            color = 0x66999999.toInt()
            textSize = 24f
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText(
            "—— 来自「吐槽日记」App",
            width / 2f,
            y + 30f,
            footerPaint
        )

        return bitmap
    }

    private fun wrapText(text: String, paint: Paint, maxWidth: Float): List<String> {
        val lines = mutableListOf<String>()
        val paragraphs = text.split("\n")

        for (paragraph in paragraphs) {
            if (paragraph.isEmpty()) {
                lines.add("")
                continue
            }
            var current = ""
            for (char in paragraph) {
                val testWidth = paint.measureText(current + char)
                if (testWidth > maxWidth && current.isNotEmpty()) {
                    lines.add(current)
                    current = char.toString()
                } else {
                    current += char
                }
            }
            if (current.isNotEmpty()) lines.add(current)
        }

        return lines
    }
}
