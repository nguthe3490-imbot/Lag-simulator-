package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.OfflineBolt
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ReflexScore
import com.example.data.SimulationHistory
import com.example.ui.theme.CardSpaceBackground
import com.example.ui.theme.CardSpaceBorder
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.ElegantGold
import com.example.ui.theme.ElegantTextPrimary
import kotlin.math.roundToInt

@Composable
fun HistoryScreen(viewModel: MainViewModel) {
    val appLanguage by viewModel.appLanguage.collectAsState() // Observe language changes instantly
    val historyList by viewModel.history.collectAsState()
    val scoresList by viewModel.scores.collectAsState()

    var selectedSubTab by remember { mutableStateOf(0) } // 0: Reflex Scores, 1: Visual Dashboard, 2: Simulation History

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- Header Block ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = "Logs",
                    tint = NeonPink,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = t("history_title"),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = t("history_subtitle"),
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
            }

            // Clear history button
            if (historyList.isNotEmpty() || scoresList.isNotEmpty()) {
                IconButton(
                    onClick = { viewModel.clearAllHistoryAndScores() },
                    modifier = Modifier.testTag("clear_history_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = "Clear All Logs",
                        tint = Color.Red
                    )
                }
            }
        }

        // --- Sub Tabs ---
        TabRow(
            selectedTabIndex = selectedSubTab,
            containerColor = CardSpaceBackground,
            contentColor = Color.White,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedSubTab]),
                    color = NeonPink
                )
            },
            modifier = Modifier.clip(RoundedCornerShape(8.dp))
        ) {
            Tab(
                selected = selectedSubTab == 0,
                onClick = { selectedSubTab = 0 },
                text = { Text(t("history_tab_reflex"), fontWeight = FontWeight.SemiBold, fontSize = 12.sp) }
            )
            Tab(
                selected = selectedSubTab == 1,
                onClick = { selectedSubTab = 1 },
                text = { Text(t("history_tab_trends"), fontWeight = FontWeight.SemiBold, fontSize = 12.sp) }
            )
            Tab(
                selected = selectedSubTab == 2,
                onClick = { selectedSubTab = 2 },
                text = { Text(t("history_tab_sim"), fontWeight = FontWeight.SemiBold, fontSize = 12.sp) }
            )
        }

        // --- List View Content ---
        when (selectedSubTab) {
            0 -> {
                // Part A: Reflex Scores List
                if (scoresList.isEmpty()) {
                    EmptyStateView(
                        message = t("history_empty_reflex")
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            GamerRankCard(scores = scoresList)
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                        items(scoresList) { score ->
                            ReflexScoreCard(score = score)
                        }
                    }
                }
            }
            1 -> {
                // Part B: Latency Trends Dashboard (Recharts style)
                Box(modifier = Modifier.weight(1f)) {
                    LatencyTrendsDashboard(viewModel)
                }
            }
            2 -> {
                // Part C: Simulation History List
                if (historyList.isEmpty()) {
                    EmptyStateView(
                        message = t("history_empty_sim")
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(historyList) { history ->
                            SimulationHistoryCard(history = history)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LatencyTrendsDashboard(viewModel: MainViewModel) {
    val historyList by viewModel.history.collectAsState()
    
    // Game filters list
    val allGamesOption = "Tất cả"
    val gamesList = listOf(allGamesOption) + viewModel.games
    var selectedGameFilter by remember { mutableStateOf(allGamesOption) }
    
    // Filtered data (oldest to newest for chronological trend charting)
    val filteredHistory = remember(historyList, selectedGameFilter) {
        val filtered = if (selectedGameFilter == allGamesOption) {
            historyList
        } else {
            historyList.filter { it.gameName == selectedGameFilter }
        }
        filtered.reversed() // oldest first
    }
    
    val totalRuns = filteredHistory.size
    val avgPing = if (totalRuns > 0) filteredHistory.map { it.pingMs }.average().toInt() else 0
    val maxJitter = if (totalRuns > 0) filteredHistory.maxOf { it.jitterMs } else 0
    val avgLoss = if (totalRuns > 0) filteredHistory.map { it.packetLossPercent }.average() else 0.0
    val avgJitter = if (totalRuns > 0) filteredHistory.map { it.jitterMs }.average() else 0.0
    
    // Stability Index = 100 - (avgLoss * 5) - (avgJitter * 0.3) - (avgPing * 0.05)
    val stabilityScore = if (totalRuns > 0) {
        (100 - (avgLoss * 5.0) - (avgJitter * 0.3) - (avgPing * 0.05)).coerceIn(10.0, 100.0).toInt()
    } else {
        100
    }
    
    // Interactive chart tooltip state
    var hoveredIndex by remember { mutableStateOf(-1) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Game filter chip row
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "Bộ lọc Trò chơi:",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.LightGray
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                gamesList.forEach { gameName ->
                    val isSelected = gameName == selectedGameFilter
                    val chipBg = if (isSelected) {
                        Brush.linearGradient(colors = listOf(NeonPink.copy(alpha = 0.25f), NeonCyan.copy(alpha = 0.25f)))
                    } else {
                        Brush.linearGradient(colors = listOf(CardSpaceBackground, CardSpaceBackground))
                    }
                    val borderStrokeColor = if (isSelected) NeonPink else CardSpaceBorder
                    
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(chipBg)
                            .border(1.dp, borderStrokeColor, RoundedCornerShape(16.dp))
                            .clickable { 
                                selectedGameFilter = gameName 
                                hoveredIndex = -1 // reset tooltip
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = gameName,
                            color = if (isSelected) Color.White else Color.LightGray,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }
        }
        
        if (totalRuns == 0) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "No data",
                        tint = Color.Gray,
                        modifier = Modifier.size(36.dp)
                    )
                    Text(
                        text = "Chưa có đủ dữ liệu giả lập cho trò chơi này.\nBật trình giả lập mạng trước nha cưng! 🎮",
                        fontSize = 12.sp,
                        color = Color.LightGray,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            // Highlights Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Stability Card
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = CardSpaceBackground),
                    border = BorderStroke(1.dp, CardSpaceBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(text = t("analyzer_stability"), fontSize = 10.sp, color = Color.Gray)
                        Text(
                            text = "$stabilityScore%",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = when {
                                stabilityScore >= 90 -> Color.Green
                                stabilityScore >= 75 -> Color.Yellow
                                else -> Color.Red
                            }
                        )
                    }
                }
                
                // Avg Ping Card
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = CardSpaceBackground),
                    border = BorderStroke(1.dp, CardSpaceBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(text = t("analyzer_avg_ping"), fontSize = 10.sp, color = Color.Gray)
                        Text(
                            text = "${avgPing}ms",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = NeonPink
                        )
                    }
                }
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Max Jitter Card
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = CardSpaceBackground),
                    border = BorderStroke(1.dp, CardSpaceBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(text = t("analyzer_max_jitter"), fontSize = 10.sp, color = Color.Gray)
                        Text(
                            text = "±${maxJitter}ms",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = NeonCyan
                        )
                    }
                }
                
                // Avg Loss Card
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = CardSpaceBackground),
                    border = BorderStroke(1.dp, CardSpaceBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(text = t("analyzer_avg_loss"), fontSize = 10.sp, color = Color.Gray)
                        Text(
                            text = String.format("%.1f%%", avgLoss),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.Yellow
                        )
                    }
                }
            }
            
            // --- Interactive Recharts-inspired Custom Chart ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardSpaceBackground),
                border = BorderStroke(1.dp, CardSpaceBorder)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Chart title & legends
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.BarChart,
                                contentDescription = "Chart",
                                tint = ElegantGold,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Biểu đồ đường truyền (Recharts-style)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                    
                    // Legends
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LegendItem(color = NeonPink, text = "Ping (ms)")
                        LegendItem(color = NeonCyan, text = "Jitter (ms)")
                        LegendItem(color = Color.Yellow, text = "Loss (%)")
                    }
                    
                    // Chart drawing container
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF0F0E1B))
                            .border(1.dp, CardSpaceBorder, RoundedCornerShape(8.dp))
                    ) {
                        Canvas(
                            modifier = Modifier
                                .fillMaxSize()
                                .pointerInput(filteredHistory) {
                                    detectTapGestures { offset ->
                                        if (filteredHistory.isEmpty()) return@detectTapGestures
                                        val chartWidth = size.width
                                        val pointsCount = filteredHistory.size
                                        val stepX = if (pointsCount > 1) chartWidth / (pointsCount - 1) else chartWidth
                                        
                                        // Find nearest point index
                                        val index = (offset.x / stepX).roundToInt().coerceIn(0, pointsCount - 1)
                                        hoveredIndex = index
                                    }
                                }
                        ) {
                            if (filteredHistory.isEmpty()) return@Canvas
                            
                            val width = size.width
                            val height = size.height
                            val maxPoints = filteredHistory.size
                            val stepX = if (maxPoints > 1) width / (maxPoints - 1) else width
                            
                            // Max visual scales
                            val maxVal = 500f
                            
                            // 1. Draw horizontal grid lines
                            val gridLines = listOf(0.2f, 0.5f, 0.8f)
                            gridLines.forEach { percent ->
                                val yGrid = height * percent
                                drawLine(
                                    color = Color.DarkGray.copy(alpha = 0.3f),
                                    start = Offset(0f, yGrid),
                                    end = Offset(width, yGrid),
                                    strokeWidth = 1.dp.toPx(),
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                                )
                            }
                            
                            // 2. Draw Packet Loss vertical columns (yellow bars)
                            filteredHistory.forEachIndexed { index, item ->
                                val x = index * stepX
                                val lossHeightPercent = (item.packetLossPercent.toFloat() / 50f).coerceIn(0f, 1f)
                                val barHeight = height * 0.4f * lossHeightPercent
                                val barWidth = 8.dp.toPx()
                                
                                drawRect(
                                    color = Color.Yellow.copy(alpha = 0.35f),
                                    topLeft = Offset(x - barWidth / 2, height - barHeight),
                                    size = Size(barWidth, barHeight)
                                )
                            }
                            
                            // 3. Build paths for Ping (Pink) and Jitter (Cyan)
                            val pingPath = Path()
                            val jitterPath = Path()
                            
                            filteredHistory.forEachIndexed { index, item ->
                                val x = index * stepX
                                val pingY = height - (item.pingMs.toFloat() / maxVal).coerceIn(0f, 1f) * (height - 30f) - 15f
                                val jitterY = height - (item.jitterMs.toFloat() / 150f).coerceIn(0f, 1f) * (height - 30f) - 15f
                                
                                if (index == 0) {
                                    pingPath.moveTo(x, pingY)
                                    jitterPath.moveTo(x, jitterY)
                                } else {
                                    pingPath.lineTo(x, pingY)
                                    jitterPath.lineTo(x, jitterY)
                                }
                            }
                            
                            // Draw the paths
                            drawPath(
                                path = pingPath,
                                color = NeonPink,
                                style = Stroke(width = 2.5.dp.toPx())
                            )
                            drawPath(
                                path = jitterPath,
                                color = NeonCyan,
                                style = Stroke(width = 2.5.dp.toPx())
                            )
                            
                            // 4. Draw node circles
                            filteredHistory.forEachIndexed { index, item ->
                                val x = index * stepX
                                val pingY = height - (item.pingMs.toFloat() / maxVal).coerceIn(0f, 1f) * (height - 30f) - 15f
                                val jitterY = height - (item.jitterMs.toFloat() / 150f).coerceIn(0f, 1f) * (height - 30f) - 15f
                                
                                if (index == hoveredIndex) {
                                    drawCircle(
                                        color = NeonPink.copy(alpha = 0.3f),
                                        radius = 8.dp.toPx(),
                                        center = Offset(x, pingY)
                                    )
                                    drawCircle(
                                        color = NeonCyan.copy(alpha = 0.3f),
                                        radius = 8.dp.toPx(),
                                        center = Offset(x, jitterY)
                                    )
                                }
                                
                                drawCircle(
                                    color = NeonPink,
                                    radius = 4.dp.toPx(),
                                    center = Offset(x, pingY)
                                )
                                drawCircle(
                                    color = NeonCyan,
                                    radius = 4.dp.toPx(),
                                    center = Offset(x, jitterY)
                                )
                            }
                            
                            // 5. Draw interactive hovering dashed line
                            if (hoveredIndex in 0 until maxPoints) {
                                val lineX = hoveredIndex * stepX
                                drawLine(
                                    color = Color.LightGray.copy(alpha = 0.5f),
                                    start = Offset(lineX, 0f),
                                    end = Offset(lineX, height),
                                    strokeWidth = 1.dp.toPx(),
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 5f), 0f)
                                )
                            }
                        }
                    }
                    
                    // Instruction or selected tooltip details (Recharts Tooltip)
                    if (hoveredIndex in 0 until totalRuns) {
                        val selectedRun = filteredHistory[hoveredIndex]
                        val runStability = (100 - (selectedRun.packetLossPercent * 5) - (selectedRun.jitterMs * 0.3) - (selectedRun.pingMs * 0.05)).coerceIn(10.0, 100.0).toInt()
                        
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF1E1C33))
                                .border(1.dp, ElegantGold.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = selectedRun.gameName,
                                        color = ElegantGold,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    val formattedTime = java.text.SimpleDateFormat("dd/MM HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date(selectedRun.timestamp))
                                    Text(
                                        text = formattedTime,
                                        color = Color.Gray,
                                        fontSize = 10.sp
                                    )
                                }
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = "• Ping: ${selectedRun.pingMs}ms", color = NeonPink, fontSize = 11.sp)
                                    Text(text = "• Jitter: ±${selectedRun.jitterMs}ms", color = NeonCyan, fontSize = 11.sp)
                                    Text(text = "• Mất gói: ${selectedRun.packetLossPercent}%", color = Color.Yellow, fontSize = 11.sp)
                                    Text(text = "• Độ ổn định: $runStability%", color = Color.Green, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    } else {
                        Text(
                            text = "👉 Chạm vào các mốc điểm trên biểu đồ để xem thông số chi tiết (Recharts interactive tooltip).",
                            fontSize = 11.sp,
                            color = Color.Gray,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
            
            // --- Linh Chi's Evaluation Bubble ---
            val evaluationText = when {
                stabilityScore >= 90 -> "Đường truyền của anh siêu ổn định luôn á! 😍 Linh Chi chấm điểm tuyệt đối 10/10 nha! Cùng em vào game làm vài trận thắng tưng bừng thui nào! 💕"
                stabilityScore >= 75 -> "Kết nối mạng ở mức Khá Tốt nè anh yêu. 😉 Có gợn sóng hay mất gói nhẹ nhưng không quá nghiêm trọng. Anh nên hạn chế download ngầm để tránh ping nhảy nhé!"
                else -> "Mạng giật lag đỏ lòm rùi anh yêu ơi! 🥺 Linh Chi khuyên anh nên cắm dây mạng trực tiếp hoặc thử khởi động lại router xem sao nhé. Em thương anh ghê, lag thế này chơi game bực lắm á! 🌸"
            }
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp, 12.dp, 12.dp, 0.dp))
                    .background(NeonPink.copy(alpha = 0.08f))
                    .border(1.dp, NeonPink.copy(alpha = 0.2f), RoundedCornerShape(12.dp, 12.dp, 12.dp, 0.dp))
                    .padding(12.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Linh Chi Đánh Giá 🌸",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonPink
                        )
                    }
                    Text(
                        text = evaluationText,
                        fontSize = 12.sp,
                        color = ElegantTextPrimary,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
fun LegendItem(color: Color, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = text,
            color = Color.LightGray,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium
        )
    }
}


