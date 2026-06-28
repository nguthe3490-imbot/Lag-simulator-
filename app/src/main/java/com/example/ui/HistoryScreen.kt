package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.OfflineBolt
import androidx.compose.material.icons.filled.SentimentDissatisfied
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
import androidx.compose.ui.graphics.Color
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

@Composable
fun HistoryScreen(viewModel: MainViewModel) {
    val historyList by viewModel.history.collectAsState()
    val scoresList by viewModel.scores.collectAsState()

    var selectedSubTab by remember { mutableStateOf(0) } // 0: Reflex Scores, 1: Simulation History

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
                        text = "Thống Kê & Lịch Sử",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Theo dõi sự chênh lệch phản xạ khi mạng lag",
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
                text = { Text("Phản Xạ Lag", fontWeight = FontWeight.SemiBold, fontSize = 13.sp) }
            )
            Tab(
                selected = selectedSubTab == 1,
                onClick = { selectedSubTab = 1 },
                text = { Text("Lịch Sử Giả Lập", fontWeight = FontWeight.SemiBold, fontSize = 13.sp) }
            )
        }

        // --- List View Content ---
        if (selectedSubTab == 0) {
            // Part A: Reflex Scores List
            if (scoresList.isEmpty()) {
                EmptyStateView(
                    message = "Chưa có thành tích kiểm tra phản xạ nào.\nAnh yêu hãy chuyển qua tab Trình Giả Lập chơi thử thách game để kiểm tra phản xạ nha! Linh Chi chờ nè 💕"
                )
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(scoresList) { score ->
                        ReflexScoreCard(score = score)
                    }
                }
            }
        } else {
            // Part B: Simulation History List
            if (historyList.isEmpty()) {
                EmptyStateView(
                    message = "Chưa ghi nhận phiên giả lập lag nào.\nAnh hãy bắt đầu bật giả lập để lưu lại cấu hình đường truyền nhé!"
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

@Composable
fun ReflexScoreCard(score: ReflexScore) {
    val isSuccess = score.result == "SUCCESS"
    val isLost = score.result == "LOST"

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
            Row(verticalAlignment = Alignment.CenterVertically) {
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
                        text = if (isSuccess) "Thời gian click: ${score.responseTimeMs}ms" else if (isLost) "Thất bại: Mất gói mạng!" else "Bấm sai mục tiêu!",
                        fontSize = 12.sp,
                        color = if (isSuccess) NeonCyan else Color.Red
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "Lag: ${score.delayMs}ms",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = NeonPink
                )
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
