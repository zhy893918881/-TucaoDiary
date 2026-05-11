package com.ai.tucaodiary.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ai.tucaodiary.data.local.entity.DiaryEntry
import com.ai.tucaodiary.ui.components.DiaryCard
import com.ai.tucaodiary.ui.components.ShareImageGenerator
import com.ai.tucaodiary.ui.theme.*
import com.ai.tucaodiary.viewmodel.DiaryViewModel

@Composable
fun TimelineScreen(
    viewModel: DiaryViewModel,
    onNavigateToResult: (Long) -> Unit
) {
    val entries by viewModel.allEntries.collectAsState(initial = emptyList())
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        // Header
        Text(
            text = "📖 我的吐槽日记",
            style = MaterialTheme.typography.headlineMedium,
            color = AccentOrange,
            modifier = Modifier.padding(start = 16.dp, top = 40.dp, end = 16.dp, bottom = 8.dp)
        )
        Text(
            text = "已吐槽 ${entries.size} 条，继续加油",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 0.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (entries.isEmpty()) {
            // 空状态
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "📝", fontSize = 64.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "还没有吐槽日记\n快去吐槽吧！",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp, horizontal = 0.dp)
            ) {
                items(entries, key = { it.id }) { entry ->
                    DiaryCard(
                        entry = entry,
                        onShare = {
                            ShareImageGenerator.generateAndShare(
                                context,
                                entry.generatedText,
                                entry.style
                            )
                            viewModel.markShared(entry.id)
                        },
                        onDelete = { viewModel.deleteEntry(entry) },
                        onClick = { onNavigateToResult(entry.id) }
                    )
                }
                // 底部间距
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

// placeholders