@Composable
fun ReflexScoreCard(score: ReflexScore) {
    val isFpsGame = score.result.startsWith("FPS|")
    
    val isSuccess: Boolean
    val isLost: Boolean
    val subtitleText: String
    val diagnosisText: String?
    val hitsText: String?
    
    if (isFpsGame) {
        val parts = score.result.split("|")
        val hits = parts.getOrNull(1) ?: "0/5"
        val accuracy = parts.getOrNull(2) ?: "0%"
        val response = parts.getOrNull(3) ?: "0 ms"
        val diagnosis = parts.getOrNull(4) ?: "Ổn định"
        
        isSuccess = hits.startsWith("5/") || hits.startsWith("4/") || hits.startsWith("3/")
        isLost = hits.startsWith("0/")
        subtitleText = "Độ chính xác: $accuracy | Tay: $response"
        hitsText = "Trúng: $hits"
        diagnosisText = diagnosis
    } else {
        isSuccess = score.result == "SUCCESS"
        isLost = score.result == "LOST"
        subtitleText = if (isSuccess) "Thời gian click: ${score.responseTimeMs}ms" else if (isLost) "Thất bại: Mất gói mạng!" else "Bấm sai mục tiêu!"
        hitsText = null
        diagnosisText = null
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardSpaceBackground),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardSpaceBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                // Icon representing test outcome
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (isSuccess) Color(0xFF143026) else Color(0xFF421521)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isSuccess) Icons.Default.Leaderboard else Icons.Default.OfflineBolt,
                        contentDescription = "Status Icon",
                        tint = if (isSuccess) Color.Green else Color.Red,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = score.gameName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = subtitleText,
                        fontSize = 12.sp,
                        color = if (isSuccess) NeonCyan else Color.Red
                    )
                    if (!diagnosisText.isNullOrEmpty()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Chẩn đoán: $diagnosisText",
                            fontSize = 11.sp,
                            color = ElegantGold,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "Lag: ${score.delayMs}ms",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = NeonPink
                )
                if (hitsText != null) {
                    Text(
                        text = hitsText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.LightGray
                    )
                }
                val dateString = java.text.SimpleDateFormat("dd/MM HH:mm", java.util.Locale.getDefault()).format(java.util.Date(score.timestamp))
                Text(
                    text = dateString,
                    fontSize = 9.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun SimulationHistoryCard(history: SimulationHistory) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardSpaceBackground),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardSpaceBorder)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = history.gameName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                val dateString = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault()).format(java.util.Date(history.timestamp))
                Text(
                    text = dateString,
                    fontSize = 10.sp,
                    color = Color.Gray
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column {
                    Text(text = "Độ trễ (Ping)", fontSize = 10.sp, color = Color.Gray)
                    Text(text = "${history.pingMs}ms", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NeonPink)
                }
                Column {
                    Text(text = "Biến động (Jitter)", fontSize = 10.sp, color = Color.Gray)
                    Text(text = "${history.jitterMs}ms", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                }
                Column {
                    Text(text = "Mất gói (Loss)", fontSize = 10.sp, color = Color.Gray)
                    Text(text = "${history.packetLossPercent}%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Yellow)
                }
            }
        }
    }
}

