package com.ai.tucaodiary.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import sp
import com.ai.tucaodiary.ui.theme.*
import com.ai.tucaodiary.viewmodel.AuthViewModel
import com.ai.tucaodiary.viewmodel.HomeViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(
    authVm: AuthViewModel, homeVm: HomeViewModel,
    onAdmin: () -> Unit, onPayment: () -> Unit
) {
    val ctx = LocalContext.current
    val scroll = rememberScrollState()

    Column(Modifier.fillMaxSize().verticalScroll(scroll)) {
        // 顶栏
        Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(authVm.user?.username ?: "", style = MaterialTheme.typography.titleMedium, color = Orange)
                    if (authVm.isAdmin) {
                        Spacer(Modifier.width(6.dp))
                        Surface(shape = RoundedCornerShape(8.dp), color = Pink) { Text("管理", Modifier.padding(horizontal = 8.dp, vertical = 2.dp), style = MaterialTheme.typography.labelLarge.copy(fontSize = sp(10)), color = androidx.compose.ui.graphics.Color.White) }
                    }
                    if (authVm.isVip) {
                        Spacer(Modifier.width(4.dp))
                        Surface(shape = RoundedCornerShape(8.dp), color = Cyan) { Text("VIP", Modifier.padding(horizontal = 8.dp, vertical = 2.dp), style = MaterialTheme.typography.labelLarge.copy(fontSize = sp(10)), color = androidx.compose.ui.graphics.Color.White) }
                    }
                }
                if (authVm.isVip && authVm.vipExpire > 0) {
                    Text("到期 ${SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(Date(authVm.vipExpire * 1000))}", style = MaterialTheme.typography.bodyMedium)
                }
            }
            Row {
                if (authVm.isAdmin) IconButton(onAdmin) { Icon(Icons.Outlined.AdminPanelSettings, "管理", tint = Cyan) }
                IconButton(onPayment) { Icon(Icons.Filled.Wallet, "充值", tint = Cyan) }
                IconButton({ authVm.logout() }) { Icon(Icons.Filled.Logout, "退出", tint = TextSub) }
            }
        }

        // 余额+次数
        Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Surface(color = CardBg, shape = RoundedCornerShape(12.dp)) {
                Text("💰 ¥${String.format("%.2f", authVm.balance)}", Modifier.padding(horizontal = 14.dp, vertical = 8.dp), style = MaterialTheme.typography.labelLarge, color = Orange)
            }
            if (authVm.isVip) {
                Surface(color = Cyan.copy(alpha = 0.15f), shape = RoundedCornerShape(12.dp)) {
                    Text("✨ 无限吐槽", Modifier.padding(horizontal = 14.dp, vertical = 8.dp), style = MaterialTheme.typography.labelLarge, color = Cyan)
                }
            } else {
                Surface(color = Orange.copy(alpha = 0.15f), shape = RoundedCornerShape(12.dp)) {
                    Text("⚡ 今日剩余 ${((authVm.freeLimit) - authVm.dailyCount).coerceAtLeast(0)} 次", Modifier.padding(horizontal = 14.dp, vertical = 8.dp), style = MaterialTheme.typography.labelLarge, color = Orange)
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // 标题
        Text("🔥 吐槽日记", Modifier.padding(horizontal = 16.dp), style = MaterialTheme.typography.headlineLarge, color = Orange)
        Text("把你的怨气变成段子", Modifier.padding(horizontal = 16.dp), style = MaterialTheme.typography.bodyMedium)

        Spacer(Modifier.height(16.dp))

        // 输入框
        Surface(Modifier.fillMaxWidth().padding(horizontal = 16.dp).heightIn(min = 130.dp), shape = RoundedCornerShape(14.dp), color = CardBg, border = BorderStroke(1.5.dp, if (homeVm.rawText.isNotBlank()) Orange else CardStroke)) {
            TextField(
                homeVm.rawText, { homeVm.rawText = it },
                Modifier.fillMaxSize().padding(12.dp),
                placeholder = { Text("今天发生了什么糟心事…", style = MaterialTheme.typography.bodyMedium) },
                colors = TextFieldDefaults.colors(unfocusedContainerColor = CardBg, focusedContainerColor = CardBg, focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent, unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent, cursorColor = Orange),
                textStyle = MaterialTheme.typography.bodyLarge
            )
        }

        Spacer(Modifier.height(16.dp))

        // 风格选择
        Text("选择吐槽风格", Modifier.padding(horizontal = 16.dp), style = MaterialTheme.typography.bodyMedium, color = TextSub)
        Spacer(Modifier.height(8.dp))
        Column(Modifier.padding(horizontal = 16.dp)) {
            val styleDescs = mapOf("阴阳怪气" to "表面客气扎心致命", "暴躁老哥" to "火力全开拒绝憋屈", "梗王附体" to "梗多到溢出屏幕", "文艺丧" to "丧得很有文学感", "自嘲大师" to "惨得好笑说得就是我")
            homeVm.styles.forEach { (name, emoji) ->
                val sel = homeVm.selectedStyle == name
                Surface(Modifier.fillMaxWidth().clickable { homeVm.selectedStyle = name }.padding(2.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = if (sel) Orange.copy(alpha = 0.15f) else CardBg,
                    border = if (sel) BorderStroke(1.dp, Orange) else null
                ) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(emoji, Modifier.width(36.dp), style = MaterialTheme.typography.headlineMedium)
                        Column {
                            Text(name, fontWeight = FontWeight.Bold)
                            Text(styleDescs[name] ?: "", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // 生成按钮
        Button(
            { homeVm.generate { } },
            Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Orange),
            enabled = !homeVm.isGenerating
        ) {
            if (homeVm.isGenerating) {
                CircularProgressIndicator(Modifier.size(22.dp), color = androidx.compose.ui.graphics.Color.White, strokeWidth = 2.dp)
                Spacer(Modifier.width(8.dp))
                Text("AI正在吐槽…")
            } else Text("✨ 生成吐槽文案", style = MaterialTheme.typography.labelLarge)
        }

        // 错误
        if (homeVm.error.isNotBlank()) {
            Surface(Modifier.padding(horizontal = 16.dp, vertical = 8.dp).fillMaxWidth(), color = Pink.copy(alpha = 0.1f), shape = RoundedCornerShape(12.dp)) {
                Text("⚠️ ${homeVm.error}", Modifier.padding(12.dp), color = Pink)
            }
        }

        // 结果
        if (homeVm.generatedText.isNotBlank()) {
            Spacer(Modifier.height(16.dp))
            Surface(Modifier.padding(horizontal = 16.dp).fillMaxWidth(), shape = RoundedCornerShape(14.dp), color = CardBg) {
                Column(Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🎉", style = MaterialTheme.typography.headlineMedium)
                        Spacer(Modifier.width(8.dp))
                        Text("吐槽生成完毕", color = Orange, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(homeVm.generatedText, style = MaterialTheme.typography.bodyLarge)
                    Spacer(Modifier.height(12.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        OutlinedButton({
                            val clip = ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clip.setPrimaryClip(ClipData.newPlainText("tucao", homeVm.generatedText))
                        }, shape = RoundedCornerShape(10.dp), border = BorderStroke(1.dp, Orange)) { Icon(Icons.Filled.ContentCopy, "", tint = Orange); Spacer(Modifier.width(6.dp)); Text("复制", color = Orange) }
                        Button({
                            val share = Intent(Intent.ACTION_SEND).apply { type = "text/plain"; putExtra(Intent.EXTRA_TEXT, "${homeVm.generatedText}\n\n—— 来自「吐槽日记」") }
                            ctx.startActivity(Intent.createChooser(share, "分享"))
                        }, shape = RoundedCornerShape(10.dp), colors = ButtonDefaults.buttonColors(containerColor = Orange)) { Icon(Icons.Filled.Share, ""); Spacer(Modifier.width(6.dp)); Text("分享") }
                    }
                }
            }
        }

        Spacer(Modifier.height(32.dp))
    }
}
