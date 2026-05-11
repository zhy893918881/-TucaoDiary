package com.ai.tucaodiary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.ai.tucaodiary.ui.navigation.NavGraph
import com.ai.tucaodiary.ui.theme.TucaoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { TucaoTheme { NavGraph() } }
    }
}