@Composable
fun EmptyStateView(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.SentimentDissatisfied,
                contentDescription = "No Data Icon",
                tint = Color.Gray,
                modifier = Modifier.size(48.dp)
            )
            Text(
                text = message,
                fontSize = 13.sp,
                color = Color.LightGray,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
        }
    }
}

@Composable
fun GamerRankCard(scores: List<ReflexScore>) {
    val successfulHits = scores.filter { it.result == "SUCCESS" || it.result.startsWith("FPS|") }
    val avgResponse = if (successfulHits.isNotEmpty()) {
        successfulHits.map { it.responseTimeMs }.filter { it > 0 }.average().toInt()
    } else 0
    val bestResponse = if (successfulHits.isNotEmpty()) {
        successfulHits.map { it.responseTimeMs }.filter { it > 0 }.minOrNull() ?: 0
    } else 0
    val totalKills = scores.sumOf { it.kills }
    val totalHits = scores.sumOf { it.targetsHit }
    val accuracyPercent = if (scores.isNotEmpty()) (successfulHits.size * 100) / scores.size else 0

    val rankTitle: String
    val rankBadge: String
    val rankColor: Color
    val rankQuote: String
    val rankBg: Brush

    if (avgResponse == 0) {
        rankTitle = "TẬP SỰ ĐANG KHẢO SÁT 🔍"
        rankBadge = "📝"
        rankColor = Color.Gray
        rankQuote = "Hãy hoàn thành thêm vài lượt huấn luyện phản xạ để em xếp hạng chính xác cho anh yêu nhé! 💕"
        rankBg = Brush.linearGradient(colors = listOf(Color(0xFF1A1A24), Color(0xFF111115)))
    } else if (avgResponse < 240) {
        rankTitle = "THÁCH ĐẤU SIÊU TỐC ⚡"
        rankBadge = "🏆"
        rankColor = Color(0xFF00F5D4)
        rankQuote = "Tốc độ thần sầu, phản xạ đỉnh cao sánh ngang các siêu sao tuyển thủ chuyên nghiệp thế giới! 👑"
        rankBg = Brush.linearGradient(colors = listOf(Color(0xFF042F2E), Color(0xFF0F172A)))
    } else if (avgResponse < 320) {
        rankTitle = "CAO THỦ TINH ANH 🔮"
        rankBadge = "💎"
        rankColor = Color(0xFFA855F7)
        rankQuote = "Tay nhanh hơn não! Khả năng phản xạ cực nhạy, né skill lả lướt như thần gió Yasuo! 🌪️"
        rankBg = Brush.linearGradient(colors = listOf(Color(0xFF3B0764), Color(0xFF0F172A)))
    } else if (avgResponse < 420) {
        rankTitle = "KIM CƯƠNG CHIẾN THUẬT 🛡️"
        rankBadge = "⭐"
        rankColor = Color(0xFF3B82F6)
        rankQuote = "Phản xạ nhạy bén và điêu luyện. Bạn chính là chỗ dựa gánh team vững chắc trong mọi pha combat! ⚔️"
        rankBg = Brush.linearGradient(colors = listOf(Color(0xFF1E3A8A), Color(0xFF0F172A)))
    } else if (avgResponse < 550) {
        rankTitle = "BẠCH KIM CỨNG CÁP 🎖️"
        rankBadge = "🎖️"
        rankColor = Color(0xFF10B981)
        rankQuote = "Nhịp tay rất đều và chắc chắn. Tối ưu thêm chút ping mạng nữa là leo thẳng lên Thách Đấu ngay thôi! 🚀"
        rankBg = Brush.linearGradient(colors = listOf(Color(0xFF064E3B), Color(0xFF0F172A)))
    } else {
        rankTitle = "VÀNG ĐỒNG KIÊN CƯỜNG 🪵"
        rankBadge = "🔰"
        rankColor = Color(0xFFF59E0B)
        rankQuote = "Dù ping giật lag đỏ lòm hay mất gói ngập đầu, ý chí kiên định chiến đấu của anh vẫn là tuyệt nhất! 💪"
        rankBg = Brush.linearGradient(colors = listOf(Color(0xFF78350F), Color(0xFF0F172A)))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.2.dp, rankColor.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .background(rankBg)
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "BẢNG PHONG THẦN PHẢN XẠ 👑",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = ElegantGold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Kiện Tướng Liên Quân & Valorant",
                            fontSize = 10.sp,
                            color = Color.Gray
                        )
                    }
                    Text(
                        text = rankBadge,
                        fontSize = 24.sp
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(rankColor.copy(alpha = 0.15f))
                            .border(2.dp, rankColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (avgResponse == 0) "?" else "${avgResponse}ms",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = rankColor
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "HẠNG KIỆN TƯỚNG:",
                            fontSize = 9.sp,
                            color = Color.LightGray,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = rankTitle,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = rankColor
                        )
                    }
                }

                Text(
                    text = rankQuote,
                    fontSize = 11.sp,
                    color = Color.LightGray,
                    lineHeight = 16.sp,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(0.5.dp)
                        .background(CardSpaceBorder)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(horizontalAlignment = Alignment.Start) {
                        Text(text = "Phản xạ nhanh nhất", fontSize = 9.sp, color = Color.Gray)
                        Text(
                            text = if (bestResponse > 0) "${bestResponse} ms" else "--",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonCyan
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Tổng mạng diệt / bia", fontSize = 9.sp, color = Color.Gray)
                        Text(
                            text = "${totalKills + totalHits} mạng",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonPink
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "Tỷ lệ chuẩn xác", fontSize = 9.sp, color = Color.Gray)
                        Text(
                            text = "${accuracyPercent}%",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Yellow
                        )
                    }
                }

                // Dynamic Badges / Trophy Room
                val badges = remember(scores) {
                    val list = mutableListOf<Triple<String, String, Color>>()
                    if (bestResponse in 1..200) {
                        list.add(Triple("⚡ ÁNH SÁNG", "Phản xạ cực đỉnh < 200ms", Color(0xFF00F5D4)))
                    }
                    if (totalKills + totalHits >= 30) {
                        list.add(Triple("⚔️ SÁT THỦ", "Hạ gục trên 30 mục tiêu", Color(0xFFEF4444)))
                    }
                    if (accuracyPercent >= 85 && scores.size >= 3) {
                        list.add(Triple("🎯 THẦN TIỄN", "Chuẩn xác xuất sắc ≥ 85%", Color(0xFFFFD700)))
                    }
                    if (scores.size >= 8) {
                        list.add(Triple("🛡️ BẤT KHUẤT", "Rèn luyện bền bỉ ≥ 8 lượt", Color(0xFF3B82F6)))
                    }
                    if (scores.any { it.kills > 0 }) {
                        list.add(Triple("👑 DIỆT MA", "Chiến thắng Ma Vương Maloch", Color(0xFFA855F7)))
                    }
                    list
                }

                if (badges.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(0.5.dp)
                            .background(CardSpaceBorder)
                    )
                    
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "DANH HIỆU ĐẠT ĐƯỢC 🏆",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = ElegantGold
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            badges.forEach { (title, desc, color) ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(color.copy(alpha = 0.12f))
                                        .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Column(horizontalAlignment = Alignment.Start) {
                                        Text(
                                            text = title,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = color
                                        )
                                        Text(
                                            text = desc,
                                            fontSize = 8.sp,
                                            color = Color.LightGray
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
