package com.ai.tucaodiary.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ai.tucaodiary.ui.theme.*

@Composable
fun StyleSelector(
    selectedStyle: String,
    onStyleSelected: (String) -> Unit
) {
    val styles = listOf("阴阳怪气", "暴躁老哥", "梗王附体", "文艺丧", "自嘲大师")
    val emojis = listOf("🙂", "🤬", "😂", "🥀", "🤡")
    val descriptions = listOf(
        "表面客气扎心致命",
        "火力全开拒绝憋屈",
        "梗多到溢出屏幕",
        "丧得很有文学感",
        "惨得好笑说的就是我"
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "选择吐槽风格",
            style = MaterialTheme.typography.titleMedium,
            color = TextSecondary
        )

        styles.forEachIndexed { index, style ->
            val isSelected = style == selectedStyle
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onStyleSelected(style) },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) AccentOrange.copy(alpha = 0.2f) else DarkCard
                ),
                border = if (isSelected) {
                    androidx.compose.foundation.BorderStroke(1.5.dp, AccentOrange)
                } else null
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = emojis[index], fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = style,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) AccentOrange else TextPrimary
                        )
                        Text(
                            text = descriptions[index],
                            style = MaterialTheme.typography.labelMedium,
                            color = TextSecondary
                        )
                    }
                }
            }
        }
    }
}
