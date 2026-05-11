package com.ai.tucaodiary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.ai.tucaodiary.ui.navigation.NavGraph
import com.ai.tucaodiary.ui.theme.TucaoTheme
import com.ai.tucaodiary.ui.theme.Orange

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        try {
            setContent { TucaoTheme { NavGraph() } }
        } catch (e: Exception) {
            setContent {
                TucaoTheme {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("启动失败: ${e.message}", color = Orange)
                    }
                }
            }
        }
    }
}
