package com.ai.tucaodiary.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ai.tucaodiary.ui.theme.*
import com.ai.tucaodiary.viewmodel.AdminViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(vm: AdminViewModel, onBack: () -> Unit) {
    var tabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("⚙️配置", "👥用户", "💳支付", "🎫充值码")
    LaunchedEffect(Unit) { vm.loadAll() }

    Column(Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onBack) { Icon(Icons.Filled.ArrowBack, "返回", tint = TextSub) }
            Text("管理面板", style = MaterialTheme.typography.headlineMedium, color = Orange)
        }

        TabRow(selectedTabIndex = tabIndex, containerColor = CardBg, contentColor = Orange) {
            tabs.forEachIndexed { i, t ->
                Tab(i == tabIndex, { tabIndex = i }, text = { Text(t, fontSize = 13.sp) })
            }
        }

        if (vm.isLoading) { Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Orange) } }
        else {
            when (tabIndex) {
                0 -> ConfigTab(vm)
                1 -> UsersTab(vm)
                2 -> PaymentsTab(vm)
                3 -> CodesTab(vm)
            }
        }
    }
}

@Composable
fun ConfigTab(vm: AdminViewModel) {
    Column(Modifier.verticalScroll(rememberScrollState()).padding(16.dp)) {
        if (vm.success.isNotBlank()) { Surface(Modifier.fillMaxWidth().padding(bottom = 8.dp), color = Cyan.copy(alpha = 0.1f), shape = RoundedCornerShape(10.dp)) { Text("✅ ${vm.success}", Modifier.padding(10.dp), color = Cyan) } }
        if (vm.error.isNotBlank()) { Surface(Modifier.fillMaxWidth().padding(bottom = 8.dp), color = Pink.copy(alpha = 0.1f), shape = RoundedCornerShape(10.dp)) { Text("❌ ${vm.error}", Modifier.padding(10.dp), color = Pink) } }

        Text("AI 配置", fontWeight = FontWeight.Bold, color = Orange)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(vm.apiKey, { vm.apiKey = it }, Modifier.fillMaxWidth(), label = { Text("API Key") }, singleLine = true, visualTransformation = PasswordVisualTransformation(), colors = fieldColors())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(vm.apiUrl, { vm.apiUrl = it }, Modifier.fillMaxWidth(), label = { Text("API 地址") }, singleLine = true, colors = fieldColors())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(vm.aiModel, { vm.aiModel = it }, Modifier.fillMaxWidth(), label = { Text("模型") }, singleLine = true, colors = fieldColors())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(vm.freeLimit, { vm.freeLimit = it }, Modifier.fillMaxWidth(), label = { Text("每日免费次数") }, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), colors = fieldColors())
        Spacer(Modifier.height(12.dp))
        Button({ vm.saveConfig() }, Modifier.fillMaxWidth().height(48.dp), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = Orange)) { Text("💾 保存配置") }

        Spacer(Modifier.height(24.dp))
        Text("收款码设置", fontWeight = FontWeight.Bold, color = Orange)
        Spacer(Modifier.height(8.dp))
        Text("粘贴收款码的base64或URL地址", style = MaterialTheme.typography.bodyMedium, color = TextSub)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(vm.paymentQr, { vm.paymentQr = it }, Modifier.fillMaxWidth().heightIn(min = 60.dp), label = { Text("收款码(URL/Base64)") }, colors = fieldColors())
        Spacer(Modifier.height(8.dp))
        Button({ vm.uploadQr(vm.paymentQr) }, Modifier.fillMaxWidth().height(44.dp), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = Cyan)) { Text("📤 上传收款码") }

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
fun UsersTab(vm: AdminViewModel) {
    LazyColumn(Modifier.padding(horizontal = 16.dp)) {
        item {
            Text("用户列表 (${vm.users.size})", style = MaterialTheme.typography.titleMedium, color = Orange)
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth().background(CardBg, RoundedCornerShape(8.dp)).padding(10.dp)) {
                Text("用户", Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text("角色", Modifier.width(50.dp), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text("VIP", Modifier.width(40.dp), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text("余额", Modifier.width(50.dp), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text("次数", Modifier.width(40.dp), fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
        items(vm.users) { u ->
            val isVip = u.isVip == 1 || u.role == "admin"
            val expireStr = if (u.vipExpire > 0) SimpleDateFormat("MM-dd", Locale.CHINA).format(Date(u.vipExpire * 1000)) else "-"
            Row(Modifier.fillMaxWidth().border(BorderStroke(0.5.dp, CardStroke)).padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(u.username, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    if (isVip && u.vipExpire > 0) Text("到期: $expireStr", fontSize = 10.sp, color = TextSub)
                }
                Surface(color = if (u.role == "admin") Pink.copy(alpha = 0.15f) else CardBg, shape = RoundedCornerShape(4.dp)) {
                    Text(u.role, Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 11.sp, color = if (u.role == "admin") Pink else TextSub)
                }
                Text(if (isVip) "⭐" else "-", Modifier.width(40.dp), fontSize = 12.sp, color = if (isVip) Cyan else TextSub)
                Text("¥${String.format("%.1f", u.balance)}", Modifier.width(50.dp), fontSize = 12.sp, color = Orange)
                Text("${u.dailyCount}", Modifier.width(40.dp), fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun PaymentsTab(vm: AdminViewModel) {
    LazyColumn(Modifier.padding(horizontal = 16.dp)) {
        item {
            Text("支付记录 (${vm.payments.size})", style = MaterialTheme.typography.titleMedium, color = Orange)
            Spacer(Modifier.height(8.dp))
        }
        items(vm.payments) { p ->
            Card(Modifier.fillMaxWidth().padding(vertical = 4.dp), shape = RoundedCornerShape(10.dp), colors = CardDefaults.cardColors(containerColor = CardBg)) {
                Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text(p.username, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(p.planName, style = MaterialTheme.typography.bodyMedium, fontSize = 12.sp)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("¥${p.amount}", color = Orange, fontWeight = FontWeight.Bold)
                        Text("${p.vipDays}天", fontSize = 12.sp, color = Cyan)
                    }
                }
            }
        }
    }
}

@Composable
fun CodesTab(vm: AdminViewModel) {
    var vipDays by remember { mutableStateOf("30") }
    var amount by remember { mutableStateOf("") }
    var count by remember { mutableStateOf("1") }

    Column(Modifier.verticalScroll(rememberScrollState()).padding(16.dp)) {
        Text("生成充值码", fontWeight = FontWeight.Bold, color = Orange)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(amount, { amount = it }, Modifier.fillMaxWidth(), label = { Text("金额 (元)") }, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), colors = fieldColors())
        Spacer(Modifier.height(4.dp))
        OutlinedTextField(vipDays, { vipDays = it }, Modifier.fillMaxWidth(), label = { Text("VIP 天数") }, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), colors = fieldColors())
        Spacer(Modifier.height(4.dp))
        OutlinedTextField(count, { count = it }, Modifier.fillMaxWidth(), label = { Text("数量") }, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), colors = fieldColors())
        Spacer(Modifier.height(8.dp))
        Button({
            vm.genCode(amount.toDoubleOrNull(), vipDays.toIntOrNull(), count.toIntOrNull() ?: 1)
        }, Modifier.fillMaxWidth().height(44.dp), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = Orange)) { Text("🎫 生成") }

        Spacer(Modifier.height(20.dp))
        Text("充值码列表", fontWeight = FontWeight.Bold, color = Orange)
        Spacer(Modifier.height(8.dp))
        vm.codes.forEach { c ->
            Surface(Modifier.fillMaxWidth().padding(vertical = 3.dp), shape = RoundedCornerShape(8.dp), color = CardBg) {
                Row(Modifier.padding(10.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(c.code, fontSize = 12.sp, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace, color = Orange)
                    Text(if (c.usedAt > 0) "已用" else if (c.vipDays > 0) "${c.vipDays}天" else "¥${c.amount}", fontSize = 11.sp, color = if (c.usedAt > 0) TextSub else Cyan)
                }
            }
        }
    }
}
