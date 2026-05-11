package com.ai.tucaodiary.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ai.tucaodiary.ui.theme.*

@Composable
fun SettingsScreen(
    apiKey: String,
    apiUrl: String,
    aiModel: String,
    isPremium: Boolean,
    onApiKeyChange: (String) -> Unit,
    onApiUrlChange: (String) -> Unit,
    onAiModelChange: (String) -> Unit
) {
    var showKey by remember { mutableStateOf(false) }
    var localApiKey by remember { mutableStateOf(apiKey) }
    var localApiUrl by remember { mutableStateOf(apiUrl) }
    var localAiModel by remember { mutableStateOf(aiModel) }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Text(
            text = "⚙️ 设置",
            style = MaterialTheme.typography.headlineMedium,
            color = AccentOrange,
            modifier = Modifier.padding(top = 40.dp, bottom = 24.dp)
        )

        // === AI 配置 ===
        Text(
            "🤖 AI 配置",
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))

        // API 地址
        OutlinedTextField(
            value = localApiUrl,
            onValueChange = {
                localApiUrl = it
                onApiUrlChange(it)
            },
            label = { Text("API 地址") },
            placeholder = { Text("https://api.openai.com/v1") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Filled.Link, contentDescription = null) },
            singleLine = true,
            colors = darkTextFieldColors(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 模型名称
        OutlinedTextField(
            value = localAiModel,
            onValueChange = {
                localAiModel = it
                onAiModelChange(it)
            },
            label = { Text("模型名称") },
            placeholder = { Text("gpt-3.5-turbo / moonshot-v1-8k / glm-4") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Filled.Memory, contentDescription = null) },
            singleLine = true,
            colors = darkTextFieldColors(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // API Key
        OutlinedTextField(
            value = localApiKey,
            onValueChange = {
                localApiKey = it
                onApiKeyChange(it)
            },
            label = { Text("API Key") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Filled.Key, contentDescription = null) },
            trailingIcon = {
                IconButton(onClick = { showKey = !showKey }) {
                    Icon(
                        if (showKey) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        contentDescription = null
                    )
                }
            },
            singleLine = true,
            visualTransformation = if (showKey) androidx.compose.ui.text.input.VisualTransformation.None
                else PasswordVisualTransformation(),
            colors = darkTextFieldColors(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // === 支持哪些 API ===
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = DarkCard)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "💡 支持这些 API（兼容 OpenAI 格式）：",
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                listOf(
                    "OpenAI → https://api.openai.com/v1",
                    "Moonshot(Kimi) → https://api.moonshot.cn/v1",
                    "DeepSeek → https://api.deepseek.com/v1",
                    "智谱AI(GLM) → https://open.bigmodel.cn/api/paas/v4",
                    "百川 → https://api.baichuan-ai.com/v1",
                    "通义千问 → https://dashscope.aliyuncs.com/compatible-mode/v1"
                ).forEach { text ->
                    Text(
                        text = "  •  $text",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 20.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // === 当前状态 ===
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isPremium) AccentCyan.copy(alpha = 0.15f) else DarkCard
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    if (isPremium) Icons.Filled.Star else Icons.Filled.Info,
                    contentDescription = null,
                    tint = if (isPremium) AccentCyan else TextSecondary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = if (isPremium) "✨ 会员版 · 无限吐槽" else "🆓 免费版 · 每日3次",
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (isPremium) AccentCyan else TextPrimary,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = if (isPremium) "已解锁所有功能" else "升级会员解锁无限次数",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
fun darkTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = TextPrimary,
    unfocusedTextColor = TextPrimary,
    cursorColor = AccentOrange,
    focusedBorderColor = AccentOrange,
    unfocusedBorderColor = TextSecondary.copy(alpha = 0.3f),
    focusedLabelColor = AccentOrange,
    unfocusedLabelColor = TextSecondary,
    focusedContainerColor = DarkCard,
    unfocusedContainerColor = DarkCard
)
