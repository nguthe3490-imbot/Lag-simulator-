package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CellTower
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CardSpaceBackground
import com.example.ui.theme.CardSpaceBorder
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.ElegantGold
import com.example.ui.theme.ElegantTextPrimary
import com.example.ui.theme.ElegantTextSecondary

@Composable
fun SimulatorScreen(viewModel: MainViewModel) {
    val selectedGame by viewModel.selectedGame.collectAsState()
    val targetPing by viewModel.targetPing.collectAsState()
    val targetJitter by viewModel.targetJitter.collectAsState()
    val targetLoss by viewModel.targetLoss.collectAsState()
    val isSimulating by viewModel.isSimulating.collectAsState()
    val currentPing by viewModel.currentPing.collectAsState()
    val pingHistory by viewModel.pingHistoryList.collectAsState()
    val packetLossActive by viewModel.packetLossActive.collectAsState()
    val currentLagReport by viewModel.currentLagReport.collectAsState()

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- Header Intro ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardSpaceBackground),
            border = BorderStroke(1.dp, CardSpaceBorder)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CellTower,
                    contentDescription = "Lag Icon",
                    tint = NeonPink,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Trình Giả Lập Lag Game",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonPink
                    )
                    Text(
                        text = "Mô phỏng độ trễ mạng thực tế trong game và trải nghiệm sự khó chịu của game thủ khi ping cao!",
                        fontSize = 12.sp,
                        color = Color.LightGray
                    )
                }
            }
        }

        // --- Config Section ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardSpaceBackground),
            border = BorderStroke(1.dp, CardSpaceBorder)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Cấu Hình Đường Truyền",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonCyan
                )

                // Game selection
                var dropdownExpanded by remember { mutableStateOf(false) }
                Column {
                    Text(text = "Trò chơi mục tiêu", fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF1B1A2F))
                            .border(1.dp, CardSpaceBorder, RoundedCornerShape(8.dp))
                            .clickable { dropdownExpanded = true }
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = selectedGame, color = Color.White, fontWeight = FontWeight.SemiBold)
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Dropdown Arrow",
                                tint = NeonCyan
                            )
                        }

                        DropdownMenu(
                            expanded = dropdownExpanded,
                            onDismissRequest = { dropdownExpanded = false },
                            modifier = Modifier.background(CardSpaceBackground)
                        ) {
                            viewModel.games.forEach { game ->
                                DropdownMenuItem(
                                    text = { Text(text = game, color = Color.White) },
                                    onClick = {
                                        viewModel.selectGame(game)
                                        dropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Slider: Ping / Latency
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Độ trễ cơ bản (Ping)", fontSize = 14.sp, color = Color.White)
                        Text(text = "${targetPing}ms", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = NeonPink)
                    }
                    Slider(
                        value = targetPing.toFloat(),
                        onValueChange = { viewModel.setTargetPing(it.toInt()) },
                        valueRange = 10f..800f,
                        colors = SliderDefaults.colors(
                            thumbColor = NeonPink,
                            activeTrackColor = NeonPink,
                            inactiveTrackColor = Color.DarkGray
                        )
                    )
                }

                // Slider: Jitter
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Biến động ping (Jitter)", fontSize = 14.sp, color = Color.White)
                        Text(text = "${targetJitter}ms", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                    }
                    Slider(
                        value = targetJitter.toFloat(),
                        onValueChange = { viewModel.setTargetJitter(it.toInt()) },
                        valueRange = 0f..150f,
                        colors = SliderDefaults.colors(
                            thumbColor = NeonCyan,
                            activeTrackColor = NeonCyan,
                            inactiveTrackColor = Color.DarkGray
                        )
                    )
                }

                // Slider: Packet Loss
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Tỉ lệ mất gói tin (Packet Loss)", fontSize = 14.sp, color = Color.White)
                        Text(text = "${targetLoss}%", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Yellow)
                    }
                    Slider(
                        value = targetLoss.toFloat(),
                        onValueChange = { viewModel.setTargetLoss(it.toInt()) },
                        valueRange = 0f..50f,
                        colors = SliderDefaults.colors(
                            thumbColor = Color.Yellow,
                            activeTrackColor = Color.Yellow,
                            inactiveTrackColor = Color.DarkGray
                        )
                    )
                }

                // Toggle Simulation button
                Button(
                    onClick = { viewModel.toggleSimulation() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("simulate_toggle_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSimulating) Color.Red else NeonPink
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = if (isSimulating) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Simulate Action"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isSimulating) "Dừng Giả Lập Mạng" else "Bắt Đầu Giả Lập",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }

        // --- Simulated Live Graph & Status Panel ---
        AnimatedVisibility(visible = isSimulating) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardSpaceBackground),
                border = BorderStroke(1.dp, CardSpaceBorder)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Trạng Thái Đường Truyền Live",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = ElegantGold
                    )

                    // Ping Monitor Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            val colorAndStatus = when {
                                currentPing == 999 -> Pair(Color.Red, "MẤT KẾT NỐI (LOSS)")
                                currentPing > 200 -> Pair(Color.Red, "RẤT LAG (999+ ms)")
                                currentPing > 80 -> Pair(Color.Yellow, "TRỄ TRUNG BÌNH")
                                else -> Pair(Color.Green, "KẾT NỐI MƯỢT")
                            }
                            Text(
                                text = if (currentPing == 999) "LOST" else "${currentPing} ms",
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Black,
                                color = colorAndStatus.first
                            )
                            Text(
                                text = colorAndStatus.second,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = colorAndStatus.first
                            )
                        }

                        // Jitter and loss display
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "Loss: $targetLoss%", color = Color.LightGray, fontSize = 12.sp)
                            Text(text = "Jitter: ±${targetJitter}ms", color = Color.LightGray, fontSize = 12.sp)
                            if (packetLossActive) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = "Loss alert",
                                        tint = Color.Red,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "Mất gói!", color = Color.Red, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // Dynamic graph drawn with Canvas!
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF0F0E1B))
                            .border(1.dp, CardSpaceBorder, RoundedCornerShape(8.dp))
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            if (pingHistory.isEmpty()) return@Canvas

                            val maxPoints = 30
                            val width = size.width
                            val height = size.height
                            val maxPingValue = 500f // normalize anything above 500ms

                            val path = Path()
                            val areaPath = Path()

                            val stepX = width / (maxPoints - 1)

                            pingHistory.forEachIndexed { index, ping ->
                                val x = index * stepX
                                // inverse height because (0,0) is top-left
                                val normalizedPing = if (ping > maxPingValue) maxPingValue else ping.toFloat()
                                val y = height - (normalizedPing / maxPingValue) * (height - 20f) - 10f

                                if (index == 0) {
                                    path.moveTo(x, y)
                                    areaPath.moveTo(x, height)
                                    areaPath.lineTo(x, y)
                                } else {
                                    path.lineTo(x, y)
                                    areaPath.lineTo(x, y)
                                }

                                if (index == pingHistory.size - 1) {
                                    areaPath.lineTo(x, height)
                                    areaPath.close()
                                }
                            }

                            // Draw gradient background area
                            drawPath(
                                path = areaPath,
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        NeonPink.copy(alpha = 0.3f),
                                        Color.Transparent
                                    )
                                )
                            )

                            // Draw glowing path line
                            drawPath(
                                path = path,
                                color = NeonPink,
                                style = Stroke(width = 3.dp.toPx())
                            )

                            // Draw dots on path
                            pingHistory.forEachIndexed { index, ping ->
                                val x = index * stepX
                                val normalizedPing = if (ping > maxPingValue) maxPingValue else ping.toFloat()
                                val y = height - (normalizedPing / maxPingValue) * (height - 20f) - 10f

                                drawCircle(
                                    color = if (ping > 200) Color.Red else NeonCyan,
                                    radius = 3.dp.toPx(),
                                    center = Offset(x, y)
                                )
                            }
                        }
                    }

                    Text(
                        text = "💡 Mẹo: Nhấn sang Tab 'Trợ lý Linh Chi' để nghe em khuyên cách sửa mạng nhé!",
                        fontSize = 11.sp,
                        color = NeonCyan,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Button(
                        onClick = { viewModel.reportHighLatency() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("report_lag_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonPink.copy(alpha = 0.15f),
                            contentColor = NeonPink
                        ),
                        border = BorderStroke(1.dp, NeonPink),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Report Lag Icon",
                            tint = NeonPink,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Báo Cáo Mạng Lag Cho Linh Chi",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // --- Dedicated Card Area: Linh Chi's Custom Optimization Tips ---
        AnimatedVisibility(visible = currentLagReport != null) {
            currentLagReport?.let { report ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("linh_chi_tips_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardSpaceBackground),
                    border = BorderStroke(1.5.dp, ElegantGold)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Title header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(ElegantGold.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = "Linh Chi Tips Icon",
                                        tint = ElegantGold,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Lời Khuyên Từ Linh Chi",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ElegantGold
                                )
                            }
                            // Time badge
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF1B1A2F))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Báo cáo: ${report.timestamp}",
                                    fontSize = 10.sp,
                                    color = Color.Gray,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        // Summary of recorded metrics
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF0F0E1B))
                                .border(1.dp, CardSpaceBorder, RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "Sự cố: ${report.mainIssue}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeonPink
                                )
                                Text(
                                    text = "Chi tiết đường truyền giả lập tại game [${report.gameName}]: Ping ${report.ping}ms | Jitter ±${report.jitter}ms | Loss ${report.loss}%",
                                    fontSize = 11.sp,
                                    color = Color.LightGray
                                )
                            }
                        }

                        // Linh Chi's response according to style
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp, 12.dp, 12.dp, 0.dp))
                                .background(NeonPink.copy(alpha = 0.08f))
                                .border(1.dp, NeonPink.copy(alpha = 0.2f), RoundedCornerShape(12.dp, 12.dp, 12.dp, 0.dp))
                                .padding(12.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "Linh Chi 🌸",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeonPink
                                )
                                Text(
                                    text = report.introMessage,
                                    fontSize = 12.sp,
                                    color = ElegantTextPrimary,
                                    lineHeight = 18.sp
                                )
                            }
                        }

                        // Numbered Optimization Tips list
                        Column(
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            report.tips.forEachIndexed { index, tip ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    // Step Number Bubble
                                    Box(
                                        modifier = Modifier
                                            .size(22.dp)
                                            .clip(CircleShape)
                                            .background(NeonCyan.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "${index + 1}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = NeonCyan
                                        )
                                    }

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = tip.first,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = NeonCyan
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = tip.second,
                                            fontSize = 12.sp,
                                            color = Color.LightGray,
                                            lineHeight = 16.sp
                                        )
                                    }
                                }
                            }
                        }

                        // Action Buttons at bottom
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { viewModel.askLinhChiAboutReport() },
                                modifier = Modifier
                                    .weight(1.3f)
                                    .height(44.dp)
                                    .testTag("ask_more_button"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = NeonPink
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "Trò Chuyện & Thả Thính",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            Button(
                                onClick = { viewModel.clearLagReport() },
                                modifier = Modifier
                                    .weight(0.7f)
                                    .height(44.dp)
                                    .testTag("dismiss_tips_button"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.DarkGray.copy(alpha = 0.6f),
                                    contentColor = Color.LightGray
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "Đóng",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- Reflex Lag Game Section ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardSpaceBackground),
            border = BorderStroke(1.dp, CardSpaceBorder)
        ) {
            val reflexState by viewModel.reflexState.collectAsState()
            val targetX by viewModel.reflexTargetX.collectAsState()
            val targetY by viewModel.reflexTargetY.collectAsState()
            val reflexMsg by viewModel.reflexMessage.collectAsState()
            val timerText by viewModel.reflexTimerText.collectAsState()

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Kiểm Tra Phản Xạ Khi Lag Mạng",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan
                    )
                    Icon(
                        imageVector = Icons.Default.Speed,
                        contentDescription = "Speed Test Icon",
                        tint = NeonCyan
                    )
                }

                Text(
                    text = "Hãy click mục tiêu đỏ xuất hiện nhanh nhất có thể. Thử thách mô phỏng chính xác độ trễ gửi click lên server và rủi ro bị mất gói tin (packet loss).",
                    fontSize = 12.sp,
                    color = Color.LightGray
                )

                // Message Area
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1B1A2F), RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = reflexMsg,
                            color = if (reflexState == "spawned") NeonPink else Color.White,
                            fontWeight = if (reflexState == "spawned") FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                        if (timerText.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = timerText,
                                color = NeonCyan,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                // Interactive Box Area
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF0F0E1B))
                        .border(1.dp, CardSpaceBorder, RoundedCornerShape(12.dp))
                ) {
                    when (reflexState) {
                        "idle" -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Button(
                                    onClick = { viewModel.startReflexGame() },
                                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.testTag("reflex_start_button")
                                ) {
                                    Text(text = "Chạy Thử Thách", color = Color.Black, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        "preparing" -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    CircularProgressIndicator(color = NeonCyan)
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(text = "Đang kết nối server game...", color = Color.LightGray, fontSize = 12.sp)
                                }
                            }
                        }
                        "spawned" -> {
                            // Infinite heartbeat glow animation for target
                            val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                            val scale by infiniteTransition.animateFloat(
                                initialValue = 1f,
                                targetValue = 1.3f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(400),
                                    repeatMode = RepeatMode.Reverse
                                ),
                                label = "scale"
                            )

                            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                                val sizeX = maxWidth
                                val sizeY = maxHeight

                                Box(
                                    modifier = Modifier
                                        .offset(
                                            x = sizeX * targetX - 24.dp,
                                            y = sizeY * targetY - 24.dp
                                        )
                                        .size(48.dp * scale)
                                        .clip(CircleShape)
                                        .background(NeonPink)
                                        .border(2.dp, Color.White, CircleShape)
                                        .clickable { viewModel.handleReflexClick() }
                                        .testTag("reflex_target_button"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    // A tiny target dot
                                    Box(
                                        modifier = Modifier
                                            .size(12.dp)
                                            .clip(CircleShape)
                                            .background(Color.White)
                                    )
                                }
                            }
                        }
                        "delaying" -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    CircularProgressIndicator(color = NeonPink)
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(text = "Độ trễ lag game đang kéo dài...", color = NeonPink, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                    Text(text = "(Vui lòng chờ phản hồi lệnh bấm từ máy chủ)", color = Color.Gray, fontSize = 10.sp)
                                }
                            }
                        }
                        "finished" -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Button(
                                    onClick = { viewModel.resetReflexGame() },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.testTag("reflex_reset_button")
                                ) {
                                    Icon(imageVector = Icons.Default.Refresh, contentDescription = "Retry Icon")
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = "Thử Lại")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
