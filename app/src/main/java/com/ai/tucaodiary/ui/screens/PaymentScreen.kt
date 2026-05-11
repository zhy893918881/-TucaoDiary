package com.ai.tucaodiary.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.ai.tucaodiary.ui.theme.*
import com.ai.tucaodiary.viewmodel.PaymentViewModel

@Composable
fun PaymentScreen(vm: PaymentViewModel, onBack: () -> Unit) {
    LaunchedEffect(Unit) { vm.loadPlans() }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        // 顶部
        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onBack) { Icon(Icons.Filled.ArrowBack, "返回", tint = TextSub) }
            Text("💎 充值会员", style = MaterialTheme.typography.headlineMedium, color = Orange)
        }

        Text("选择套餐，获取无限吐槽！", Modifier.padding(horizontal = 16.dp), style = MaterialTheme.typography.bodyMedium)

        Spacer(Modifier.height(16.dp))

        // 套餐列表
        Column(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            vm.plans.forEach { plan ->
                val tagColor = when {
                    plan.tag?.contains("超值") == true -> Pink
                    plan.tag?.contains("推荐") == true -> Cyan
                    plan.tag?.contains("热门") == true -> Orange
                    else -> TextSub
                }
                Card(
                    Modifier.fillMaxWidth().clickable { vm.selectPlan(plan) },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    border = BorderStroke(1.dp, CardStroke)
                ) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(plan.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                plan.tag?.let {
                                    Spacer(Modifier.width(8.dp))
                                    Surface(color = tagColor.copy(alpha = 0.15f), shape = RoundedCornerShape(6.dp)) {
                                        Text(it, Modifier.padding(horizontal = 8.dp, vertical = 2.dp), color = tagColor, style = MaterialTheme.typography.labelLarge.copy(fontSize = sp(11)))
                                    }
                                }
                            }
                            Spacer(Modifier.height(4.dp))
                            Text(plan.desc, style = MaterialTheme.typography.bodyMedium)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("¥${plan.price}", style = MaterialTheme.typography.headlineMedium.copy(fontSize = androidx.compose.ui.unit.sp(24)), color = Orange, fontWeight = FontWeight.Bold)
                            Text("${plan.vipDays}天", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }

        // 错误/成功
        if (vm.error.isNotBlank()) {
            Spacer(Modifier.height(8.dp))
            Surface(Modifier.padding(horizontal = 16.dp).fillMaxWidth(), color = Pink.copy(alpha = 0.1f), shape = RoundedCornerShape(12.dp)) {
                Text("❌ ${vm.error}", Modifier.padding(12.dp), color = Pink)
            }
        }
        if (vm.paySuccess.isNotBlank()) {
            Spacer(Modifier.height(8.dp))
            Surface(Modifier.padding(horizontal = 16.dp).fillMaxWidth(), color = Cyan.copy(alpha = 0.1f), shape = RoundedCornerShape(12.dp)) {
                Text("🎉 ${vm.paySuccess}", Modifier.padding(12.dp), color = Cyan, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(Modifier.height(32.dp))
    }

    // 付款弹窗
    if (vm.showQrDialog && vm.selectedPlan != null) {
        Dialog(onDismissRequest = { vm.showQrDialog = false }, properties = DialogProperties(usePlatformDefaultWidth = false)) {
            Surface(Modifier.fillMaxWidth(0.9f).fillMaxHeight(0.75f), shape = RoundedCornerShape(20.dp), color = CardBg) {
                Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("扫码支付", style = MaterialTheme.typography.headlineMedium, color = Orange)
                    Text("${vm.selectedPlan!!.name} · ¥${vm.selectedPlan!!.price}", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 8.dp))

                    Spacer(Modifier.height(16.dp))

                    if (vm.qrCode.isNotBlank()) {
                        AsyncImage(
                            model = vm.qrCode,
                            contentDescription = "收款码",
                            modifier = Modifier.fillMaxWidth().aspectRatio(1f).clip(RoundedCornerShape(12.dp)).border(BorderStroke(2.dp, Orange), RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Fit
                        )
                    } else {
                        Surface(Modifier.fillMaxWidth().aspectRatio(1f), shape = RoundedCornerShape(12.dp), color = CardBg, border = BorderStroke(2.dp, CardStroke)) {
                            Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                                Icon(Icons.Filled.QrCode, "", Modifier.size(64.dp), tint = TextSub)
                                Spacer(Modifier.height(8.dp))
                                Text("管理员未设置收款码", color = TextSub)
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))
                    Text("请使用微信/支付宝扫码支付 ¥${vm.selectedPlan!!.price}", style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)

                    Spacer(Modifier.height(20.dp))

                    Button(
                        { vm.confirmPayment {} },
                        Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Orange),
                        enabled = !vm.isLoading
                    ) {
                        if (vm.isLoading) CircularProgressIndicator(Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                        else Text("✅ 已完成付款", style = MaterialTheme.typography.labelLarge)
                    }

                    Spacer(Modifier.height(8.dp))
                    TextButton({ vm.showQrDialog = false }) { Text("取消", color = TextSub) }
                }
            }
        }
    }
}
extSub) }
                }
            }
        }
    }
}
