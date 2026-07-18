package com.example.ui

import com.example.R

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CellTower
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import com.example.ui.theme.CardSpaceBackground
import com.example.ui.theme.CardSpaceBorder
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.ElegantGold
import com.example.ui.theme.ElegantTextPrimary
import com.example.ui.theme.ElegantTextSecondary
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.animateContentSize

@Composable
fun SimulatorScreen(viewModel: MainViewModel) {
    val appLanguage by viewModel.appLanguage.collectAsState() // Observe language changes instantly
    val selectedGame by viewModel.selectedGame.collectAsState()
    val targetPing by viewModel.targetPing.collectAsState()
    val targetJitter by viewModel.targetJitter.collectAsState()
    val targetLoss by viewModel.targetLoss.collectAsState()
    val isSimulating by viewModel.isSimulating.collectAsState()
    val currentPing by viewModel.currentPing.collectAsState()
    val pingHistory by viewModel.pingHistoryList.collectAsState()
    val packetLossActive by viewModel.packetLossActive.collectAsState()
    val currentLagReport by viewModel.currentLagReport.collectAsState()

    var selectedSubTab by remember { mutableStateOf(0) }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- Sub Tab Bar (UX Improvement: Segment and group to reduce clutter) ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF131124)),
            border = BorderStroke(1.dp, CardSpaceBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val tabs = listOf(
                    Triple(0, t("subtab_network"), Icons.Default.Speed),
                    Triple(1, t("subtab_fps"), Icons.Default.Bolt),
                    Triple(2, t("subtab_moba"), Icons.Default.CellTower),
                    Triple(3, t("subtab_sphere"), Icons.Default.Info)
                )
                tabs.forEach { (index, title, icon) ->
                    val isTabSelected = selectedSubTab == index
                    val tabBg = if (isTabSelected) {
                        Brush.linearGradient(colors = listOf(NeonPink.copy(alpha = 0.8f), NeonCyan.copy(alpha = 0.8f)))
                    } else {
                        Brush.linearGradient(colors = listOf(Color.Transparent, Color.Transparent))
                    }
                    val tabBorder = if (isTabSelected) BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f)) else null

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(tabBg)
                            .then(if (tabBorder != null) Modifier.border(tabBorder, RoundedCornerShape(8.dp)) else Modifier)
                            .clickable { selectedSubTab = index }
                            .padding(horizontal = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = title,
                                tint = if (isTabSelected) Color.White else Color.Gray,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = title,
                                color = if (isTabSelected) Color.White else Color.Gray,
                                fontSize = 11.sp,
                                fontWeight = if (isTabSelected) FontWeight.Bold else FontWeight.Medium,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }

        // Active Simulation Reminder/Shortcut for Games tab
        if (selectedSubTab > 0 && !isSimulating) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2D1B1B)),
                border = BorderStroke(1.dp, Color.Red.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Warning",
                        tint = Color.Red,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = t("not_simulating_warning"),
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = t("not_simulating_desc"),
                            color = Color.LightGray,
                            fontSize = 11.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { viewModel.toggleSimulation() },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text(t("sim_enable_now"), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        if (selectedSubTab == 0) {
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
                            text = t("app_name"),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonPink
                        )
                        Text(
                            text = t("app_description"),
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
                    text = t("sim_network_setup").uppercase(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonCyan
                )

                // Game selection
                var dropdownExpanded by remember { mutableStateOf(false) }
                Column {
                    Text(text = t("sim_game_target"), fontSize = 12.sp, color = Color.Gray)
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
                            Text(text = getLocalizedGameName(selectedGame), color = Color.White, fontWeight = FontWeight.SemiBold)
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
                                    text = { Text(text = getLocalizedGameName(game), color = Color.White) },
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
                        text = if (isSimulating) t("sim_stop") else t("sim_start"),
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
                        text = t("sim_live_status"),
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
                        text = t("sim_tip_chat_assistant"),
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
                            text = t("sim_report_lag_button"),
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
                                    text = t("sim_assistant_tips"),
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
        }

        if (selectedSubTab == 1) {
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

            val fpsCurrentTarget by viewModel.fpsCurrentTarget.collectAsState()
            val fpsHits by viewModel.fpsHits.collectAsState()
            val fpsShots by viewModel.fpsShots.collectAsState()
            val fpsMisses by viewModel.fpsMisses.collectAsState()
            val fpsLostShots by viewModel.fpsLostShots.collectAsState()
            val fpsBulletHoles by viewModel.fpsBulletHoles.collectAsState()
            val fpsShotVisuals by viewModel.fpsShotVisuals.collectAsState()
            val fpsDiagnosticReport by viewModel.fpsDiagnosticReport.collectAsState()
            val fpsDifficultyMultiplier by viewModel.fpsDifficultyMultiplier.collectAsState()
            val fpsDifficultyLevelName by viewModel.fpsDifficultyLevelName.collectAsState()
            val fpsIsZoomed by viewModel.fpsIsZoomed.collectAsState()
            val fpsGameMode by viewModel.fpsGameMode.collectAsState()
            val fpsBossHp by viewModel.fpsBossHp.collectAsState()
            val fpsUserHp by viewModel.fpsUserHp.collectAsState()
            val fpsBossState by viewModel.fpsBossState.collectAsState()
            val fpsWeapon by viewModel.fpsWeapon.collectAsState()
            val fpsZombies by viewModel.fpsZombies.collectAsState()

            val sphereList by viewModel.sphereList.collectAsState()
            val sphereFps by viewModel.sphereFps.collectAsState()
            val sphereGameState by viewModel.sphereGameState.collectAsState()
            val sphereCount by viewModel.sphereCount.collectAsState()

            if (fpsIsZoomed) {
                Dialog(
                    onDismissRequest = { viewModel.setFpsZoomed(false) },
                    properties = DialogProperties(
                        usePlatformDefaultWidth = false,
                        dismissOnBackPress = true,
                        dismissOnClickOutside = false
                    )
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(0.96f)
                            .wrapContentHeight()
                            .padding(8.dp)
                            .border(1.5.dp, NeonCyan, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0A0914))
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            // Dialog Title Bar
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Fullscreen,
                                        contentDescription = "Zoom Icon",
                                        tint = NeonCyan,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "PHÒNG TẬP BẮN PHÓNG TO & CỐ ĐỊNH (GIẢM TRỄ MẠNG)",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NeonCyan
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.1f))
                                        .clickable { viewModel.setFpsZoomed(false) }
                                        .padding(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Đóng",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            // Dynamic Difficulty Banner inside Dialog
                            AnimatedVisibility(visible = reflexState == "spawned" || reflexState == "delaying") {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(ElegantGold.copy(alpha = 0.1f))
                                        .border(1.dp, ElegantGold.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Bolt,
                                            contentDescription = "Dynamic Difficulty Icon",
                                            tint = ElegantGold,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Tốc Độ Bia Thích Ứng (Lag):",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = ElegantTextPrimary
                                        )
                                    }
                                    
                                    Text(
                                        text = "${"%.1f".format(fpsDifficultyMultiplier)}x ($fpsDifficultyLevelName)",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ElegantGold
                                    )
                                }
                            }

                            // Message Area inside Dialog
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
                                        fontSize = 14.sp,
                                        textAlign = TextAlign.Center
                                    )
                                    if (timerText.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = timerText,
                                            color = NeonCyan,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }

                            // Interactive Shooting Range Area inside Dialog with LARGER SIZE (Height: 350.dp!)
                            BoxWithConstraints(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(350.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF05050A))
                                    .border(1.5.dp, NeonCyan.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                    .pointerInput(reflexState) {
                                        if (reflexState == "spawned" || reflexState == "delaying") {
                                            detectTapGestures { offset ->
                                                viewModel.handleFpsShot(
                                                    tapX = offset.x,
                                                    tapY = offset.y,
                                                    boxWidth = size.width.toFloat(),
                                                    boxHeight = size.height.toFloat()
                                                )
                                            }
                                        }
                                    }
                            ) {
                                val boxWidthDp = maxWidth
                                val boxHeightDp = maxHeight

                                // Custom background image for mini game FPS (Zoomed Dialog)
                                Image(
                                    painter = painterResource(id = R.drawable.img_fps_bg),
                                    contentDescription = "FPS Background Zoomed",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize().alpha(0.55f)
                                )

                                // Draw Decorative futuristic grid inside dialog range
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    // Horizontal Grid
                                    for (i in 1..4) {
                                        val y = size.height * (i / 5f)
                                        drawLine(
                                            color = Color.Cyan.copy(alpha = 0.08f),
                                            start = Offset(0f, y),
                                            end = Offset(size.width, y),
                                            strokeWidth = 1.dp.toPx()
                                        )
                                    }
                                    // Vertical Grid
                                    for (i in 1..4) {
                                        val x = size.width * (i / 5f)
                                        drawLine(
                                            color = Color.Cyan.copy(alpha = 0.08f),
                                            start = Offset(x, 0f),
                                            end = Offset(x, size.height),
                                            strokeWidth = 1.dp.toPx()
                                        )
                                    }

                                    // Diagonal corner sights
                                    val lineLen = 16.dp.toPx()
                                    // Top Left
                                    drawLine(NeonCyan.copy(alpha = 0.5f), Offset(0f, 0f), Offset(lineLen, 0f), 2.5.dp.toPx())
                                    drawLine(NeonCyan.copy(alpha = 0.5f), Offset(0f, 0f), Offset(0f, lineLen), 2.5.dp.toPx())
                                    // Top Right
                                    drawLine(NeonCyan.copy(alpha = 0.5f), Offset(size.width, 0f), Offset(size.width - lineLen, 0f), 2.5.dp.toPx())
                                    drawLine(NeonCyan.copy(alpha = 0.5f), Offset(size.width, 0f), Offset(size.width, lineLen), 2.5.dp.toPx())
                                    // Bottom Left
                                    drawLine(NeonCyan.copy(alpha = 0.5f), Offset(0f, size.height), Offset(lineLen, size.height), 2.5.dp.toPx())
                                    drawLine(NeonCyan.copy(alpha = 0.5f), Offset(0f, size.height), Offset(0f, size.height - lineLen), 2.5.dp.toPx())
                                    // Bottom Right
                                    drawLine(NeonCyan.copy(alpha = 0.5f), Offset(size.width, size.height), Offset(size.width - lineLen, size.height), 2.5.dp.toPx())
                                    drawLine(NeonCyan.copy(alpha = 0.5f), Offset(size.width, size.height), Offset(size.width, size.height - lineLen), 2.5.dp.toPx())

                                    // Draw Bullet Holes (Bullet holes of the session)
                                    fpsBulletHoles.forEach { hole ->
                                        if (hole.isHit) {
                                            drawCircle(
                                                color = Color.Green.copy(alpha = 0.8f),
                                                radius = 6.dp.toPx(),
                                                center = Offset(hole.x, hole.y)
                                            )
                                            drawCircle(
                                                color = Color.White,
                                                radius = 2.5.dp.toPx(),
                                                center = Offset(hole.x, hole.y)
                                            )
                                        } else {
                                            drawCircle(
                                                color = NeonPink.copy(alpha = 0.4f),
                                                radius = 7.dp.toPx(),
                                                center = Offset(hole.x, hole.y)
                                            )
                                            drawCircle(
                                                color = Color(0xFF1E1E1E),
                                                radius = 3.5.dp.toPx(),
                                                center = Offset(hole.x, hole.y)
                                            )
                                        }
                                    }

                                    // Draw Laser Shot Tracers (active shots fired)
                                    fpsShotVisuals.forEach { tracer ->
                                        drawLine(
                                            color = NeonCyan,
                                            start = Offset(tracer.startX, tracer.startY),
                                            end = Offset(tracer.endX, tracer.endY),
                                            strokeWidth = 3.5.dp.toPx()
                                        )
                                        drawLine(
                                            color = NeonCyan.copy(alpha = 0.4f),
                                            start = Offset(tracer.startX, tracer.startY),
                                            end = Offset(tracer.endX, tracer.endY),
                                            strokeWidth = 9.dp.toPx()
                                        )
                                        drawCircle(
                                            color = NeonCyan,
                                            radius = 12.dp.toPx(),
                                            center = Offset(tracer.startX, tracer.startY)
                                        )
                                    }
                                }

                                // Game States overlay
                                when (reflexState) {
                                    "idle" -> {
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.spacedBy(12.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Speed,
                                                    contentDescription = "FPS Target Icon",
                                                    tint = NeonCyan,
                                                    modifier = Modifier.size(50.dp)
                                                )
                                                Button(
                                                    onClick = { viewModel.startReflexGame() },
                                                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                                                    shape = RoundedCornerShape(8.dp)
                                                ) {
                                                    Text(text = t("fps_start_btn"), color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                                }
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
                                                Text(text = t("fps_connecting"), color = Color.LightGray, fontSize = 13.sp)
                                            }
                                        }
                                    }
                                    "spawned", "delaying" -> {
                                        if (fpsGameMode == "zombie") {
                                            // Render all active zombies moving down in zoomed mode!
                                            fpsZombies.forEach { zombie ->
                                                val sizeFactor = zombie.sizeMultiplier
                                                val zombieSize = 48.dp * sizeFactor
                                                Box(
                                                    modifier = Modifier
                                                        .offset(
                                                            x = boxWidthDp * zombie.x - (zombieSize / 2),
                                                            y = boxHeightDp * zombie.y - (zombieSize / 2)
                                                        )
                                                        .size(zombieSize),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Canvas(modifier = Modifier.fillMaxSize()) {
                                                        val w = size.width
                                                        val h = size.height
                                                        val headRadius = size.minDimension * 0.42f
                                                        
                                                        // Zombie Head Glow/Shadow
                                                        drawCircle(
                                                            color = if (zombie.isBoss) Color(0xFF9333EA).copy(alpha = 0.35f) else Color(0xFF10B981).copy(alpha = 0.25f),
                                                            radius = headRadius * 1.2f
                                                        )
                                                        
                                                        // Head Oval
                                                        drawCircle(
                                                            color = if (zombie.isBoss) Color(0xFF7E22CE) else Color(0xFF047857),
                                                            radius = headRadius
                                                        )
                                                        
                                                        // Eyes
                                                        val eyeRadius = headRadius * 0.18f
                                                        drawCircle(
                                                            color = Color(0xFFEF4444),
                                                            radius = eyeRadius,
                                                            center = Offset(w * 0.33f, h * 0.43f)
                                                        )
                                                        drawCircle(
                                                            color = Color(0xFFEF4444),
                                                            radius = eyeRadius,
                                                            center = Offset(w * 0.67f, h * 0.43f)
                                                        )
                                                        // Pupils
                                                        drawCircle(
                                                            color = Color.White,
                                                            radius = eyeRadius * 0.4f,
                                                            center = Offset(w * 0.33f, h * 0.43f)
                                                        )
                                                        drawCircle(
                                                            color = Color.White,
                                                            radius = eyeRadius * 0.4f,
                                                            center = Offset(w * 0.67f, h * 0.43f)
                                                        )
                                                        
                                                        // Mouth
                                                        drawLine(
                                                            color = Color.Black,
                                                            start = Offset(w * 0.25f, h * 0.72f),
                                                            end = Offset(w * 0.75f, h * 0.72f),
                                                            strokeWidth = 2.5.dp.toPx()
                                                        )
                                                        
                                                        // Stitches
                                                        for (k in 1..4) {
                                                            val xOffset = w * (0.25f + k * 0.1f)
                                                            drawLine(
                                                                color = Color.Black,
                                                                start = Offset(xOffset, h * 0.65f),
                                                                end = Offset(xOffset + 2.dp.toPx(), h * 0.79f),
                                                                strokeWidth = 1.5.dp.toPx()
                                                            )
                                                        }
                                                    }
                                                    
                                                    // Health Bar
                                                    val maxHpOfZ = if (zombie.isBoss) 15f else 1f
                                                    val hpPercent = (zombie.hp / maxHpOfZ).coerceIn(0f, 1f)
                                                    Box(
                                                        modifier = Modifier
                                                            .align(Alignment.TopCenter)
                                                            .offset(y = (-14).dp)
                                                            .width(36.dp)
                                                            .height(5.dp)
                                                            .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(2.dp))
                                                            .border(0.5.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(2.dp))
                                                    ) {
                                                        Box(
                                                            modifier = Modifier
                                                                .fillMaxHeight()
                                                                .fillMaxWidth(hpPercent)
                                                                .background(if (zombie.isBoss) Color(0xFFA855F7) else Color(0xFFEF4444))
                                                        )
                                                    }

                                                    if (zombie.isBoss) {
                                                        Text(
                                                            text = t("fps_boss_tag"),
                                                            color = Color(0xFFF3E8FF),
                                                            fontSize = 7.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            modifier = Modifier
                                                                .align(Alignment.BottomCenter)
                                                                .offset(y = 10.dp)
                                                                .background(Color(0xFF6B21A8), RoundedCornerShape(3.dp))
                                                                .padding(horizontal = 4.dp, vertical = 1.dp)
                                                        )
                                                    }
                                                }
                                            }
                                        } else {
                                            val infiniteTransition = rememberInfiniteTransition(label = "pulse_dialog")
                                            val scale by infiniteTransition.animateFloat(
                                                initialValue = 0.95f,
                                                targetValue = 1.15f,
                                                animationSpec = infiniteRepeatable(
                                                    animation = tween(450),
                                                    repeatMode = RepeatMode.Reverse
                                                ),
                                                label = "scale_dialog"
                                            )

                                            val targetSize = when (fpsGameMode) {
                                                "boss" -> 84.dp
                                                "sniper" -> 28.dp
                                                "bottle" -> 64.dp
                                                else -> 56.dp
                                            }

                                            Box(
                                                modifier = Modifier
                                                    .offset(
                                                        x = boxWidthDp * targetX - (targetSize / 2),
                                                        y = boxHeightDp * targetY - (targetSize / 2)
                                                    )
                                                    .size(targetSize * scale),
                                                contentAlignment = Alignment.Center
                                            ) {
                                            when (fpsGameMode) {
                                                "boss" -> {
                                                    val bossState = fpsBossState
                                                    Canvas(modifier = Modifier.fillMaxSize()) {
                                                        val w = size.width
                                                        val h = size.height
                                                        val center = Offset(w / 2f, h / 2f)
                                                        val radius = size.minDimension / 2f
                                                        
                                                        // 1. Draw glowing aura based on boss state
                                                        val auraColor = when (bossState) {
                                                            "preparing" -> Color(0xFFFFB300) // Amber / Warning
                                                            "shooting" -> NeonPink
                                                            "hit" -> Color.Red
                                                            else -> Color(0xFF39FF14) // Alien Neon Green
                                                        }
                                                        
                                                        drawCircle(
                                                            color = auraColor.copy(alpha = 0.22f),
                                                            radius = radius * (if (bossState == "preparing") 1.25f else 1.0f)
                                                        )
                                                        
                                                        // Charging ring
                                                        if (bossState == "preparing") {
                                                            drawCircle(
                                                                color = auraColor,
                                                                radius = radius * 1.12f,
                                                                style = Stroke(width = 3.dp.toPx())
                                                            )
                                                        }
                                                        
                                                        // 2. Head & Antenna Outline and Base Colors
                                                        val headColor = if (bossState == "hit") Color(0xFFFF5722) else Color(0xFF2ECC71) // Hit: Orange/Red, Normal: Alien Green
                                                        val outlineColor = if (bossState == "hit") Color.White else Color(0xFF27AE60)
                                                        
                                                        // Draw Alien Antennae
                                                        // Left Antenna
                                                        drawLine(
                                                            color = outlineColor,
                                                            start = Offset(w * 0.4f, h * 0.35f),
                                                            end = Offset(w * 0.28f, h * 0.15f),
                                                            strokeWidth = 3.dp.toPx()
                                                        )
                                                        // Right Antenna
                                                        drawLine(
                                                            color = outlineColor,
                                                            start = Offset(w * 0.6f, h * 0.35f),
                                                            end = Offset(w * 0.72f, h * 0.15f),
                                                            strokeWidth = 3.dp.toPx()
                                                        )
                                                        
                                                        // Antenna Balls (glowing bulb)
                                                        val bulbColor = when (bossState) {
                                                            "preparing" -> Color(0xFFFFD700)
                                                            "shooting" -> NeonPink
                                                            "hit" -> Color.Red
                                                            else -> Color(0xFFF1C40F)
                                                        }
                                                        drawCircle(color = bulbColor, radius = radius * 0.12f, center = Offset(w * 0.28f, h * 0.15f))
                                                        drawCircle(color = outlineColor, radius = radius * 0.12f, center = Offset(w * 0.28f, h * 0.15f), style = Stroke(1.5.dp.toPx()))
                                                        drawCircle(color = bulbColor, radius = radius * 0.12f, center = Offset(w * 0.72f, h * 0.15f))
                                                        drawCircle(color = outlineColor, radius = radius * 0.12f, center = Offset(w * 0.72f, h * 0.15f), style = Stroke(1.5.dp.toPx()))
                                                        
                                                        // Main Head Oval (Alien shape: wider on top)
                                                        drawOval(
                                                            color = headColor,
                                                            topLeft = Offset(w * 0.15f, h * 0.28f),
                                                            size = Size(w * 0.7f, h * 0.58f)
                                                        )
                                                        drawOval(
                                                            color = outlineColor,
                                                            topLeft = Offset(w * 0.15f, h * 0.28f),
                                                            size = Size(w * 0.7f, h * 0.58f),
                                                            style = Stroke(width = 3.dp.toPx())
                                                        )
                                                        
                                                        // 3. Draw Eyes (Alien style, large, cute, slightly tilted)
                                                        if (bossState == "hit") {
                                                            // Dizzied cross eyes "X X"
                                                            val sizeEye = 8.dp.toPx()
                                                            // Left cross eye
                                                            val lx = w * 0.36f
                                                            val ly = h * 0.48f
                                                            drawLine(color = Color.White, start = Offset(lx - sizeEye, ly - sizeEye), end = Offset(lx + sizeEye, ly + sizeEye), strokeWidth = 2.5.dp.toPx())
                                                            drawLine(color = Color.White, start = Offset(lx + sizeEye, ly - sizeEye), end = Offset(lx - sizeEye, ly + sizeEye), strokeWidth = 2.5.dp.toPx())
                                                            
                                                            // Right cross eye
                                                            val rx = w * 0.64f
                                                            val ry = h * 0.48f
                                                            drawLine(color = Color.White, start = Offset(rx - sizeEye, ry - sizeEye), end = Offset(rx + sizeEye, ry + sizeEye), strokeWidth = 2.5.dp.toPx())
                                                            drawLine(color = Color.White, start = Offset(rx + sizeEye, ry - sizeEye), end = Offset(rx - sizeEye, ry + sizeEye), strokeWidth = 2.5.dp.toPx())
                                                        } else {
                                                            // Huge cute alien black eyes
                                                            // Left eye
                                                            drawOval(
                                                                color = Color.Black,
                                                                topLeft = Offset(w * 0.26f, h * 0.42f),
                                                                size = Size(w * 0.18f, h * 0.16f)
                                                            )
                                                            // Right eye
                                                            drawOval(
                                                                color = Color.Black,
                                                                topLeft = Offset(w * 0.56f, h * 0.42f),
                                                                size = Size(w * 0.18f, h * 0.16f)
                                                            )
                                                            
                                                            // White sparkle reflections
                                                            drawCircle(
                                                                color = Color.White,
                                                                radius = radius * 0.045f,
                                                                center = Offset(w * 0.32f, h * 0.47f)
                                                            )
                                                            drawCircle(
                                                                color = Color.White,
                                                                radius = radius * 0.045f,
                                                                center = Offset(w * 0.62f, h * 0.47f)
                                                            )
                                                        }
                                                        
                                                        // 4. Smile & Tongue (Goofy alien)
                                                        val mouthCenterY = h * 0.68f
                                                        if (bossState == "hit") {
                                                            // Funny wave / confused mouth
                                                            val wavePath = Path().apply {
                                                                moveTo(w * 0.4f, mouthCenterY)
                                                                quadraticBezierTo(w * 0.45f, mouthCenterY - 4.dp.toPx(), w * 0.5f, mouthCenterY)
                                                                quadraticBezierTo(w * 0.55f, mouthCenterY + 4.dp.toPx(), w * 0.6f, mouthCenterY)
                                                            }
                                                            drawPath(
                                                                path = wavePath,
                                                                color = Color.White,
                                                                style = Stroke(width = 2.5.dp.toPx())
                                                            )
                                                        } else {
                                                            // Big happy smile!
                                                            val smilePath = Path().apply {
                                                                moveTo(w * 0.36f, mouthCenterY)
                                                                quadraticBezierTo(w * 0.5f, mouthCenterY + 12.dp.toPx(), w * 0.64f, mouthCenterY)
                                                            }
                                                            drawPath(
                                                                path = smilePath,
                                                                color = Color.Black,
                                                                style = Stroke(width = 3.dp.toPx())
                                                            )
                                                            
                                                            // Tongue sticking out!
                                                            val tongueRect = androidx.compose.ui.geometry.Rect(
                                                                left = w * 0.46f,
                                                                top = mouthCenterY + 3.dp.toPx(),
                                                                right = w * 0.54f,
                                                                bottom = mouthCenterY + 11.dp.toPx()
                                                            )
                                                            drawRoundRect(
                                                                color = Color(0xFFFF5E7E), // Cheeky Pink Tongue
                                                                topLeft = Offset(tongueRect.left, tongueRect.top),
                                                                size = Size(tongueRect.width, tongueRect.height),
                                                                cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                                                            )
                                                        }
                                                    }
                                                }
                                                "bottle" -> {
                                                    Canvas(modifier = Modifier.fillMaxSize()) {
                                                        val w = size.width
                                                        val h = size.height
                                                        val glassColor = Color(0xFF4DEEEA)
                                                        drawRoundRect(
                                                            color = glassColor,
                                                            topLeft = Offset(w * 0.38f, h * 0.05f),
                                                            size = Size(w * 0.24f, h * 0.35f),
                                                            cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                                                        )
                                                        drawRect(
                                                            color = NeonPink,
                                                            topLeft = Offset(w * 0.34f, h * 0.02f),
                                                            size = Size(w * 0.32f, h * 0.06f)
                                                        )
                                                        drawRoundRect(
                                                            color = glassColor,
                                                            topLeft = Offset(w * 0.2f, h * 0.35f),
                                                            size = Size(w * 0.6f, h * 0.6f),
                                                            cornerRadius = CornerRadius(10.dp.toPx(), 10.dp.toPx())
                                                        )
                                                        drawRect(
                                                            color = Color.White,
                                                            topLeft = Offset(w * 0.28f, h * 0.48f),
                                                            size = Size(w * 0.44f, h * 0.2f)
                                                        )
                                                        drawLine(
                                                            color = Color.Red,
                                                            start = Offset(w * 0.32f, h * 0.58f),
                                                            end = Offset(w * 0.68f, h * 0.58f),
                                                            strokeWidth = 2.dp.toPx()
                                                        )
                                                    }
                                                }
                                                "sniper" -> {
                                                    Canvas(modifier = Modifier.fillMaxSize()) {
                                                        val radius = size.minDimension / 2f
                                                        drawCircle(color = NeonCyan, radius = radius, style = Stroke(1.5.dp.toPx()))
                                                        drawCircle(color = NeonCyan, radius = radius * 0.5f, style = Stroke(1.dp.toPx()))
                                                        drawCircle(color = Color.Red, radius = radius * 0.15f)
                                                        drawLine(color = NeonCyan, start = Offset(0f, size.height / 2f), end = Offset(size.width, size.height / 2f), strokeWidth = 1.dp.toPx())
                                                        drawLine(color = NeonCyan, start = Offset(size.width / 2f, 0f), end = Offset(size.width / 2f, size.height), strokeWidth = 1.dp.toPx())
                                                    }
                                                }
                                                "fast" -> {
                                                    Canvas(modifier = Modifier.fillMaxSize()) {
                                                        val radius = size.minDimension / 2f
                                                        drawCircle(color = Color(0xFFFFD700), radius = radius, style = Stroke(2.dp.toPx()))
                                                        drawCircle(color = Color.Red, radius = radius * 0.7f, style = Stroke(1.5.dp.toPx()))
                                                        drawCircle(color = Color.Yellow, radius = radius * 0.35f)
                                                        drawLine(
                                                            color = Color(0xFFFFD700).copy(alpha = 0.8f),
                                                            start = Offset(-12.dp.toPx(), size.height / 2f),
                                                            end = Offset(-2.dp.toPx(), size.height / 2f),
                                                            strokeWidth = 3.dp.toPx()
                                                        )
                                                        drawLine(
                                                            color = Color(0xFFFFD700).copy(alpha = 0.5f),
                                                            start = Offset(-22.dp.toPx(), size.height * 0.4f),
                                                            end = Offset(-8.dp.toPx(), size.height * 0.4f),
                                                            strokeWidth = 2.dp.toPx()
                                                        )
                                                    }
                                                }
                                                else -> {
                                                    Canvas(modifier = Modifier.fillMaxSize()) {
                                                        val radius = size.minDimension / 2f
                                                        drawCircle(color = Color.Red, radius = radius)
                                                        drawCircle(color = Color.White, radius = radius * 0.65f)
                                                        drawCircle(color = Color.Red, radius = radius * 0.4f)
                                                        drawCircle(color = Color.Yellow, radius = radius * 0.15f)
                                                    }
                                                }
                                            }
                                            if (reflexState == "delaying") {
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(4.dp))
                                                        .background(NeonPink)
                                                        .padding(horizontal = 6.dp, vertical = 3.dp)
                                                ) {
                                                    Text(
                                                        text = "SENDING...",
                                                        fontSize = 8.sp,
                                                        color = Color.White,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                        }
                                        }
                                    }
                                    "finished" -> {
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.spacedBy(10.dp)
                                            ) {
                                                Text(
                                                    text = t("fps_game_complete"),
                                                    color = ElegantGold,
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Button(
                                                    onClick = { viewModel.resetReflexGame() },
                                                    colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                                                    shape = RoundedCornerShape(8.dp)
                                                ) {
                                                    Icon(imageVector = Icons.Default.Refresh, contentDescription = "Retry Icon")
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text(text = "Tập Bắn Lại", fontSize = 13.sp)
                                                }
                                            }
                                        }
                                    }
                                }

                                // Floating small 'x' button on the top right corner of the interactive shooting range
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(8.dp)
                                        .clip(CircleShape)
                                        .background(Color.Black.copy(alpha = 0.7f))
                                        .border(1.dp, NeonPink, CircleShape)
                                        .clickable { viewModel.setFpsZoomed(false) }
                                        .padding(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Đóng phòng tập phóng to",
                                        tint = NeonPink,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                val fpsIsPaused by viewModel.fpsIsPaused.collectAsState()

                                // Floating Pause button for Zoomed FPS
                                if (reflexState == "spawned" || reflexState == "delaying") {
                                    IconButton(
                                        onClick = { viewModel.setFpsPaused(true) },
                                        modifier = Modifier
                                            .align(Alignment.TopStart) // Since small close 'x' is at TopEnd, TopStart is perfect!
                                            .padding(8.dp)
                                            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                                            .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                                            .size(36.dp)
                                            .testTag("fps_pause_button_zoomed")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Pause,
                                            contentDescription = "Tạm Dừng FPS",
                                            tint = NeonPink,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }

                                // Zoomed FPS Pause Overlay
                                if (fpsIsPaused) {
                                    val targetPing by viewModel.targetPing.collectAsState()
                                    val targetJitter by viewModel.targetJitter.collectAsState()
                                    val targetLoss by viewModel.targetLoss.collectAsState()
                                    
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color.Black.copy(alpha = 0.9f))
                                            .pointerInput(Unit) { detectTapGestures { } },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.spacedBy(16.dp),
                                            modifier = Modifier.padding(24.dp)
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Pause,
                                                    contentDescription = "Game Paused",
                                                    tint = NeonPink,
                                                    modifier = Modifier.size(28.dp)
                                                )
                                                Text(
                                                    text = t("fps_paused_overlay_title"),
                                                    color = NeonPink,
                                                    fontSize = 18.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    letterSpacing = 1.sp
                                                )
                                            }
                                            
                                            Text(
                                                text = "Cấu hình mạng hiện tại:",
                                                color = Color.LightGray,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .background(Color(0xFF151324), RoundedCornerShape(8.dp))
                                                    .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                                                    .padding(12.dp),
                                                verticalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Text("Độ trễ (Ping):", color = Color.Gray, fontSize = 11.sp)
                                                    Text("${targetPing} ms", color = NeonCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                }
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Text("Biến động (Jitter):", color = Color.Gray, fontSize = 11.sp)
                                                    Text("${targetJitter} ms", color = ElegantGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                }
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Text("Mất gói (Loss):", color = Color.Gray, fontSize = 11.sp)
                                                    Text("${targetLoss} %", color = NeonPink, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }

                                            // Button to Reset delay/latency
                                            Button(
                                                onClick = { 
                                                    viewModel.resetNetworkDelay()
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Refresh,
                                                    contentDescription = "Reset Network",
                                                    tint = Color.White,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(t("fps_paused_reset_network_btn"), color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            }

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                                            ) {
                                                // Resume Button
                                                Button(
                                                    onClick = { viewModel.setFpsPaused(false) },
                                                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                                                    shape = RoundedCornerShape(8.dp),
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.PlayArrow,
                                                        contentDescription = "Resume",
                                                        tint = Color.Black,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text(t("fps_paused_resume_btn"), color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                                }

                                                // Exit Button
                                                Button(
                                                    onClick = { 
                                                        viewModel.resetReflexGame()
                                                        viewModel.setFpsPaused(false)
                                                    },
                                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.8f)),
                                                    shape = RoundedCornerShape(8.dp),
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Stop,
                                                        contentDescription = "Exit",
                                                        tint = Color.White,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text(t("fps_paused_exit_btn"), color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            // Diagnostics report displayed inside Zoomed Dialog
                            if (reflexState == "finished" && fpsDiagnosticReport != null) {
                                fpsDiagnosticReport?.let { report ->
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color(0xFF141322), RoundedCornerShape(12.dp))
                                            .border(1.dp, ElegantGold.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                            .padding(14.dp),
                                        verticalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = t("fps_report_title"),
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = ElegantGold
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(
                                                        if (report.accuracy >= 80 && report.networkPingSimulated < 50) Color(0xFF143026) else Color(0xFF421521)
                                                    )
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = if (report.accuracy >= 80 && report.networkPingSimulated < 50) t("fps_report_status_smooth") else t("fps_report_status_affected"),
                                                    fontSize = 9.sp,
                                                    color = if (report.accuracy >= 80 && report.networkPingSimulated < 50) Color.Green else Color.Red,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }

                                        Column {
                                            Text(t("fps_report_primary_diagnosis"), fontSize = 10.sp, color = Color.Gray)
                                            Text(
                                                text = getLocalizedText(report.mainIssue),
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Black,
                                                color = if (report.networkPingSimulated > 100 || report.networkLossSimulated > 10) NeonPink else NeonCyan
                                            )
                                        }

                                        // Simple statistics grid in dialog
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            listOf(
                                                t("fps_report_accuracy_label") to "${report.accuracy}%" to t("fps_report_target_fraction").replace("%d/%d", "${report.hits}/${report.totalTargets}").replace("%d", report.hits.toString()),
                                                t("fps_report_physical_reflex_label") to "${report.avgPhysicalResponseMs}ms" to t("fps_report_pure_physical"),
                                                t("fps_report_total_response_label") to "${report.avgWithNetworkResponseMs}ms" to t("fps_report_includes_lag")
                                            ).forEach { item ->
                                                Card(
                                                    modifier = Modifier.weight(1f),
                                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1D31)),
                                                    border = BorderStroke(1.dp, CardSpaceBorder)
                                                ) {
                                                    Column(
                                                        modifier = Modifier.padding(8.dp),
                                                        horizontalAlignment = Alignment.CenterHorizontally
                                                    ) {
                                                        Text(item.first.first, fontSize = 9.sp, color = Color.Gray)
                                                        Text(item.first.second, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                                        Text(item.second, fontSize = 8.sp, color = Color.LightGray)
                                                    }
                                                }
                                            }
                                        }

                                        if (report.lostShotsCount > 0) {
                                            Text(
                                                text = t("fps_report_packet_loss_warn_short").replace("%d", report.lostShotsCount.toString()),
                                                fontSize = 10.sp,
                                                color = Color.Red,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }

                                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                            Text(text = t("fps_report_linh_chi_assessment"), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NeonPink)
                                            Text(text = getLocalizedText(report.linhChiEvaluation), fontSize = 11.sp, color = ElegantTextPrimary)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = t("fps_2d_title"),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonCyan
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(NeonPink.copy(alpha = 0.2f))
                                .padding(horizontal = 5.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = t("fps_new_badge"),
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonPink
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        // Fullscreen Toggle Button
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (fpsIsZoomed) NeonCyan.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.1f))
                                .border(1.dp, if (fpsIsZoomed) NeonCyan else Color.Transparent, RoundedCornerShape(6.dp))
                                .clickable { viewModel.setFpsZoomed(!fpsIsZoomed) }
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(
                                    imageVector = if (fpsIsZoomed) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                                    contentDescription = "Zoom Toggle",
                                    tint = if (fpsIsZoomed) NeonCyan else Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = if (fpsIsZoomed) t("fps_zoom_minimize") else t("fps_zoom_maximize"),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (fpsIsZoomed) NeonCyan else Color.White
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = "Speed Test Icon",
                            tint = NeonCyan
                        )
                    }
                }

                Text(
                    text = t("fps_2d_desc"),
                    fontSize = 12.sp,
                    color = Color.LightGray
                )

                // Game Mode Selector
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = t("fps_select_mode_label"),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            "classic" to t("fps_mode_classic"),
                            "bottle" to t("fps_mode_bottle"),
                            "fast" to t("fps_mode_fast"),
                            "sniper" to t("fps_mode_sniper"),
                            "boss" to t("fps_mode_boss"),
                            "zombie" to t("fps_mode_zombie")
                        ).forEach { (modeKey, modeLabel) ->
                            val isSelected = fpsGameMode == modeKey
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) NeonCyan.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.05f))
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) NeonCyan else Color.White.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable(enabled = reflexState == "idle") {
                                        viewModel.setFpsGameMode(modeKey)
                                    }
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = modeLabel,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Normal,
                                    color = if (isSelected) NeonCyan else Color.LightGray,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }

                // Weapon Selector
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = t("fps_select_weapon_label"),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            "pistol" to t("fps_weapon_pistol"),
                            "ak47" to t("fps_weapon_ak47"),
                            "shotgun" to t("fps_weapon_shotgun"),
                            "sniper" to t("fps_weapon_sniper")
                        ).forEach { (weaponKey, weaponLabel) ->
                            val isSelected = fpsWeapon == weaponKey
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) NeonPink.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.05f))
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) NeonPink else Color.White.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable {
                                        viewModel.setFpsWeapon(weaponKey)
                                    }
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = weaponLabel,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Normal,
                                    color = if (isSelected) NeonPink else Color.LightGray,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }

                if (fpsIsZoomed) {
                    // Render the placeholder inside the normal screen card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF0F0E1C))
                            .border(1.dp, NeonCyan.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Fullscreen,
                                contentDescription = "Zoom Active",
                                tint = NeonCyan,
                                modifier = Modifier.size(48.dp)
                            )
                            Text(
                                text = t("fps_zoom_title"),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = t("fps_zoom_desc"),
                                fontSize = 11.sp,
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )
                            Button(
                                onClick = { viewModel.setFpsZoomed(true) },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = t("fps_zoom_reopen_btn"),
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                } else {
                    // Dynamic Difficulty Banner based on Lag
                    AnimatedVisibility(visible = reflexState == "spawned" || reflexState == "delaying") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(ElegantGold.copy(alpha = 0.1f))
                            .border(1.dp, ElegantGold.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = "Dynamic Difficulty Icon",
                                tint = ElegantGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = t("fps_difficulty_adaptive"),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ElegantTextPrimary
                            )
                        }
                        
                        Text(
                            text = "${"%.1f".format(fpsDifficultyMultiplier)}x (${getLocalizedText(fpsDifficultyLevelName)})",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = ElegantGold
                        )
                    }
                }

                // Message Area
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1B1A2F), RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = getLocalizedText(reflexMsg),
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

                // Interactive Shooting Range Area
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(230.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF0C0B14))
                        .border(1.dp, CardSpaceBorder, RoundedCornerShape(12.dp))
                        .pointerInput(reflexState) {
                            if (reflexState == "spawned" || reflexState == "delaying") {
                                detectTapGestures { offset ->
                                    viewModel.handleFpsShot(
                                        tapX = offset.x,
                                        tapY = offset.y,
                                        boxWidth = size.width.toFloat(),
                                        boxHeight = size.height.toFloat()
                                    )
                                }
                            }
                        }
                ) {
                    val boxWidthDp = maxWidth
                    val boxHeightDp = maxHeight

                    // Custom background image for mini game FPS (Inline)
                    Image(
                        painter = painterResource(id = R.drawable.img_fps_bg),
                        contentDescription = "FPS Background",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize().alpha(0.5f)
                    )

                    // Boss Battle Health HUD
                    if (fpsGameMode == "boss" && (reflexState == "spawned" || reflexState == "delaying")) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .align(Alignment.TopCenter),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            // Boss HP bar
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(0.92f)
                                    .background(Color.Black.copy(alpha = 0.65f), RoundedCornerShape(4.dp))
                                    .border(0.5.dp, Color.Red.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text("👾 ALIEN HP", color = Color.Red, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                        for (i in 1..5) {
                                            Box(
                                                modifier = Modifier
                                                    .size(width = 12.dp, height = 6.dp)
                                                    .background(if (i <= fpsBossHp) Color.Red else Color.DarkGray, RoundedCornerShape(1.dp))
                                            )
                                        }
                                    }
                                }
                                Text("${fpsBossHp}/5", color = Color.Red, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                            
                            // User HP bar
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(0.92f)
                                    .background(Color.Black.copy(alpha = 0.65f), RoundedCornerShape(4.dp))
                                    .border(0.5.dp, NeonCyan.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text("🛡️ YOUR HP", color = NeonCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                        for (i in 1..3) {
                                            Box(
                                                modifier = Modifier
                                                    .size(width = 16.dp, height = 6.dp)
                                                    .background(if (i <= fpsUserHp) NeonCyan else Color.DarkGray, RoundedCornerShape(1.dp))
                                            )
                                        }
                                    }
                                }
                                Text("${fpsUserHp}/3", color = NeonCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // 1. Draw decorative futuristic grid inside range
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        // Horizontal Grid
                        for (i in 1..4) {
                            val y = size.height * (i / 5f)
                            drawLine(
                                color = Color.Gray.copy(alpha = 0.12f),
                                start = Offset(0f, y),
                                end = Offset(size.width, y),
                                strokeWidth = 1.dp.toPx()
                            )
                        }
                        // Vertical Grid
                        for (i in 1..4) {
                            val x = size.width * (i / 5f)
                            drawLine(
                                color = Color.Gray.copy(alpha = 0.12f),
                                start = Offset(x, 0f),
                                end = Offset(x, size.height),
                                strokeWidth = 1.dp.toPx()
                            )
                        }

                        // Diagonal corner sights
                        val lineLen = 12.dp.toPx()
                        // Top Left
                        drawLine(Color.DarkGray, Offset(0f, 0f), Offset(lineLen, 0f), 2.dp.toPx())
                        drawLine(Color.DarkGray, Offset(0f, 0f), Offset(0f, lineLen), 2.dp.toPx())
                        // Top Right
                        drawLine(Color.DarkGray, Offset(size.width, 0f), Offset(size.width - lineLen, 0f), 2.dp.toPx())
                        drawLine(Color.DarkGray, Offset(size.width, 0f), Offset(size.width, lineLen), 2.dp.toPx())
                        // Bottom Left
                        drawLine(Color.DarkGray, Offset(0f, size.height), Offset(lineLen, size.height), 2.dp.toPx())
                        drawLine(Color.DarkGray, Offset(0f, size.height), Offset(0f, size.height - lineLen), 2.dp.toPx())
                        // Bottom Right
                        drawLine(Color.DarkGray, Offset(size.width, size.height), Offset(size.width - lineLen, size.height), 2.dp.toPx())
                        drawLine(Color.DarkGray, Offset(size.width, size.height), Offset(size.width, size.height - lineLen), 2.dp.toPx())

                        // 2. Draw Bullet Holes (Bullet holes of the session)
                        fpsBulletHoles.forEach { hole ->
                            if (hole.isHit) {
                                // Hit: Draw a bright neon green impact spark
                                drawCircle(
                                    color = Color.Green.copy(alpha = 0.8f),
                                    radius = 5.dp.toPx(),
                                    center = Offset(hole.x, hole.y)
                                )
                                drawCircle(
                                    color = Color.White,
                                    radius = 2.dp.toPx(),
                                    center = Offset(hole.x, hole.y)
                                )
                            } else {
                                // Miss: Draw a dark-gray bullet impact with neon pink glow
                                drawCircle(
                                    color = NeonPink.copy(alpha = 0.4f),
                                    radius = 6.dp.toPx(),
                                    center = Offset(hole.x, hole.y)
                                )
                                drawCircle(
                                    color = Color(0xFF1E1E1E),
                                    radius = 3.dp.toPx(),
                                    center = Offset(hole.x, hole.y)
                                )
                            }
                        }

                        // 3. Draw Laser Shot Tracers (active shots fired)
                        fpsShotVisuals.forEach { tracer ->
                            drawLine(
                                color = NeonCyan,
                                start = Offset(tracer.startX, tracer.startY),
                                end = Offset(tracer.endX, tracer.endY),
                                strokeWidth = 3.dp.toPx()
                            )
                            // Draw an outer glow tracer
                            drawLine(
                                color = NeonCyan.copy(alpha = 0.4f),
                                start = Offset(tracer.startX, tracer.startY),
                                end = Offset(tracer.endX, tracer.endY),
                                strokeWidth = 8.dp.toPx()
                            )
                            // Muzzle flash circle at source
                            drawCircle(
                                color = NeonCyan,
                                radius = 10.dp.toPx(),
                                center = Offset(tracer.startX, tracer.startY)
                            )
                            drawCircle(
                                color = Color.White,
                                radius = 5.dp.toPx(),
                                center = Offset(tracer.startX, tracer.startY)
                            )
                        }
                    }

                    // 3.5 Interactive Sci-Fi Gun pointing forward with dynamic Muzzle Flash
                    if (reflexState == "spawned" || reflexState == "delaying" || reflexState == "idle") {
                        val isFiring = fpsShotVisuals.isNotEmpty()
                        
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .offset(y = 12.dp)
                                .width(90.dp)
                                .height(80.dp),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            // Metal Gun Barrel pointing upward (forward)
                            Box(
                                modifier = Modifier
                                    .width(16.dp)
                                    .height(55.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color(0xFF64748B), // Slate blue metallic
                                                Color(0xFF334155),
                                                Color(0xFF1E293B)
                                            )
                                        )
                                    )
                                    .border(1.5.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(4.dp))
                            )
                            
                            // Laser indicator light on top of the barrel
                            Box(
                                modifier = Modifier
                                    .padding(top = 8.dp)
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(Color.Red)
                            )
                            
                            // Futuristic Heavy Stock/Receiver at bottom of screen
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .width(50.dp)
                                    .height(42.dp)
                                    .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp))
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color(0xFF475569),
                                                Color(0xFF0F172A)
                                            )
                                        )
                                    )
                                    .border(1.5.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp))
                            ) {
                                // Plasma core reactor glow inside gun body
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .width(22.dp)
                                        .height(7.dp)
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(if (isFiring) Color.Yellow else NeonCyan.copy(alpha = 0.7f))
                                )
                            }
                            
                            // Bright Dynamic Muzzle Flash flare when shooting
                            if (isFiring) {
                                Box(
                                    modifier = Modifier
                                        .offset(y = (-24).dp)
                                        .size(64.dp)
                                        .drawBehind {
                                            // Golden-orange energy blast burst radiating outwards
                                            drawCircle(
                                                brush = Brush.radialGradient(
                                                    colors = listOf(
                                                        Color.White,
                                                        Color(0xFFFBBF24), // Vivid gold yellow
                                                        Color(0xFFF97316), // Vivid orange
                                                        Color.Transparent
                                                    )
                                                ),
                                                radius = size.width / 2f
                                            )
                                        }
                                )
                            }
                        }
                    }

                    // 4. Game Screen States overlay
                    when (reflexState) {
                        "idle" -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Speed,
                                        contentDescription = "FPS Target Icon",
                                        tint = NeonCyan,
                                        modifier = Modifier.size(40.dp)
                                    )
                                    Button(
                                        onClick = { viewModel.startReflexGame() },
                                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.testTag("reflex_start_button")
                                    ) {
                                        Text(text = t("fps_start_btn"), color = Color.Black, fontWeight = FontWeight.Bold)
                                    }
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
                                    Text(text = t("fps_connecting"), color = Color.LightGray, fontSize = 12.sp)
                                }
                            }
                        }
                        "spawned", "delaying" -> {
                            if (fpsGameMode == "zombie") {
                                // Render all active zombies moving down
                                fpsZombies.forEach { zombie ->
                                    val sizeFactor = zombie.sizeMultiplier
                                    val zombieSize = 48.dp * sizeFactor
                                    Box(
                                        modifier = Modifier
                                            .offset(
                                                x = boxWidthDp * zombie.x - (zombieSize / 2),
                                                y = boxHeightDp * zombie.y - (zombieSize / 2)
                                            )
                                            .size(zombieSize),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Canvas(modifier = Modifier.fillMaxSize()) {
                                            val w = size.width
                                            val h = size.height
                                            val headRadius = size.minDimension * 0.42f
                                            
                                            // Zombie Head Glow/Shadow
                                            drawCircle(
                                                color = if (zombie.isBoss) Color(0xFF9333EA).copy(alpha = 0.35f) else Color(0xFF10B981).copy(alpha = 0.25f),
                                                radius = headRadius * 1.2f
                                            )
                                            
                                            // Head Oval
                                            drawCircle(
                                                color = if (zombie.isBoss) Color(0xFF7E22CE) else Color(0xFF047857), // purple for boss, green for normal
                                                radius = headRadius
                                            )
                                            
                                            // Eyes (large glowing red/white eyes)
                                            val eyeRadius = headRadius * 0.18f
                                            drawCircle(
                                                color = Color(0xFFEF4444),
                                                radius = eyeRadius,
                                                center = Offset(w * 0.33f, h * 0.43f)
                                            )
                                            drawCircle(
                                                color = Color(0xFFEF4444),
                                                radius = eyeRadius,
                                                center = Offset(w * 0.67f, h * 0.43f)
                                            )
                                            // Pupils
                                            drawCircle(
                                                color = Color.White,
                                                radius = eyeRadius * 0.4f,
                                                center = Offset(w * 0.33f, h * 0.43f)
                                            )
                                            drawCircle(
                                                color = Color.White,
                                                radius = eyeRadius * 0.4f,
                                                center = Offset(w * 0.67f, h * 0.43f)
                                            )
                                            
                                            // Mouth (grotesque or stitches)
                                            drawLine(
                                                color = Color.Black,
                                                start = Offset(w * 0.25f, h * 0.72f),
                                                end = Offset(w * 0.75f, h * 0.72f),
                                                strokeWidth = 2.5.dp.toPx()
                                            )
                                            
                                            // Stitches
                                            for (k in 1..4) {
                                                val xOffset = w * (0.25f + k * 0.1f)
                                                drawLine(
                                                    color = Color.Black,
                                                    start = Offset(xOffset, h * 0.65f),
                                                    end = Offset(xOffset + 2.dp.toPx(), h * 0.79f),
                                                    strokeWidth = 1.5.dp.toPx()
                                                )
                                            }
                                        }
                                        
                                        // Health Bar
                                        val maxHpOfZ = if (zombie.isBoss) 15f else 1f
                                        val hpPercent = (zombie.hp / maxHpOfZ).coerceIn(0f, 1f)
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.TopCenter)
                                                .offset(y = (-14).dp)
                                                .width(36.dp)
                                                .height(5.dp)
                                                .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(2.dp))
                                                .border(0.5.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(2.dp))
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxHeight()
                                                    .fillMaxWidth(hpPercent)
                                                    .background(if (zombie.isBoss) Color(0xFFA855F7) else Color(0xFFEF4444))
                                            )
                                        }

                                        // Text badge for Boss Zombie
                                        if (zombie.isBoss) {
                                            Text(
                                                text = t("fps_boss_tag"),
                                                color = Color(0xFFF3E8FF),
                                                fontSize = 7.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier
                                                    .align(Alignment.BottomCenter)
                                                    .offset(y = 12.dp)
                                                    .background(Color(0xFF7E22CE), RoundedCornerShape(3.dp))
                                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                                            )
                                        }
                                    }
                                }
                            } else {
                                // Render target bullseye
                                val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                                val scale by infiniteTransition.animateFloat(
                                    initialValue = 0.95f,
                                    targetValue = 1.15f,
                                    animationSpec = infiniteRepeatable(
                                        animation = tween(450),
                                        repeatMode = RepeatMode.Reverse
                                    ),
                                    label = "scale"
                                )

                                val targetSize = when (fpsGameMode) {
                                    "boss" -> 76.dp
                                    "sniper" -> 24.dp
                                    "bottle" -> 56.dp
                                    else -> 48.dp
                                }

                                Box(
                                    modifier = Modifier
                                        .offset(
                                            x = boxWidthDp * targetX - (targetSize / 2),
                                            y = boxHeightDp * targetY - (targetSize / 2)
                                        )
                                        .size(targetSize * scale),
                                    contentAlignment = Alignment.Center
                                ) {
                                    when (fpsGameMode) {
                                        "boss" -> {
                                            val bossState = fpsBossState
                                            Canvas(modifier = Modifier.fillMaxSize()) {
                                                val w = size.width
                                                val h = size.height
                                                val center = Offset(w / 2f, h / 2f)
                                                val radius = size.minDimension / 2f
                                                
                                                // 1. Draw glowing aura based on boss state
                                                val auraColor = when (bossState) {
                                                    "preparing" -> Color(0xFFFFB300) // Amber / Warning
                                                    "shooting" -> NeonPink
                                                    "hit" -> Color.Red
                                                    else -> Color(0xFF39FF14) // Alien Neon Green
                                                }
                                                
                                                drawCircle(
                                                    color = auraColor.copy(alpha = 0.22f),
                                                    radius = radius * (if (bossState == "preparing") 1.25f else 1.0f)
                                                )
                                                
                                                // Charging ring
                                                if (bossState == "preparing") {
                                                    drawCircle(
                                                        color = auraColor,
                                                        radius = radius * 1.12f,
                                                        style = Stroke(width = 3.dp.toPx())
                                                    )
                                                }
                                                
                                                // 2. Head & Antenna Outline and Base Colors
                                                val headColor = if (bossState == "hit") Color(0xFFFF5722) else Color(0xFF2ECC71) // Hit: Orange/Red, Normal: Alien Green
                                                val outlineColor = if (bossState == "hit") Color.White else Color(0xFF27AE60)
                                                
                                                // Draw Alien Antennae
                                                // Left Antenna
                                                drawLine(
                                                    color = outlineColor,
                                                    start = Offset(w * 0.4f, h * 0.35f),
                                                    end = Offset(w * 0.28f, h * 0.15f),
                                                    strokeWidth = 3.dp.toPx()
                                                )
                                                // Right Antenna
                                                drawLine(
                                                    color = outlineColor,
                                                    start = Offset(w * 0.6f, h * 0.35f),
                                                    end = Offset(w * 0.72f, h * 0.15f),
                                                    strokeWidth = 3.dp.toPx()
                                                )
                                                
                                                // Antenna Balls (glowing bulb)
                                                val bulbColor = when (bossState) {
                                                    "preparing" -> Color(0xFFFFD700)
                                                    "shooting" -> NeonPink
                                                    "hit" -> Color.Red
                                                    else -> Color(0xFFF1C40F)
                                                }
                                                drawCircle(color = bulbColor, radius = radius * 0.12f, center = Offset(w * 0.28f, h * 0.15f))
                                                drawCircle(color = outlineColor, radius = radius * 0.12f, center = Offset(w * 0.28f, h * 0.15f), style = Stroke(1.5.dp.toPx()))
                                                drawCircle(color = bulbColor, radius = radius * 0.12f, center = Offset(w * 0.72f, h * 0.15f))
                                                drawCircle(color = outlineColor, radius = radius * 0.12f, center = Offset(w * 0.72f, h * 0.15f), style = Stroke(1.5.dp.toPx()))
                                                
                                                // Main Head Oval (Alien shape: wider on top)
                                                drawOval(
                                                    color = headColor,
                                                    topLeft = Offset(w * 0.15f, h * 0.28f),
                                                    size = Size(w * 0.7f, h * 0.58f)
                                                )
                                                drawOval(
                                                    color = outlineColor,
                                                    topLeft = Offset(w * 0.15f, h * 0.28f),
                                                    size = Size(w * 0.7f, h * 0.58f),
                                                    style = Stroke(width = 3.dp.toPx())
                                                )
                                                
                                                // 3. Draw Eyes (Alien style, large, cute, slightly tilted)
                                                if (bossState == "hit") {
                                                    // Dizzied cross eyes "X X"
                                                    val sizeEye = 8.dp.toPx()
                                                    // Left cross eye
                                                    val lx = w * 0.36f
                                                    val ly = h * 0.48f
                                                    drawLine(color = Color.White, start = Offset(lx - sizeEye, ly - sizeEye), end = Offset(lx + sizeEye, ly + sizeEye), strokeWidth = 2.5.dp.toPx())
                                                    drawLine(color = Color.White, start = Offset(lx + sizeEye, ly - sizeEye), end = Offset(lx - sizeEye, ly + sizeEye), strokeWidth = 2.5.dp.toPx())
                                                    
                                                    // Right cross eye
                                                    val rx = w * 0.64f
                                                    val ry = h * 0.48f
                                                    drawLine(color = Color.White, start = Offset(rx - sizeEye, ry - sizeEye), end = Offset(rx + sizeEye, ry + sizeEye), strokeWidth = 2.5.dp.toPx())
                                                    drawLine(color = Color.White, start = Offset(rx + sizeEye, ry - sizeEye), end = Offset(rx - sizeEye, ry + sizeEye), strokeWidth = 2.5.dp.toPx())
                                                } else {
                                                    // Huge cute alien black eyes
                                                    // Left eye
                                                    drawOval(
                                                        color = Color.Black,
                                                        topLeft = Offset(w * 0.26f, h * 0.42f),
                                                        size = Size(w * 0.18f, h * 0.16f)
                                                    )
                                                    // Right eye
                                                    drawOval(
                                                        color = Color.Black,
                                                        topLeft = Offset(w * 0.56f, h * 0.42f),
                                                        size = Size(w * 0.18f, h * 0.16f)
                                                    )
                                                    
                                                    // White sparkle reflections
                                                    drawCircle(
                                                        color = Color.White,
                                                        radius = radius * 0.045f,
                                                        center = Offset(w * 0.32f, h * 0.47f)
                                                    )
                                                    drawCircle(
                                                        color = Color.White,
                                                        radius = radius * 0.045f,
                                                        center = Offset(w * 0.62f, h * 0.47f)
                                                    )
                                                }
                                                
                                                // 4. Smile & Tongue (Goofy alien)
                                                val mouthCenterY = h * 0.68f
                                                if (bossState == "hit") {
                                                    // Funny wave / confused mouth
                                                    val wavePath = Path().apply {
                                                        moveTo(w * 0.4f, mouthCenterY)
                                                        quadraticBezierTo(w * 0.45f, mouthCenterY - 4.dp.toPx(), w * 0.5f, mouthCenterY)
                                                        quadraticBezierTo(w * 0.55f, mouthCenterY + 4.dp.toPx(), w * 0.6f, mouthCenterY)
                                                    }
                                                    drawPath(
                                                        path = wavePath,
                                                        color = Color.White,
                                                        style = Stroke(width = 2.5.dp.toPx())
                                                    )
                                                } else {
                                                    // Big happy smile!
                                                    val smilePath = Path().apply {
                                                        moveTo(w * 0.36f, mouthCenterY)
                                                        quadraticBezierTo(w * 0.5f, mouthCenterY + 12.dp.toPx(), w * 0.64f, mouthCenterY)
                                                    }
                                                    drawPath(
                                                        path = smilePath,
                                                        color = Color.Black,
                                                        style = Stroke(width = 3.dp.toPx())
                                                    )
                                                    
                                                    // Tongue sticking out!
                                                    val tongueRect = androidx.compose.ui.geometry.Rect(
                                                        left = w * 0.46f,
                                                        top = mouthCenterY + 3.dp.toPx(),
                                                        right = w * 0.54f,
                                                        bottom = mouthCenterY + 11.dp.toPx()
                                                    )
                                                    drawRoundRect(
                                                        color = Color(0xFFFF5E7E), // Cheeky Pink Tongue
                                                        topLeft = Offset(tongueRect.left, tongueRect.top),
                                                        size = Size(tongueRect.width, tongueRect.height),
                                                        cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                                                    )
                                                }
                                            }
                                        }
                                        "bottle" -> {
                                            Canvas(modifier = Modifier.fillMaxSize()) {
                                                val w = size.width
                                                val h = size.height
                                                val glassColor = Color(0xFF4DEEEA)
                                                drawRoundRect(
                                                    color = glassColor,
                                                    topLeft = Offset(w * 0.38f, h * 0.05f),
                                                    size = Size(w * 0.24f, h * 0.35f),
                                                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                                                )
                                                drawRect(
                                                    color = NeonPink,
                                                    topLeft = Offset(w * 0.34f, h * 0.02f),
                                                    size = Size(w * 0.32f, h * 0.06f)
                                                )
                                                drawRoundRect(
                                                    color = glassColor,
                                                    topLeft = Offset(w * 0.2f, h * 0.35f),
                                                    size = Size(w * 0.6f, h * 0.6f),
                                                    cornerRadius = CornerRadius(10.dp.toPx(), 10.dp.toPx())
                                                )
                                                drawRect(
                                                    color = Color.White,
                                                    topLeft = Offset(w * 0.28f, h * 0.48f),
                                                    size = Size(w * 0.44f, h * 0.2f)
                                                )
                                                drawLine(
                                                    color = Color.Red,
                                                    start = Offset(w * 0.32f, h * 0.58f),
                                                    end = Offset(w * 0.68f, h * 0.58f),
                                                    strokeWidth = 2.dp.toPx()
                                                )
                                            }
                                        }
                                        "sniper" -> {
                                            Canvas(modifier = Modifier.fillMaxSize()) {
                                                val radius = size.minDimension / 2f
                                                drawCircle(color = NeonCyan, radius = radius, style = Stroke(1.5.dp.toPx()))
                                                drawCircle(color = NeonCyan, radius = radius * 0.5f, style = Stroke(1.dp.toPx()))
                                                drawCircle(color = Color.Red, radius = radius * 0.15f)
                                                drawLine(color = NeonCyan, start = Offset(0f, size.height / 2f), end = Offset(size.width, size.height / 2f), strokeWidth = 1.dp.toPx())
                                                drawLine(color = NeonCyan, start = Offset(size.width / 2f, 0f), end = Offset(size.width / 2f, size.height), strokeWidth = 1.dp.toPx())
                                            }
                                        }
                                        "fast" -> {
                                            Canvas(modifier = Modifier.fillMaxSize()) {
                                                val radius = size.minDimension / 2f
                                                drawCircle(color = Color(0xFFFFD700), radius = radius, style = Stroke(2.dp.toPx()))
                                                drawCircle(color = Color.Red, radius = radius * 0.7f, style = Stroke(1.5.dp.toPx()))
                                                drawCircle(color = Color.Yellow, radius = radius * 0.35f)
                                                drawLine(
                                                    color = Color(0xFFFFD700).copy(alpha = 0.8f),
                                                    start = Offset(-10.dp.toPx(), size.height / 2f),
                                                    end = Offset(-2.dp.toPx(), size.height / 2f),
                                                    strokeWidth = 2.dp.toPx()
                                                )
                                                drawLine(
                                                    color = Color(0xFFFFD700).copy(alpha = 0.5f),
                                                    start = Offset(-18.dp.toPx(), size.height * 0.4f),
                                                    end = Offset(-6.dp.toPx(), size.height * 0.4f),
                                                    strokeWidth = 1.5.dp.toPx()
                                                )
                                            }
                                        }
                                        else -> {
                                            Canvas(modifier = Modifier.fillMaxSize()) {
                                                val radius = size.minDimension / 2f
                                                drawCircle(color = Color.Red, radius = radius)
                                                drawCircle(color = Color.White, radius = radius * 0.65f)
                                                drawCircle(color = Color.Red, radius = radius * 0.4f)
                                                drawCircle(color = Color.Yellow, radius = radius * 0.15f)
                                            }
                                        }
                                    }
                                    
                                    // Show "DELAY" tag if shot in progress (the ping flight)
                                    if (reflexState == "delaying") {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(NeonPink)
                                                .padding(horizontal = 4.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "SENDING...",
                                                fontSize = 7.sp,
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        "finished" -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = t("fps_game_complete"),
                                        color = ElegantGold,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Button(
                                        onClick = { viewModel.resetReflexGame() },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.testTag("reflex_reset_button")
                                    ) {
                                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Retry Icon")
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(text = t("fps_practice_again"))
                                    }
                                }
                            }
                        }
                    }

                    val fpsIsPaused by viewModel.fpsIsPaused.collectAsState()

                    // Floating Pause button for inline FPS
                    if (reflexState == "spawned" || reflexState == "delaying") {
                        IconButton(
                            onClick = { viewModel.setFpsPaused(true) },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                                .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                                .size(36.dp)
                                .testTag("fps_pause_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Pause,
                                contentDescription = "Tạm Dừng FPS",
                                tint = NeonPink,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Inline FPS Pause Overlay
                    if (fpsIsPaused) {
                        val targetPing by viewModel.targetPing.collectAsState()
                        val targetJitter by viewModel.targetJitter.collectAsState()
                        val targetLoss by viewModel.targetLoss.collectAsState()
                        
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.9f))
                                .pointerInput(Unit) { detectTapGestures { } },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Pause,
                                        contentDescription = "Game Paused",
                                        tint = NeonPink,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Text(
                                        text = t("fps_paused_overlay_title"),
                                        color = NeonPink,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.5.sp
                                    )
                                }
                                
                                Text(
                                    text = t("fps_paused_current_config"),
                                    color = Color.LightGray,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth(0.9f)
                                        .background(Color(0xFF151324), RoundedCornerShape(8.dp))
                                        .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                                        .padding(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(t("fps_paused_latency"), color = Color.Gray, fontSize = 10.sp)
                                        Text("${targetPing} ms", color = NeonCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(t("fps_paused_jitter"), color = Color.Gray, fontSize = 10.sp)
                                        Text("${targetJitter} ms", color = ElegantGold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(t("fps_paused_loss"), color = Color.Gray, fontSize = 10.sp)
                                        Text("${targetLoss} %", color = NeonPink, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                // Button to Reset delay/latency
                                Button(
                                    onClick = { 
                                        viewModel.resetNetworkDelay()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth(0.9f),
                                    contentPadding = PaddingValues(vertical = 4.dp, horizontal = 12.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "Reset Network",
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(t("fps_paused_reset_network_btn"), color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(0.9f),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    // Resume Button
                                    Button(
                                        onClick = { viewModel.setFpsPaused(false) },
                                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f),
                                        contentPadding = PaddingValues(vertical = 4.dp, horizontal = 8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PlayArrow,
                                            contentDescription = "Resume",
                                            tint = Color.Black,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(t("fps_paused_resume_btn"), color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }

                                    // Exit Button
                                    Button(
                                        onClick = { 
                                            viewModel.resetReflexGame()
                                            viewModel.setFpsPaused(false)
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.8f)),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f),
                                        contentPadding = PaddingValues(vertical = 4.dp, horizontal = 8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Stop,
                                            contentDescription = "Exit",
                                            tint = Color.White,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(t("fps_paused_exit_btn"), color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                // Comprehensive Evaluation Diagnostic Report
                AnimatedVisibility(visible = reflexState == "finished" && fpsDiagnosticReport != null) {
                    fpsDiagnosticReport?.let { report ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF141322), RoundedCornerShape(12.dp))
                                .border(1.dp, ElegantGold.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = t("fps_report_title"),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ElegantGold
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(
                                            if (report.accuracy >= 80 && report.networkPingSimulated < 50) Color(0xFF143026) else Color(0xFF421521)
                                        )
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = if (report.accuracy >= 80 && report.networkPingSimulated < 50) t("fps_report_status_smooth") else t("fps_report_status_affected"),
                                        fontSize = 10.sp,
                                        color = if (report.accuracy >= 80 && report.networkPingSimulated < 50) Color.Green else Color.Red,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(CardSpaceBorder)
                            )

                            // Issue Header
                            Column {
                                Text(
                                    text = t("fps_report_primary_diagnosis"),
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                                Text(
                                    text = getLocalizedText(report.mainIssue),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (report.networkPingSimulated > 100 || report.networkLossSimulated > 10) NeonPink else NeonCyan
                                )
                            }

                            // Stats Grid
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Card(
                                    modifier = Modifier.weight(1f),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1D31)),
                                    border = BorderStroke(1.dp, CardSpaceBorder)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(10.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(t("fps_report_accuracy_label"), fontSize = 10.sp, color = Color.Gray)
                                        Text("${report.accuracy}%", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        Text(t("fps_report_target_fraction").replace("%d/%d", "${report.hits}/${report.totalTargets}").replace("%d", report.hits.toString()), fontSize = 9.sp, color = Color.LightGray)
                                    }
                                }
                                Card(
                                    modifier = Modifier.weight(1f),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1D31)),
                                    border = BorderStroke(1.dp, CardSpaceBorder)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(10.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(t("fps_report_physical_reflex_label"), fontSize = 10.sp, color = Color.Gray)
                                        Text("${report.avgPhysicalResponseMs}ms", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                                        Text(t("fps_report_pure_physical"), fontSize = 9.sp, color = Color.LightGray)
                                    }
                                }
                                Card(
                                    modifier = Modifier.weight(1f),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1D31)),
                                    border = BorderStroke(1.dp, CardSpaceBorder)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(10.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(t("fps_report_total_response_label"), fontSize = 10.sp, color = Color.Gray)
                                        Text("${report.avgWithNetworkResponseMs}ms", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NeonPink)
                                        Text(t("fps_report_includes_lag"), fontSize = 9.sp, color = Color.LightGray)
                                    }
                                }
                            }

                            // Loss Stats
                            if (report.lostShotsCount > 0) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFF33161C), RoundedCornerShape(8.dp))
                                        .padding(8.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Warning,
                                            contentDescription = "Warning",
                                            tint = Color.Red,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = t("fps_report_packet_loss_warn").replace("%d", report.lostShotsCount.toString()),
                                            fontSize = 11.sp,
                                            color = Color.Red,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            // Linh Chi evaluation
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(NeonPink.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
                                    .border(1.dp, NeonPink.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                    .padding(12.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(
                                        text = t("fps_report_linh_chi_assessment"),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NeonPink
                                    )
                                    Text(
                                        text = getLocalizedText(report.linhChiEvaluation),
                                        fontSize = 12.sp,
                                        color = ElegantTextPrimary,
                                        lineHeight = 18.sp
                                    )
                                }
                            }

                            // Advice/Tips
                            if (report.detailedTips.isNotEmpty()) {
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(
                                        text = t("fps_report_optimization_solutions"),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    report.detailedTips.forEach { tip ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Text(
                                                text = "•",
                                                color = ElegantGold,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                            )
                                            Column {
                                                Text(
                                                    text = getLocalizedText(tip.first),
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = ElegantGold
                                                )
                                                Text(
                                                    text = getLocalizedText(tip.second),
                                                    fontSize = 11.sp,
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
                } // Closes the else block of if (fpsIsZoomed)
            }
        }
        }

        if (selectedSubTab == 2) {
            // --- MOBA Liên Quân 2D Arena Section ---
            Card(
            modifier = Modifier.fillMaxWidth().testTag("moba_game_section_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardSpaceBackground),
            border = BorderStroke(1.dp, CardSpaceBorder)
        ) {
            val mobaState by viewModel.mobaState.collectAsState()
            val mobaHero by viewModel.mobaHero.collectAsState()
            val mobaSelectedEnemy by viewModel.mobaSelectedEnemy.collectAsState()
            val mobaUnlockedHeroes by viewModel.mobaUnlockedHeroes.collectAsState()
            val mobaWinsCount by viewModel.mobaWinsCount.collectAsState()
            val mobaWinsForBoss by viewModel.mobaWinsForBoss.collectAsState()
            val mobaHeroX by viewModel.mobaHeroX.collectAsState()
            val mobaHeroY by viewModel.mobaHeroY.collectAsState()
            val mobaHeroDestX by viewModel.mobaHeroDestX.collectAsState()
            val mobaHeroDestY by viewModel.mobaHeroDestY.collectAsState()
            val mobaHeroHP by viewModel.mobaHeroHP.collectAsState()
            val mobaHeroMaxHP by viewModel.mobaHeroMaxHP.collectAsState()
            val mobaHeroMP by viewModel.mobaHeroMP.collectAsState()
            val mobaHeroMaxMP by viewModel.mobaHeroMaxMP.collectAsState()

            val mobaEnemyX by viewModel.mobaEnemyX.collectAsState()
            val mobaEnemyY by viewModel.mobaEnemyY.collectAsState()
            val mobaEnemyHP by viewModel.mobaEnemyHP.collectAsState()
            val mobaEnemyMaxHP by viewModel.mobaEnemyMaxHP.collectAsState()
            val mobaEnemyName by viewModel.mobaEnemyName.collectAsState()
            val mobaEnemyIsStunned by viewModel.mobaEnemyIsStunned.collectAsState()

            val mobaCreeps by viewModel.mobaCreeps.collectAsState()
            val mobaProjectiles by viewModel.mobaProjectiles.collectAsState()
            val mobaDamageTexts by viewModel.mobaDamageTexts.collectAsState()

            val mobaSkillCooldowns by viewModel.mobaSkillCooldowns.collectAsState()
            val mobaPassiveStacks by viewModel.mobaPassiveStacks.collectAsState()
            val mobaKills by viewModel.mobaKills.collectAsState()
            val mobaDeaths by viewModel.mobaDeaths.collectAsState()
            val mobaAllyTurretHP by viewModel.mobaAllyTurretHP.collectAsState()
            val mobaEnemyTurretHP by viewModel.mobaEnemyTurretHP.collectAsState()
            val mobaLog by viewModel.mobaLog.collectAsState()
            val mobaIsZoomed by viewModel.mobaIsZoomed.collectAsState()
            val mobaDiagnosticReport by viewModel.mobaDiagnosticReport.collectAsState()

            val mobaMuradCloneX by viewModel.mobaMuradCloneX.collectAsState()
            val mobaMuradCloneY by viewModel.mobaMuradCloneY.collectAsState()
            val mobaMuradCastCount by viewModel.mobaMuradCastCount.collectAsState()
            val mobaYasuoGreenZones by viewModel.mobaYasuoGreenZones.collectAsState()
            val mobaYasuoArcSlashes by viewModel.mobaYasuoArcSlashes.collectAsState()

            if (mobaIsZoomed) {
                Dialog(
                    onDismissRequest = { viewModel.setMobaZoomed(false) },
                    properties = DialogProperties(usePlatformDefaultWidth = false)
                ) {
                    BoxWithConstraints(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.95f))
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        val maxDialogHeight = maxHeight
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = maxDialogHeight * 0.9f)
                                .align(Alignment.Center),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = CardSpaceBackground),
                            border = BorderStroke(1.dp, CardSpaceBorder)
                        ) {
                            MobaGameAreaContent(
                                viewModel = viewModel,
                                mobaState = mobaState,
                                mobaHero = mobaHero,
                                mobaHeroX = mobaHeroX,
                                mobaHeroY = mobaHeroY,
                                mobaHeroDestX = mobaHeroDestX,
                                mobaHeroDestY = mobaHeroDestY,
                                mobaHeroHP = mobaHeroHP,
                                mobaHeroMaxHP = mobaHeroMaxHP,
                                mobaHeroMP = mobaHeroMP,
                                mobaHeroMaxMP = mobaHeroMaxMP,
                                mobaEnemyX = mobaEnemyX,
                                mobaEnemyY = mobaEnemyY,
                                mobaEnemyHP = mobaEnemyHP,
                                mobaEnemyMaxHP = mobaEnemyMaxHP,
                                mobaEnemyName = mobaEnemyName,
                                mobaEnemyIsStunned = mobaEnemyIsStunned,
                                mobaCreeps = mobaCreeps,
                                mobaProjectiles = mobaProjectiles,
                                mobaDamageTexts = mobaDamageTexts,
                                mobaSkillCooldowns = mobaSkillCooldowns,
                                mobaPassiveStacks = mobaPassiveStacks,
                                mobaKills = mobaKills,
                                mobaDeaths = mobaDeaths,
                                mobaAllyTurretHP = mobaAllyTurretHP,
                                mobaEnemyTurretHP = mobaEnemyTurretHP,
                                mobaLog = mobaLog,
                                isZoomed = true,
                                mobaDiagnosticReport = mobaDiagnosticReport
                            )
                        }

                        IconButton(
                            onClick = { viewModel.setMobaZoomed(false) },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(16.dp)
                                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                                .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close Zoomed MOBA Game",
                                tint = Color.White
                            )
                        }
                    }
                }
            } else {
                MobaGameAreaContent(
                    viewModel = viewModel,
                    mobaState = mobaState,
                    mobaHero = mobaHero,
                    mobaHeroX = mobaHeroX,
                    mobaHeroY = mobaHeroY,
                    mobaHeroDestX = mobaHeroDestX,
                    mobaHeroDestY = mobaHeroDestY,
                    mobaHeroHP = mobaHeroHP,
                    mobaHeroMaxHP = mobaHeroMaxHP,
                    mobaHeroMP = mobaHeroMP,
                    mobaHeroMaxMP = mobaHeroMaxMP,
                    mobaEnemyX = mobaEnemyX,
                    mobaEnemyY = mobaEnemyY,
                    mobaEnemyHP = mobaEnemyHP,
                    mobaEnemyMaxHP = mobaEnemyMaxHP,
                    mobaEnemyName = mobaEnemyName,
                    mobaEnemyIsStunned = mobaEnemyIsStunned,
                    mobaCreeps = mobaCreeps,
                    mobaProjectiles = mobaProjectiles,
                    mobaDamageTexts = mobaDamageTexts,
                    mobaSkillCooldowns = mobaSkillCooldowns,
                    mobaPassiveStacks = mobaPassiveStacks,
                    mobaKills = mobaKills,
                    mobaDeaths = mobaDeaths,
                    mobaAllyTurretHP = mobaAllyTurretHP,
                    mobaEnemyTurretHP = mobaEnemyTurretHP,
                    mobaLog = mobaLog,
                    isZoomed = false,
                    mobaDiagnosticReport = mobaDiagnosticReport
                )
            }
        }
        }

        if (selectedSubTab == 3) {
            // --- Sphere Simulation Section ---
            Card(
                modifier = Modifier.fillMaxWidth().testTag("sphere_sim_section_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardSpaceBackground),
                border = BorderStroke(1.dp, CardSpaceBorder)
            ) {
                val sphereList by viewModel.sphereList.collectAsState()
                val sphereFps by viewModel.sphereFps.collectAsState()
                val sphereGameState by viewModel.sphereGameState.collectAsState()
                val sphereCount by viewModel.sphereCount.collectAsState()

                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header / HUD
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                t("sphere_sim_title"),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = ElegantGold
                            )
                            Text(
                                t("sphere_sim_desc"),
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }

                        // FPS Meter / Count Badge
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (sphereFps > 20) Color(0xFF10B981).copy(alpha = 0.2f) else Color(0xFFEF4444).copy(alpha = 0.2f))
                                    .border(1.dp, if (sphereFps > 20) Color(0xFF10B981) else Color(0xFFEF4444), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    "FPS: ${sphereFps.roundToInt()}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (sphereFps > 20) Color(0xFF34D399) else Color(0xFFF87171)
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(NeonCyan.copy(alpha = 0.2f))
                                    .border(1.dp, NeonCyan, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    t("sphere_current_count").format(sphereCount),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeonCyan
                                )
                            }
                        }
                    }

                    // Simulated Physics Canvas Box
                    BoxWithConstraints(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF070512))
                            .border(1.dp, NeonCyan.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    ) {
                        val widthPx = constraints.maxWidth.toFloat()
                        val heightPx = constraints.maxHeight.toFloat()

                        if (sphereGameState == "idle") {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Button(
                                    onClick = { viewModel.startSphereSimulation() },
                                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = t("sphere_start_sim"),
                                        color = Color.Black,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        } else if (sphereGameState == "ended") {
                            Box(
                                modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.8f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = t("sphere_max_latency_reached"),
                                        color = Color.Red,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = t("sphere_system_overloaded"),
                                        color = Color.White,
                                        fontSize = 12.sp
                                    )
                                    Text(
                                        text = t("sphere_total_created").format(sphereCount),
                                        color = ElegantGold,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Button(
                                        onClick = { viewModel.startSphereSimulation() },
                                        colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = t("sphere_try_again"),
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        } else {
                            // Active simulation canvas!
                            Canvas(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .pointerInput(Unit) {
                                        detectTapGestures { offset ->
                                            viewModel.handleSphereTap(offset.x, offset.y, widthPx, heightPx)
                                        }
                                    }
                            ) {
                                sphereList.forEach { sphere ->
                                    val posX = (sphere.x / 100f) * size.width
                                    val posY = (sphere.y / 100f) * size.height
                                    val rad = (sphere.radius / 100f) * size.width
                                    val sphereColor = Color(sphere.color)
                                    val glowRadius = rad * 1.5f
                                    
                                    drawCircle(
                                        brush = Brush.radialGradient(
                                            colors = listOf(
                                                sphereColor.copy(alpha = 0.6f),
                                                sphereColor.copy(alpha = 0.2f),
                                                Color.Transparent
                                            ),
                                            center = Offset(posX, posY),
                                            radius = glowRadius
                                        ),
                                        radius = glowRadius,
                                        center = Offset(posX, posY)
                                    )
                                    drawCircle(
                                        color = sphereColor,
                                        radius = rad,
                                        center = Offset(posX, posY)
                                    )
                                    drawCircle(
                                        color = Color.White.copy(alpha = 0.8f),
                                        radius = rad * 0.4f,
                                        center = Offset(posX - rad * 0.25f, posY - rad * 0.25f)
                                    )
                                }
                            }
                        }
                    }

                    // Helper Controls when running
                    if (sphereGameState == "running") {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = { viewModel.doubleAllSpheres() },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("🔥 Nhân Đôi Toàn Bộ", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }

                            Button(
                                onClick = { viewModel.startSphereSimulation() }, // resets
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Khởi Động Lại 🔄", color = Color.White, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MobaGameAreaContent(
    viewModel: MainViewModel,
    mobaState: String,
    mobaHero: String,
    mobaHeroX: Float,
    mobaHeroY: Float,
    mobaHeroDestX: Float,
    mobaHeroDestY: Float,
    mobaHeroHP: Float,
    mobaHeroMaxHP: Float,
    mobaHeroMP: Float,
    mobaHeroMaxMP: Float,
    mobaEnemyX: Float,
    mobaEnemyY: Float,
    mobaEnemyHP: Float,
    mobaEnemyMaxHP: Float,
    mobaEnemyName: String,
    mobaEnemyIsStunned: Boolean,
    mobaCreeps: List<MobaCreep>,
    mobaProjectiles: List<MobaProjectile>,
    mobaDamageTexts: List<MobaDamageText>,
    mobaSkillCooldowns: List<Float>,
    mobaPassiveStacks: Int,
    mobaKills: Int,
    mobaDeaths: Int,
    mobaAllyTurretHP: Float,
    mobaEnemyTurretHP: Float,
    mobaLog: String,
    isZoomed: Boolean,
    mobaDiagnosticReport: MobaDiagnostic?
) {
    val isSimulating by viewModel.isSimulating.collectAsState()
    val currentPing by viewModel.currentPing.collectAsState()
    val mobaSelectedEnemy by viewModel.mobaSelectedEnemy.collectAsState()
    val mobaUnlockedHeroes by viewModel.mobaUnlockedHeroes.collectAsState()
    val mobaWinsCount by viewModel.mobaWinsCount.collectAsState()
    val mobaWinsForBoss by viewModel.mobaWinsForBoss.collectAsState()
    val mobaComboCount by viewModel.mobaComboCount.collectAsState()
    val mobaComboActive by viewModel.mobaComboActive.collectAsState()
    val mobaComboTimeProgress by viewModel.mobaComboTimeProgress.collectAsState()
    val mobaMuradCloneX by viewModel.mobaMuradCloneX.collectAsState()
    val mobaMuradCloneY by viewModel.mobaMuradCloneY.collectAsState()
    val mobaMuradS2Active by viewModel.mobaMuradS2Active.collectAsState()
    val mobaMuradS2X by viewModel.mobaMuradS2X.collectAsState()
    val mobaMuradS2Y by viewModel.mobaMuradS2Y.collectAsState()
    val mobaWindWallX by viewModel.mobaWindWallX.collectAsState()
    val mobaWindWallY by viewModel.mobaWindWallY.collectAsState()
    val mobaWindWallActive by viewModel.mobaWindWallActive.collectAsState()
    
    val mobaEnemyYasuoTrapWallsActive by viewModel.mobaEnemyYasuoTrapWallsActive.collectAsState()
    val mobaEnemyYasuoTrapCenterX by viewModel.mobaEnemyYasuoTrapCenterX.collectAsState()
    val mobaEnemyYasuoTrapCenterY by viewModel.mobaEnemyYasuoTrapCenterY.collectAsState()
    val mobaAlphaBetaX by viewModel.mobaAlphaBetaX.collectAsState()
    val mobaAlphaBetaY by viewModel.mobaAlphaBetaY.collectAsState()
    val mobaAlphaBetaActive by viewModel.mobaAlphaBetaActive.collectAsState()
    
    val mobaXiaoMaskActive by viewModel.mobaXiaoMaskActive.collectAsState()
    val mobaXiaoMaskDurationLeftMs by viewModel.mobaXiaoMaskDurationLeftMs.collectAsState()
    val mobaXiaoDamageBonus by viewModel.mobaXiaoDamageBonus.collectAsState()
    val mobaYasuoGreenZones by viewModel.mobaYasuoGreenZones.collectAsState()
    val mobaYasuoArcSlashes by viewModel.mobaYasuoArcSlashes.collectAsState()

    val mobaIsZoomed by viewModel.mobaIsZoomed.collectAsState()

    val mobaEnemyIsKnockedUp by viewModel.mobaEnemyIsKnockedUp.collectAsState()
    val mobaEnemyKnockupHeight by viewModel.mobaEnemyKnockupHeight.collectAsState()
    val mobaHeroKnockupHeight by viewModel.mobaHeroKnockupHeight.collectAsState()

    val mobaEnemyShield by viewModel.mobaEnemyShield.collectAsState()
    val mobaEnemyEnchanted by viewModel.mobaEnemyEnchanted.collectAsState()
    val mobaEnemyS3TargetX by viewModel.mobaEnemyS3TargetX.collectAsState()
    val mobaEnemyS3TargetY by viewModel.mobaEnemyS3TargetY.collectAsState()

    val mobaAllyTurretTopHP by viewModel.mobaAllyTurretTopHP.collectAsState()
    val mobaAllyTurretBotHP by viewModel.mobaAllyTurretBotHP.collectAsState()
    val mobaEnemyTurretTopHP by viewModel.mobaEnemyTurretTopHP.collectAsState()
    val mobaEnemyTurretBotHP by viewModel.mobaEnemyTurretBotHP.collectAsState()
    val mobaDashTrails by viewModel.mobaDashTrails.collectAsState()

    val mobaAllyCastleHP by viewModel.mobaAllyCastleHP.collectAsState()
    val mobaEnemyCastleHP by viewModel.mobaEnemyCastleHP.collectAsState()
    val mobaTulenOrbs by viewModel.mobaTulenOrbs.collectAsState()
    val mobaTulenUltLaserTargetId by viewModel.mobaTulenUltLaserTargetId.collectAsState()

    val mobaEnemyMuradS2Active by viewModel.mobaEnemyMuradS2Active.collectAsState()
    val mobaEnemyMuradS2X by viewModel.mobaEnemyMuradS2X.collectAsState()
    val mobaEnemyMuradS2Y by viewModel.mobaEnemyMuradS2Y.collectAsState()
    val mobaEnemyTulenUltLaserActive by viewModel.mobaEnemyTulenUltLaserActive.collectAsState()
    val mobaEnemyClones by viewModel.mobaEnemyClones.collectAsState()

    val comboScale = remember { Animatable(1.0f) }
    val comboFlash = remember { Animatable(0.0f) }

    LaunchedEffect(mobaComboCount) {
        if (mobaComboCount > 0) {
            launch {
                comboScale.snapTo(1.35f)
                comboScale.animateTo(
                    targetValue = 1.0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            }
            launch {
                comboFlash.snapTo(1.0f)
                comboFlash.animateTo(
                    targetValue = 0.0f,
                    animationSpec = tween(350)
                )
            }
        }
    }

    val mobaScrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .padding(16.dp)
            .then(
                if (isZoomed) Modifier.fillMaxHeight() else Modifier
            ),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = t("moba_2d_title"),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan
                    )
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFE11D48).copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                            .border(1.dp, Color(0xFFE11D48), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "BETA LAG TEST",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFB7185)
                        )
                    }
                }
                Text(
                    text = t("moba_2d_desc"),
                    fontSize = 11.sp,
                    color = Color.LightGray
                )
            }

            // Stats top right
            if (mobaState == "playing") {
                val mobaIsPaused by viewModel.mobaIsPaused.collectAsState()
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "⚔️ $mobaKills / $mobaDeaths",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = ElegantGold
                    )
                    IconButton(
                        onClick = { viewModel.setMobaPaused(!mobaIsPaused) },
                        modifier = Modifier.size(36.dp).testTag("moba_pause_button")
                    ) {
                        Icon(
                            imageVector = if (mobaIsPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                            contentDescription = "Tạm dừng",
                            tint = NeonPink,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(
                        onClick = { viewModel.setMobaZoomed(!isZoomed) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (isZoomed) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                            contentDescription = "Zoom Game",
                            tint = NeonCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        when (mobaState) {
            "idle" -> {
                MobaHeroSelection(
                    mobaHero = mobaHero,
                    mobaSelectedEnemy = mobaSelectedEnemy,
                    mobaUnlockedHeroes = mobaUnlockedHeroes,
                    mobaWinsCount = mobaWinsCount,
                    mobaWinsForBoss = mobaWinsForBoss,
                    onSelectHero = { viewModel.selectMobaHero(it) },
                    onSelectEnemy = { viewModel.selectMobaEnemy(it) },
                    onResetBossProgress = { viewModel.resetMobaBossProgress() },
                    onStartGame = { viewModel.startMobaGame() }
                )
            }
            "preparing" -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(color = NeonCyan, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = t("moba_loading_game"),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = t("moba_loading_desc"),
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
            "playing" -> {
                var controllerMode by remember { mutableStateOf("mobile") }
                // Game Board Canvas Area
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF0B0F19))
                        .border(1.dp, CardSpaceBorder)
                        .pointerInput(Unit) {
                            detectTapGestures { offset ->
                                val logicalX = (offset.x / size.width) * 100f
                                val logicalY = (offset.y / size.height) * 100f
                                viewModel.moveHeroTo(logicalX, logicalY)
                            }
                        }
                ) {
                    val boardWidth = maxWidth
                    val boardHeight = maxHeight

                    // 1. Draw river and paths behind everything using standard Canvas
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        // Draw three horizontal lane pathways (Kinh Thống - Top, Giữa - Mid, Rồng - Bot)
                        // Top Lane
                        drawRect(
                            color = Color(0xFF1E293B),
                            topLeft = Offset(0f, size.height * 0.20f),
                            size = Size(size.width, size.height * 0.12f)
                        )
                        // Mid Lane
                        drawRect(
                            color = Color(0xFF1E293B),
                            topLeft = Offset(0f, size.height * 0.44f),
                            size = Size(size.width, size.height * 0.12f)
                        )
                        // Bot Lane
                        drawRect(
                            color = Color(0xFF1E293B),
                            topLeft = Offset(0f, size.height * 0.68f),
                            size = Size(size.width, size.height * 0.12f)
                        )

                        // River (vertical stream down the middle)
                        drawRect(
                            color = Color(0xFF0369A1).copy(alpha = 0.3f),
                            topLeft = Offset(size.width * 0.48f, 0f),
                            size = Size(size.width * 0.04f, size.height)
                        )

                        // Top Allied Turret Ring
                        drawCircle(
                            color = Color(0xFF0284C7).copy(alpha = 0.14f),
                            radius = size.width * 0.09f,
                            center = Offset(size.width * 0.30f, size.height * 0.28f),
                            style = Stroke(width = 1.5f)
                        )
                        // Top Enemy Turret Ring
                        drawCircle(
                            color = Color(0xFFEF4444).copy(alpha = 0.14f),
                            radius = size.width * 0.09f,
                            center = Offset(size.width * 0.75f, size.height * 0.28f),
                            style = Stroke(width = 1.5f)
                        )

                        // Mid Allied Turret Ring
                        drawCircle(
                            color = Color(0xFF0284C7).copy(alpha = 0.14f),
                            radius = size.width * 0.09f,
                            center = Offset(size.width * 0.30f, size.height * 0.50f),
                            style = Stroke(width = 1.5f)
                        )
                        // Mid Enemy Turret Ring
                        drawCircle(
                            color = Color(0xFFEF4444).copy(alpha = 0.14f),
                            radius = size.width * 0.09f,
                            center = Offset(size.width * 0.75f, size.height * 0.50f),
                            style = Stroke(width = 1.5f)
                        )

                        // Bot Allied Turret Ring
                        drawCircle(
                            color = Color(0xFF0284C7).copy(alpha = 0.14f),
                            radius = size.width * 0.09f,
                            center = Offset(size.width * 0.30f, size.height * 0.72f),
                            style = Stroke(width = 1.5f)
                        )
                        // Bot Enemy Turret Ring
                        drawCircle(
                            color = Color(0xFFEF4444).copy(alpha = 0.14f),
                            radius = size.width * 0.09f,
                            center = Offset(size.width * 0.75f, size.height * 0.72f),
                            style = Stroke(width = 1.5f)
                        )

                        // LUYỆN NGỤC Warning Indicator
                        if (mobaEnemyS3TargetX > 0f) {
                            val ringX = size.width * (mobaEnemyS3TargetX / 100f)
                            val ringY = size.height * (mobaEnemyS3TargetY / 100f)
                            val ringRadius = size.width * 0.14f
                            
                            drawCircle(
                                color = Color(0xFFEF4444).copy(alpha = 0.45f),
                                radius = ringRadius,
                                center = Offset(ringX, ringY),
                                style = Stroke(width = 3.5f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 10f), 0f))
                            )
                            drawCircle(
                                color = Color(0xFFEF4444).copy(alpha = 0.15f),
                                radius = ringRadius,
                                center = Offset(ringX, ringY)
                            )
                        }

                        // 1.1 Draw Movement Direction Arrow
                        val dx = mobaHeroDestX - mobaHeroX
                        val dy = mobaHeroDestY - mobaHeroY
                        val dist = kotlin.math.sqrt(dx * dx + dy * dy)
                        if (dist > 1.5f && mobaHeroHP > 0f) {
                            val startX = size.width * (mobaHeroX / 100f)
                            val startY = size.height * (mobaHeroY / 100f)
                            val endX = size.width * (mobaHeroDestX / 100f)
                            val endY = size.height * (mobaHeroDestY / 100f)
                            
                            val arrowColor = if (mobaHero == "Tulen") Color(0xFF06B6D4) else if (mobaHero == "Murad") Color(0xFFF59E0B) else Color(0xFFFFCC33)
                            
                            drawLine(
                                color = arrowColor.copy(alpha = 0.5f),
                                start = Offset(startX, startY),
                                end = Offset(endX, endY),
                                strokeWidth = 3f,
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
                            )
                            
                            val angle = kotlin.math.atan2(endY - startY, endX - startX)
                            val arrowLength = 16f
                            val arrowAngle = Math.PI / 6
                            
                            val x1 = endX - arrowLength * kotlin.math.cos(angle - arrowAngle).toFloat()
                            val y1 = endY - arrowLength * kotlin.math.sin(angle - arrowAngle).toFloat()
                            val x2 = endX - arrowLength * kotlin.math.cos(angle + arrowAngle).toFloat()
                            val y2 = endY - arrowLength * kotlin.math.sin(angle + arrowAngle).toFloat()
                            
                            val arrowPath = Path().apply {
                                moveTo(endX, endY)
                                lineTo(x1, y1)
                                lineTo(x2, y2)
                                close()
                            }
                            drawPath(
                                path = arrowPath,
                                color = arrowColor
                            )
                            
                            drawCircle(
                                color = arrowColor.copy(alpha = 0.4f),
                                radius = 8f,
                                center = Offset(startX, startY),
                                style = Stroke(width = 2f)
                            )
                        }
                    }

                    // 2. Bases
                    // Allied Base Left (X=5)
                    MobaBaseView(
                        hp = mobaAllyCastleHP,
                        maxHp = 3500f,
                        isEnemy = false,
                        modifier = Modifier.align(Alignment.CenterStart).offset(x = 6.dp)
                    )

                    // Enemy Base Right (X=95)
                    MobaBaseView(
                        hp = mobaEnemyCastleHP,
                        maxHp = 3500f,
                        isEnemy = true,
                        modifier = Modifier.align(Alignment.CenterEnd).offset(x = (-6).dp)
                    )

                    // 3. Turrets (Ally X=30, Enemy X=75)
                    // -- ALLIED TURRETS --
                    // Top Allied Turret
                    if (mobaAllyTurretTopHP > 0f) {
                        MobaTurretView(
                            hp = mobaAllyTurretTopHP,
                            maxHp = 1500f,
                            isEnemy = false,
                            modifier = Modifier.offset(
                                x = boardWidth * 0.30f - 16.dp,
                                y = boardHeight * 0.28f - 16.dp
                            )
                        )
                    }
                    // Mid Allied Turret
                    if (mobaAllyTurretHP > 0f) {
                        MobaTurretView(
                            hp = mobaAllyTurretHP,
                            maxHp = 1500f,
                            isEnemy = false,
                            modifier = Modifier.offset(
                                x = boardWidth * 0.30f - 16.dp,
                                y = boardHeight * 0.50f - 16.dp
                            )
                        )
                    }
                    // Bot Allied Turret
                    if (mobaAllyTurretBotHP > 0f) {
                        MobaTurretView(
                            hp = mobaAllyTurretBotHP,
                            maxHp = 1500f,
                            isEnemy = false,
                            modifier = Modifier.offset(
                                x = boardWidth * 0.30f - 16.dp,
                                y = boardHeight * 0.72f - 16.dp
                            )
                        )
                    }

                    // -- ENEMY TURRETS --
                    // Top Enemy Turret
                    if (mobaEnemyTurretTopHP > 0f) {
                        MobaTurretView(
                            hp = mobaEnemyTurretTopHP,
                            maxHp = 1500f,
                            isEnemy = true,
                            modifier = Modifier.offset(
                                x = boardWidth * 0.75f - 16.dp,
                                y = boardHeight * 0.28f - 16.dp
                            )
                        )
                    }
                    // Mid Enemy Turret
                    if (mobaEnemyTurretHP > 0f) {
                        MobaTurretView(
                            hp = mobaEnemyTurretHP,
                            maxHp = 1500f,
                            isEnemy = true,
                            modifier = Modifier.offset(
                                x = boardWidth * 0.75f - 16.dp,
                                y = boardHeight * 0.50f - 16.dp
                            )
                        )
                    }
                    // Bot Enemy Turret
                    if (mobaEnemyTurretBotHP > 0f) {
                        MobaTurretView(
                            hp = mobaEnemyTurretBotHP,
                            maxHp = 1500f,
                            isEnemy = true,
                            modifier = Modifier.offset(
                                x = boardWidth * 0.75f - 16.dp,
                                y = boardHeight * 0.72f - 16.dp
                            )
                        )
                    }

                    // 4. Creeps (Lính)
                    mobaCreeps.forEach { creep ->
                        MobaCreepView(
                            creep = creep,
                            modifier = Modifier.offset(
                                x = boardWidth * (creep.x / 100f) - 10.dp,
                                y = boardHeight * (creep.y / 100f) - 10.dp
                            )
                        )
                    }

                    // 5. Enemy Champion
                    if (mobaEnemyHP > 0f) {
                        val enemyColor = when (mobaSelectedEnemy) {
                            "Tulen" -> Color(0xFF06B6D4)
                            "Tulen hắc pháp sư" -> Color(0xFF8B5CF6)
                            "Valhein" -> Color(0xFFEAB308)
                            "Valhein ma cà rồng" -> Color(0xFFEF4444)
                            "Murad" -> Color(0xFF3B82F6)
                            "Murad hoàng tử suy tàn" -> Color(0xFFD97706)
                            "Yasuo" -> Color(0xFFCBD5E1)
                            "Yasuo cơn gió cuồng ma" -> Color(0xFF475569)
                            "Alpha" -> Color(0xFF22D3EE)
                            "Alpha kẻ kí sinh" -> Color(0xFFEC4899)
                            "Xiao" -> Color(0xFF10B981)
                            "Xiao nghiệp chướng" -> Color(0xFF065F46)
                            "Maloch" -> Color(0xFFA21CAF)
                            else -> Color(0xFFA21CAF)
                        }
                        MobaChampionView(
                            name = if (mobaEnemyIsKnockedUp) "HẤT TUNG 🌪️" else if (mobaEnemyIsStunned) "CHOÁNG 🌀" else mobaSelectedEnemy,
                            isEnemy = true,
                            hp = mobaEnemyHP,
                            maxHp = mobaEnemyMaxHP,
                            modifier = Modifier.offset(
                                x = boardWidth * (mobaEnemyX / 100f) - 18.dp,
                                y = boardHeight * (mobaEnemyY / 100f) - 18.dp - mobaEnemyKnockupHeight.dp
                            ),
                            color = enemyColor,
                            shield = mobaEnemyShield,
                            isEnchanted = mobaEnemyEnchanted
                        )
                    }

                    // 5b. Murad's Shadow Clone (Ảo Ảnh)
                    if (mobaHero == "Murad" && mobaMuradCloneX > 0f) {
                        MobaChampionView(
                            name = "Ảo Ảnh 👥",
                            isEnemy = false,
                            hp = 0f,
                            maxHp = 0f,
                            modifier = Modifier.offset(
                                x = boardWidth * (mobaMuradCloneX / 100f) - 18.dp,
                                y = boardHeight * (mobaMuradCloneY / 100f) - 18.dp
                            ),
                            color = Color(0xFFF59E0B).copy(alpha = 0.55f), // semi-transparent golden
                            hasGlow = false
                        )
                    }

                    // 5b2. Enemy Murad's Shadow Clones (Phân Thân Địch)
                    mobaEnemyClones.forEach { clone ->
                        MobaChampionView(
                            name = "Phân thân 👥",
                            isEnemy = true,
                            hp = 0f,
                            maxHp = 0f,
                            modifier = Modifier.offset(
                                x = boardWidth * (clone.x / 100f) - 18.dp,
                                y = boardHeight * (clone.y / 100f) - 18.dp
                            ),
                            color = Color(0xFF8B5CF6).copy(alpha = 0.6f), // semi-transparent purple shadow
                            hasGlow = true
                        )
                    }

                    // 5c. Yasuo's Wind Wall (Tường Gió)
                    if (mobaHero == "Yasuo" && mobaWindWallActive) {
                        val infiniteTransition = rememberInfiniteTransition(label = "windWall")
                        val windOffset by infiniteTransition.animateFloat(
                            initialValue = -15f,
                            targetValue = 15f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(1000, easing = LinearEasing),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "windOffset"
                        )
                        val windAlpha by infiniteTransition.animateFloat(
                            initialValue = 0.5f,
                            targetValue = 0.95f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(800, easing = FastOutSlowInEasing),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "windAlpha"
                        )
                        val windScale by infiniteTransition.animateFloat(
                            initialValue = 0.95f,
                            targetValue = 1.05f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(1000, easing = FastOutSlowInEasing),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "windScale"
                        )
                        Box(
                            modifier = Modifier
                                .offset(
                                    x = boardWidth * (mobaWindWallX / 100f) - 6.dp,
                                    y = boardHeight * (mobaWindWallY / 100f) - 30.dp
                                )
                                .size(12.dp * windScale, 60.dp * windScale)
                                .alpha(windAlpha)
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color(0xFF38BDF8).copy(alpha = 0.1f),
                                            Color(0xFFE0F2FE).copy(alpha = 0.8f),
                                            Color(0xFF0284C7).copy(alpha = 0.9f),
                                            Color(0xFFE0F2FE).copy(alpha = 0.8f),
                                            Color(0xFF38BDF8).copy(alpha = 0.1f)
                                        )
                                    ),
                                    shape = RoundedCornerShape(6.dp)
                                )
                                .border(1.5.dp, Color(0xFFE0F2FE).copy(alpha = 0.7f), RoundedCornerShape(6.dp))
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                drawLine(
                                    color = Color.White.copy(alpha = 0.6f),
                                    start = Offset(size.width / 2f, 15f + windOffset),
                                    end = Offset(size.width / 2f, 30f + windOffset),
                                    strokeWidth = 2.5f,
                                    cap = StrokeCap.Round
                                )
                                drawLine(
                                    color = Color(0xFF7DD3FC).copy(alpha = 0.8f),
                                    start = Offset(size.width / 2f, 40f - windOffset),
                                    end = Offset(size.width / 2f, 55f - windOffset),
                                    strokeWidth = 3f,
                                    cap = StrokeCap.Round
                                )
                            }
                            Text(
                                "💨",
                                modifier = Modifier.align(Alignment.Center),
                                fontSize = 12.sp
                            )
                        }
                    }

                    // 5cd. Yasuo Enemy's 4 Trap Walls (Thiên Phong Địa Trận)
                    if (mobaEnemyYasuoTrapWallsActive) {
                        val cx = mobaEnemyYasuoTrapCenterX
                        val cy = mobaEnemyYasuoTrapCenterY
                        val halfOffset = 8.5f // match ViewModel clamping halfSize
                        
                        // Let's draw 4 walls enclosing the center
                        val wallPositions = listOf(
                            // Triple of (x, y, isHorizontal)
                            Triple(cx, cy - halfOffset, true),   // Top
                            Triple(cx, cy + halfOffset, true),   // Bottom
                            Triple(cx - halfOffset, cy, false),  // Left
                            Triple(cx + halfOffset, cy, false)   // Right
                        )
                        
                        wallPositions.forEach { (wX, wY, isHoriz) ->
                            val infiniteTransition = rememberInfiniteTransition(label = "enemyWindWall")
                            val windAlpha by infiniteTransition.animateFloat(
                                initialValue = 0.6f,
                                targetValue = 0.95f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(600, easing = LinearEasing),
                                    repeatMode = RepeatMode.Reverse
                                ),
                                label = "enemyWindAlpha"
                            )
                            Box(
                                modifier = Modifier
                                    .offset(
                                        x = boardWidth * (wX / 100f) - (if (isHoriz) 30.dp else 6.dp),
                                        y = boardHeight * (wY / 100f) - (if (isHoriz) 6.dp else 30.dp)
                                    )
                                    .size(
                                        width = if (isHoriz) 60.dp else 12.dp,
                                        height = if (isHoriz) 12.dp else 60.dp
                                    )
                                    .alpha(windAlpha)
                                    .background(
                                        Brush.linearGradient(
                                            colors = listOf(
                                                Color(0xFF7C3AED), // Demon purple
                                                Color(0xFFEC4899), // Pinkish energy
                                                Color(0xFF1E1B4B)  // Dark deep space
                                            )
                                        ),
                                        shape = RoundedCornerShape(6.dp)
                                    )
                                    .border(2.dp, Color(0xFFF472B6).copy(alpha = 0.8f), RoundedCornerShape(6.dp))
                            ) {
                                Text(
                                    text = if (isHoriz) "➖" else "|",
                                    modifier = Modifier.align(Alignment.Center),
                                    color = Color.White,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }

                    // 5cc. Yasuo's Green Zones (Hasagi Zones)
                    mobaYasuoGreenZones.forEach { zone ->
                        val zoneDiameter = boardWidth * (zone.radius * 2f / 100f)
                        val zX = boardWidth * (zone.x / 100f)
                        val zY = boardHeight * (zone.y / 100f)

                        Box(
                            modifier = Modifier
                                .offset(
                                    x = zX - (zoneDiameter / 2),
                                    y = zY - (zoneDiameter / 2)
                                )
                                .size(zoneDiameter)
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            Color(0xFF10B981).copy(alpha = 0.45f), // Emerald green
                                            Color(0xFF059669).copy(alpha = 0.25f),
                                            Color.Transparent
                                        )
                                    ),
                                    shape = CircleShape
                                )
                                .border(1.5.dp, Color(0xFF34D399).copy(alpha = 0.6f), CircleShape)
                        ) {
                            Text(
                                "🌪️ HASAGI",
                                modifier = Modifier.align(Alignment.Center),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    // 5cd. Yasuo's Arc Slashes (Crescent wind arc sweep)
                    mobaYasuoArcSlashes.forEach { arc ->
                        val arcDiameter = boardWidth * (arc.radius * 2f / 100f)
                        val aX = boardWidth * (arc.x / 100f)
                        val aY = boardHeight * (arc.y / 100f)

                        Box(
                            modifier = Modifier
                                .offset(
                                    x = aX - (arcDiameter / 2),
                                    y = aY - (arcDiameter / 2)
                                )
                                .size(arcDiameter),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val w = size.width
                                val h = size.height
                                
                                val path = Path().apply {
                                    moveTo(w * 0.1f, h * 0.5f)
                                    quadraticBezierTo(w * 0.5f, h * 1.1f, w * 0.9f, h * 0.5f)
                                    quadraticBezierTo(w * 0.5f, h * 0.75f, w * 0.1f, h * 0.5f)
                                    close()
                                }
                                
                                drawPath(
                                    path = path,
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            Color(0xFF00F5D4).copy(alpha = 0.9f),
                                            Color(0xFF10B981).copy(alpha = 0.4f),
                                            Color.Transparent
                                        ),
                                        center = Offset(w / 2f, h * 0.75f),
                                        radius = w / 2f
                                    )
                                )
                                
                                drawPath(
                                    path = path,
                                    color = Color.White,
                                    style = Stroke(
                                        width = 3.dp.toPx(),
                                        cap = StrokeCap.Round
                                    )
                                )
                            }
                            
                            Text(
                                "CRITICAL SLASH! ⚔️",
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .offset(y = 12.dp),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF00F5D4)
                            )
                        }
                    }

                    // 5d. Murad's S2: Vô Ảnh Trận (Orange Sandstorm Circle)
                    if (mobaMuradS2Active && mobaMuradS2X > 0f) {
                        val infiniteTransition = rememberInfiniteTransition(label = "muradS2")
                        val rotateAngle by infiniteTransition.animateFloat(
                            initialValue = 0f,
                            targetValue = 360f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(3000, easing = LinearEasing),
                                repeatMode = RepeatMode.Restart
                            ),
                            label = "rotateAngle"
                        )
                        val pulseScale by infiniteTransition.animateFloat(
                            initialValue = 0.96f,
                            targetValue = 1.04f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(1200, easing = FastOutSlowInEasing),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "pulseScale"
                        )
                        val sandAlpha by infiniteTransition.animateFloat(
                            initialValue = 0.15f,
                            targetValue = 0.35f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(1000, easing = FastOutSlowInEasing),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "sandAlpha"
                        )

                        // Vô Ảnh Trận boundary size is roughly 10% radius -> 20% diameter
                        val s2CenterX = boardWidth * (mobaMuradS2X / 100f)
                        val s2CenterY = boardHeight * (mobaMuradS2Y / 100f)
                        val s2Diameter = boardWidth * 0.20f * pulseScale

                        Box(
                            modifier = Modifier
                                .offset(
                                    x = s2CenterX - (s2Diameter / 2),
                                    y = s2CenterY - (s2Diameter / 2)
                                )
                                .size(s2Diameter)
                        ) {
                            // 1. Spinning Sandstorm/Runic Canvas
                            Canvas(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .graphicsLayer(rotationZ = rotateAngle)
                            ) {
                                val radius = size.width / 2f
                                
                                // Outer glowing circle
                                drawCircle(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            Color(0xFFFF9800).copy(alpha = 0.8f),
                                            Color(0xFFFF5722).copy(alpha = 0.4f),
                                            Color.Transparent
                                        )
                                    ),
                                    radius = radius,
                                    center = center
                                )

                                // Sandstorm ring boundary
                                drawCircle(
                                    color = Color(0xFFF59E0B),
                                    radius = radius - 4f,
                                    center = center,
                                    style = Stroke(
                                        width = 4.5f,
                                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 15f), 0f)
                                    )
                                )

                                // Secondary thin orange ring
                                drawCircle(
                                    color = Color(0xFFFF5722).copy(alpha = 0.6f),
                                    radius = radius - 15f,
                                    center = center,
                                    style = Stroke(
                                        width = 1.5f,
                                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(30f, 20f), 0f)
                                    )
                                )
                            }

                            // 2. Translucent golden center sand area
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(8.dp)
                                    .background(
                                        Brush.radialGradient(
                                            colors = listOf(
                                                Color(0xFFFEF3C7).copy(alpha = sandAlpha),
                                                Color(0xFFFBBF24).copy(alpha = sandAlpha * 0.5f),
                                                Color.Transparent
                                            )
                                        ),
                                        shape = CircleShape
                                    )
                                    .border(1.dp, Color(0xFFFFB74D).copy(alpha = 0.3f), CircleShape)
                            )

                            // 3. Falling gold particles/glyphs at cardinal directions on boundary
                            Text("🏜️", modifier = Modifier.align(Alignment.Center), fontSize = 14.sp)
                            Text("✨", modifier = Modifier.align(Alignment.TopCenter).offset(y = (-4).dp), fontSize = 10.sp)
                            Text("✨", modifier = Modifier.align(Alignment.BottomCenter).offset(y = 4.dp), fontSize = 10.sp)
                            Text("✨", modifier = Modifier.align(Alignment.CenterStart).offset(x = (-4).dp), fontSize = 10.sp)
                            Text("✨", modifier = Modifier.align(Alignment.CenterEnd).offset(x = 4.dp), fontSize = 10.sp)
                        }
                    }

                    // Enemy Murad's S2: Vô Ảnh Trận (Red/Orange Dark Sandstorm Circle)
                    if (mobaEnemyMuradS2Active && mobaEnemyMuradS2X > 0f) {
                        val infiniteTransition = rememberInfiniteTransition(label = "enemyMuradS2")
                        val rotateAngle by infiniteTransition.animateFloat(
                            initialValue = 0f,
                            targetValue = 360f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(3000, easing = LinearEasing),
                                repeatMode = RepeatMode.Restart
                            ),
                            label = "rotateAngle"
                        )
                        val pulseScale by infiniteTransition.animateFloat(
                            initialValue = 0.96f,
                            targetValue = 1.04f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(1200, easing = FastOutSlowInEasing),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "pulseScale"
                        )
                        val sandAlpha by infiniteTransition.animateFloat(
                            initialValue = 0.15f,
                            targetValue = 0.35f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(1000, easing = FastOutSlowInEasing),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "sandAlpha"
                        )

                        val s2CenterX = boardWidth * (mobaEnemyMuradS2X / 100f)
                        val s2CenterY = boardHeight * (mobaEnemyMuradS2Y / 100f)
                        val s2Diameter = boardWidth * 0.20f * pulseScale

                        Box(
                            modifier = Modifier
                                .offset(
                                    x = s2CenterX - (s2Diameter / 2),
                                    y = s2CenterY - (s2Diameter / 2)
                                )
                                .size(s2Diameter)
                        ) {
                            Canvas(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .graphicsLayer(rotationZ = rotateAngle)
                            ) {
                                val radius = size.width / 2f
                                
                                drawCircle(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            Color(0xFFEF4444).copy(alpha = 0.8f),
                                            Color(0xFFD97706).copy(alpha = 0.4f),
                                            Color.Transparent
                                        )
                                    ),
                                    radius = radius,
                                    center = center
                                )

                                drawCircle(
                                    color = Color(0xFFEF4444),
                                    radius = radius - 4f,
                                    center = center,
                                    style = Stroke(
                                        width = 4.5f,
                                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 15f), 0f)
                                    )
                                )

                                drawCircle(
                                    color = Color(0xFFF59E0B).copy(alpha = 0.6f),
                                    radius = radius - 15f,
                                    center = center,
                                    style = Stroke(
                                        width = 1.5f,
                                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(30f, 20f), 0f)
                                    )
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(8.dp)
                                    .background(
                                        Brush.radialGradient(
                                            colors = listOf(
                                                Color(0xFFFEE2E2).copy(alpha = sandAlpha),
                                                Color(0xFFFCA5A5).copy(alpha = sandAlpha * 0.5f),
                                                Color.Transparent
                                            )
                                        ),
                                        shape = CircleShape
                                    )
                                    .border(1.dp, Color(0xFFEF4444).copy(alpha = 0.3f), CircleShape)
                            )

                            Text("😈", modifier = Modifier.align(Alignment.Center), fontSize = 14.sp)
                            Text("🩸", modifier = Modifier.align(Alignment.TopCenter).offset(y = (-4).dp), fontSize = 10.sp)
                            Text("🩸", modifier = Modifier.align(Alignment.BottomCenter).offset(y = 4.dp), fontSize = 10.sp)
                            Text("🩸", modifier = Modifier.align(Alignment.CenterStart).offset(x = (-4).dp), fontSize = 10.sp)
                            Text("🩸", modifier = Modifier.align(Alignment.CenterEnd).offset(x = 4.dp), fontSize = 10.sp)
                        }
                    }

                    // 5e. Alpha's Beta Drone (Robot Companion)
                    if (mobaAlphaBetaActive && mobaAlphaBetaX > 0f) {
                        val infiniteTransition = rememberInfiniteTransition(label = "betaDrone")
                        val betaPulse by infiniteTransition.animateFloat(
                            initialValue = 0.9f,
                            targetValue = 1.1f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(600, easing = FastOutSlowInEasing),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "betaPulse"
                        )
                        val betaAlpha by infiniteTransition.animateFloat(
                            initialValue = 0.8f,
                            targetValue = 1.0f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(800, easing = LinearEasing),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "betaAlpha"
                        )

                        val betaCenterX = boardWidth * (mobaAlphaBetaX / 100f)
                        val betaCenterY = boardHeight * (mobaAlphaBetaY / 100f)

                        Box(
                            modifier = Modifier
                                .offset(
                                    x = betaCenterX - 10.dp,
                                    y = betaCenterY - 10.dp
                                )
                                .size(20.dp * betaPulse)
                                .alpha(betaAlpha),
                            contentAlignment = Alignment.Center
                        ) {
                            // High-tech circular energy ring under Beta
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                drawCircle(
                                    color = Color(0xFF00FFFF).copy(alpha = 0.3f),
                                    radius = size.width / 2f,
                                    style = Stroke(width = 1.5f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f))
                                )
                                drawCircle(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            Color(0xFF22D3EE).copy(alpha = 0.5f),
                                            Color.Transparent
                                        )
                                    ),
                                    radius = size.width / 3f
                                )
                            }
                            Text(
                                text = "🛸", // futuristic hovering drone emoji
                                fontSize = 12.sp
                            )
                        }
                    }

                    // 5b. Moba Dash Trails (such as Tulen's lightning strikes!)
                    mobaDashTrails.forEach { trail ->
                        if (trail.isTulen) {
                            // "hiệu ứng sấm sét" - columns of lightning striking down
                            Box(modifier = Modifier.fillMaxSize()) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    val tX = size.width * (trail.x / 100f)
                                    val tY = size.height * (trail.y / 100f)
                                    
                                    val random = java.util.Random(trail.id.hashCode().toLong())
                                    val segments = 4
                                    val segHeight = tY / segments
                                    
                                    val lightningPath = Path().apply {
                                        moveTo(tX + (random.nextFloat() * 12f - 6f), 0f)
                                        for (step in 1 until segments) {
                                            val currY = step * segHeight
                                            val currX = tX + (random.nextFloat() * 24f - 12f)
                                            lineTo(currX, currY)
                                        }
                                        lineTo(tX, tY)
                                    }
                                    
                                    // Outer electric cyan glow
                                    drawPath(
                                        path = lightningPath,
                                        color = Color(0xFF00FFFF).copy(alpha = 0.4f * trail.alpha),
                                        style = Stroke(width = 6f, cap = StrokeCap.Round)
                                    )
                                    // Inner pure white-hot core
                                    drawPath(
                                        path = lightningPath,
                                        color = Color.White.copy(alpha = trail.alpha),
                                        style = Stroke(width = 2f, cap = StrokeCap.Round)
                                    )
                                    
                                    // Flash light burst at the strike impact point!
                                    drawCircle(
                                        color = Color(0xFF22D3EE).copy(alpha = 0.5f * trail.alpha),
                                        radius = 20f,
                                        center = Offset(tX, tY)
                                    )
                                    drawCircle(
                                        color = Color.White.copy(alpha = 0.8f * trail.alpha),
                                        radius = 8f,
                                        center = Offset(tX, tY)
                                    )
                                }
                                Text(
                                    text = "⚡",
                                    fontSize = 13.sp,
                                    modifier = Modifier.offset(
                                        x = boardWidth * (trail.x / 100f) - 8.dp,
                                        y = boardHeight * (trail.y / 100f) - 16.dp
                                    )
                                )
                            }
                        } else {
                            // Draw standard ghost trails for Murad or other heroes
                            val trailX = boardWidth * (trail.x / 100f) - 10.dp
                            val trailY = boardHeight * (trail.y / 100f) - 10.dp
                            Box(
                                modifier = Modifier
                                    .offset(x = trailX, y = trailY)
                                    .size(20.dp)
                                    .background(Color(trail.color).copy(alpha = 0.35f * trail.alpha), CircleShape)
                                    .border(1.dp, Color(trail.color).copy(alpha = 0.5f * trail.alpha), CircleShape)
                            )
                        }
                    }

                    // 6. Player Hero (Tulen / Valhein / Murad / Yasuo / Xiao)
                    if (mobaHeroHP > 0f) {
                        val glow = (mobaHero == "Tulen" && mobaPassiveStacks >= 5) || 
                                   (mobaHero == "Murad" && mobaPassiveStacks >= 4) || 
                                   (mobaHero == "Yasuo" && mobaHeroKnockupHeight > 0f) ||
                                   (mobaHero == "Alpha" && mobaPassiveStacks >= 2) ||
                                   (mobaHero == "Xiao" && mobaXiaoMaskActive)
                        MobaChampionView(
                            name = mobaHero,
                            isEnemy = false,
                            hp = mobaHeroHP,
                            maxHp = mobaHeroMaxHP,
                            mp = mobaHeroMP,
                            maxMp = mobaHeroMaxMP,
                            modifier = Modifier.offset(
                                x = boardWidth * (mobaHeroX / 100f) - 18.dp,
                                y = boardHeight * (mobaHeroY / 100f) - 18.dp - mobaHeroKnockupHeight.dp
                            ),
                            color = if (mobaHero == "Tulen") Color(0xFF06B6D4) else if (mobaHero == "Murad") Color(0xFFF59E0B) else if (mobaHero == "Yasuo") Color(0xFF64748B) else if (mobaHero == "Alpha") Color(0xFF22D3EE) else if (mobaHero == "Xiao") Color(0xFF10B981) else Color(0xFFFFCC33),
                            hasGlow = glow,
                            isXiaoMaskActive = mobaXiaoMaskActive
                        )

                        // Tulen Orbiting Electric Orbs Visual
                        if (mobaHero == "Tulen" && mobaTulenOrbs.isNotEmpty()) {
                            val hX = boardWidth * (mobaHeroX / 100f)
                            val hY = boardHeight * (mobaHeroY / 100f)
                            mobaTulenOrbs.forEach { orb ->
                                val oX = hX + (kotlin.math.cos(orb.angle) * orb.radius).dp
                                val oY = hY + (kotlin.math.sin(orb.angle) * orb.radius).dp
                                Box(
                                    modifier = Modifier
                                        .offset(x = oX - 5.dp, y = oY - 5.dp)
                                        .size(10.dp)
                                        .background(Color(0xFF00FFFF), CircleShape)
                                        .border(1.5.dp, Color.White, CircleShape)
                                ) {
                                    Canvas(modifier = Modifier.fillMaxSize()) {
                                        drawCircle(
                                            color = Color.White.copy(alpha = 0.7f),
                                            radius = size.width * 0.4f
                                        )
                                    }
                                }
                            }
                        }

                        // Tulen Ult Laser Targeting Beam
                        if (mobaHero == "Tulen" && mobaTulenUltLaserTargetId != null) {
                            val startX = boardWidth * (mobaHeroX / 100f)
                            val startY = boardHeight * (mobaHeroY / 100f)
                            var endX = startX
                            var endY = startY
                            if (mobaTulenUltLaserTargetId == "enemy_hero" && mobaEnemyHP > 0f) {
                                endX = boardWidth * (mobaEnemyX / 100f)
                                endY = boardHeight * (mobaEnemyY / 100f)
                            } else {
                                val tc = mobaCreeps.find { it.id == mobaTulenUltLaserTargetId }
                                if (tc != null) {
                                    endX = boardWidth * (tc.x / 100f)
                                    endY = boardHeight * (tc.y / 100f)
                                }
                            }
                            if (endX != startX || endY != startY) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    drawLine(
                                        color = Color(0xFF00FFFF).copy(alpha = 0.6f),
                                        start = Offset(startX.toPx(), startY.toPx()),
                                        end = Offset(endX.toPx(), endY.toPx()),
                                        strokeWidth = 6f
                                    )
                                    drawLine(
                                        color = Color.White,
                                        start = Offset(startX.toPx(), startY.toPx()),
                                        end = Offset(endX.toPx(), endY.toPx()),
                                        strokeWidth = 2f
                                    )
                                }
                            }
                        }

                        // Enemy Tulen Ult Laser Targeting Beam (Purple for Hắc pháp sư, Gold for normal Tulen)
                        if (mobaEnemyTulenUltLaserActive && mobaEnemyHP > 0f) {
                            val startX = boardWidth * (mobaEnemyX / 100f)
                            val startY = boardHeight * (mobaEnemyY / 100f)
                            val endX = boardWidth * (mobaHeroX / 100f)
                            val endY = boardHeight * (mobaHeroY / 100f)
                            val laserColor = if (mobaSelectedEnemy == "Tulen hắc pháp sư") Color(0xFF8B5CF6) else Color(0xFFEAB308)
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                drawLine(
                                    color = laserColor.copy(alpha = 0.7f),
                                    start = Offset(startX.toPx(), startY.toPx()),
                                    end = Offset(endX.toPx(), endY.toPx()),
                                    strokeWidth = 7f
                                )
                                drawLine(
                                    color = Color.White,
                                    start = Offset(startX.toPx(), startY.toPx()),
                                    end = Offset(endX.toPx(), endY.toPx()),
                                    strokeWidth = 2.5f
                                )
                            }
                        }
                    }

                    // 7. Projectiles (Đạn bay)
                    mobaProjectiles.forEach { proj ->
                        if (proj.type == "yasuo_slash_visual" || proj.type == "yasuo_basic") {
                            // "vết chém katana đầy hoa mĩ" for Yasuo
                            val sXDp = boardWidth * (proj.x / 100f)
                            val sYDp = boardHeight * (proj.y / 100f)
                            val eXDp = boardWidth * (proj.targetX / 100f)
                            val eYDp = boardHeight * (proj.targetY / 100f)
                            
                            Box(modifier = Modifier.fillMaxSize()) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    val sX = size.width * (proj.x / 100f)
                                    val sY = size.height * (proj.y / 100f)
                                    val eX = size.width * (proj.targetX / 100f)
                                    val eY = size.height * (proj.targetY / 100f)
                                    
                                    val slashPath = Path().apply {
                                        moveTo(sX, sY)
                                        quadraticTo(
                                            (sX + eX) / 2f - 25f,
                                            (sY + eY) / 2f - 25f,
                                            eX,
                                            eY
                                        )
                                    }
                                    // Soft pink outer shadow
                                    drawPath(
                                        path = slashPath,
                                        color = Color(0xFFF472B6).copy(alpha = 0.5f),
                                        style = Stroke(width = 44f, cap = StrokeCap.Round)
                                    )
                                    // Bright cyan-white core
                                    drawPath(
                                        path = slashPath,
                                        color = Color(0xFFE0F2FE),
                                        style = Stroke(width = 18f, cap = StrokeCap.Round)
                                    )
                                }
                                // Sakura petals falling off the slash
                                Text(
                                    text = "🌸",
                                    fontSize = 13.sp,
                                    modifier = Modifier.offset(
                                        x = (sXDp + eXDp) / 2f - 18.dp,
                                        y = (sYDp + eYDp) / 2f - 24.dp
                                    )
                                )
                                Text(
                                    text = "✨",
                                    fontSize = 10.sp,
                                    modifier = Modifier.offset(
                                        x = eXDp - 8.dp,
                                        y = eYDp - 8.dp
                                    )
                                )
                            }
                        } else if (proj.type == "murad_slash_visual" || proj.type == "murad_desert_slash") {
                            // "vết chém dao găm sa mạc" for Murad with stunning golden dash shadow trails
                            val sXDp = boardWidth * (proj.x / 100f)
                            val sYDp = boardHeight * (proj.y / 100f)
                            val eXDp = boardWidth * (proj.targetX / 100f)
                            val eYDp = boardHeight * (proj.targetY / 100f)
                            
                            val dx = proj.targetX - proj.x
                            val dy = proj.targetY - proj.y
                            val dist = kotlin.math.sqrt(dx * dx + dy * dy)
                            val ux = if (dist > 0.1f) dx / dist else 1f
                            val uy = if (dist > 0.1f) dy / dist else 0f

                            Box(modifier = Modifier.fillMaxSize()) {
                                // 1. Draw the beautiful neon slash wind arc
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    val sX = size.width * (proj.x / 100f)
                                    val sY = size.height * (proj.y / 100f)
                                    val eX = size.width * (proj.targetX / 100f)
                                    val eY = size.height * (proj.targetY / 100f)
                                    
                                    val slashPath = Path().apply {
                                        moveTo(sX, sY)
                                        quadraticTo(
                                            (sX + eX) / 2f + 20f,
                                            (sY + eY) / 2f - 20f,
                                            eX,
                                            eY
                                        )
                                    }
                                    // Sandy-orange outer sandstorm wind
                                    drawPath(
                                        path = slashPath,
                                        color = Color(0xFFD97706).copy(alpha = 0.6f),
                                        style = Stroke(width = 46f, cap = StrokeCap.Round)
                                    )
                                    // Golden core dagger line
                                    drawPath(
                                        path = slashPath,
                                        color = Color(0xFFFBBF24),
                                        style = Stroke(width = 20f, cap = StrokeCap.Round)
                                    )
                                }

                                // 2. Trailing Shadow Clones (Bóng lướt ảo ảnh) behind the current position
                                // Far tail trail (smaller, faint)
                                val x3Percent = (proj.x - ux * 6.5f).coerceIn(0f, 100f)
                                val y3Percent = (proj.y - uy * 6.5f).coerceIn(0f, 100f)
                                val t3X = boardWidth * (x3Percent / 100f) - 7.dp
                                val t3Y = boardHeight * (y3Percent / 100f) - 7.dp
                                Box(
                                    modifier = Modifier
                                        .offset(x = t3X, y = t3Y)
                                        .size(14.dp)
                                        .background(Color(0xFFFBBF24).copy(alpha = 0.15f), CircleShape)
                                        .border(0.8.dp, Color(0xFFFF9800).copy(alpha = 0.25f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("🗡️", fontSize = 6.sp, modifier = Modifier.alpha(0.2f))
                                }

                                // Medium trail
                                val x2Percent = (proj.x - ux * 4.0f).coerceIn(0f, 100f)
                                val y2Percent = (proj.y - uy * 4.0f).coerceIn(0f, 100f)
                                val t2X = boardWidth * (x2Percent / 100f) - 9.dp
                                val t2Y = boardHeight * (y2Percent / 100f) - 9.dp
                                Box(
                                    modifier = Modifier
                                        .offset(x = t2X, y = t2Y)
                                        .size(18.dp)
                                        .background(Color(0xFFFBBF24).copy(alpha = 0.35f), CircleShape)
                                        .border(1.dp, Color(0xFFFF9800).copy(alpha = 0.45f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("🗡️", fontSize = 8.sp, modifier = Modifier.alpha(0.4f))
                                }

                                // Close tail trail
                                val x1Percent = (proj.x - ux * 1.8f).coerceIn(0f, 100f)
                                val y1Percent = (proj.y - uy * 1.8f).coerceIn(0f, 100f)
                                val t1X = boardWidth * (x1Percent / 100f) - 11.dp
                                val t1Y = boardHeight * (y1Percent / 100f) - 11.dp
                                Box(
                                    modifier = Modifier
                                        .offset(x = t1X, y = t1Y)
                                        .size(22.dp)
                                        .background(Color(0xFFF59E0B).copy(alpha = 0.55f), CircleShape)
                                        .border(1.2.dp, Color(0xFFFEF08A).copy(alpha = 0.7f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("🗡️", fontSize = 10.sp, modifier = Modifier.alpha(0.6f))
                                }

                                // Head Shadow Clone (Vị trí hiện tại của Murad đang chém lướt)
                                val headX = boardWidth * (proj.x / 100f) - 13.dp
                                val headY = boardHeight * (proj.y / 100f) - 13.dp
                                Box(
                                    modifier = Modifier
                                        .offset(x = headX, y = headY)
                                        .size(26.dp)
                                        .background(Color(0xFFFF9800).copy(alpha = 0.85f), CircleShape)
                                        .border(1.8.dp, Color.White, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("🗡️", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }

                                // Desert sand wind sparkles
                                Text(
                                    text = "🏜️",
                                    fontSize = 12.sp,
                                    modifier = Modifier.offset(
                                        x = (sXDp + eXDp) / 2f - 10.dp,
                                        y = (sYDp + eYDp) / 2f - 16.dp
                                    )
                                )
                                Text(
                                    text = "✨",
                                    fontSize = 9.sp,
                                    modifier = Modifier.offset(
                                        x = sXDp - 5.dp,
                                        y = sYDp - 5.dp
                                    )
                                )
                            }
                        } else if (proj.type == "maloch_cleave" || proj.type == "basic_cleave") {
                            // "vết chém đao địa ngục" for enemy Maloch
                            val sXDp = boardWidth * (proj.x / 100f)
                            val sYDp = boardHeight * (proj.y / 100f)
                            val eXDp = boardWidth * (proj.targetX / 100f)
                            val eYDp = boardHeight * (proj.targetY / 100f)
                            
                            Box(modifier = Modifier.fillMaxSize()) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    val sX = size.width * (proj.x / 100f)
                                    val sY = size.height * (proj.y / 100f)
                                    val eX = size.width * (proj.targetX / 100f)
                                    val eY = size.height * (proj.targetY / 100f)
                                    
                                    val sweepPath = Path().apply {
                                        moveTo(sX, sY)
                                        quadraticTo(
                                            (sX + eX) / 2f - 30f,
                                            (sY + eY) / 2f + 30f,
                                            eX,
                                            eY
                                        )
                                    }
                                    // Dark purple-black underworld shadow
                                    drawPath(
                                        path = sweepPath,
                                        color = Color(0xFF581C87).copy(alpha = 0.7f),
                                        style = Stroke(width = 54f, cap = StrokeCap.Round)
                                    )
                                    // Hot lava-red core cleave
                                    drawPath(
                                        path = sweepPath,
                                        color = Color(0xFFEF4444),
                                        style = Stroke(width = 24f, cap = StrokeCap.Round)
                                    )
                                }
                                // Underworld fire skulls
                                Text(
                                    text = "🔥",
                                    fontSize = 12.sp,
                                    modifier = Modifier.offset(
                                        x = (sXDp + eXDp) / 2f - 10.dp,
                                        y = (sYDp + eYDp) / 2f - 20.dp
                                    )
                                )
                                Text(
                                    text = "👿",
                                    fontSize = 12.sp,
                                    modifier = Modifier.offset(
                                        x = eXDp - 10.dp,
                                        y = eYDp - 10.dp
                                    )
                                )
                            }
                        } else if (proj.type == "yasuo_q") {
                            // Swift sword thrust visual line
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val sX = size.width * (proj.x / 100f)
                                val sY = size.height * (proj.y / 100f)
                                val eX = size.width * (proj.targetX / 100f)
                                val eY = size.height * (proj.targetY / 100f)
                                drawLine(
                                    color = Color(0xFF38BDF8).copy(alpha = 0.9f),
                                    start = Offset(sX, sY),
                                    end = Offset(eX, eY),
                                    strokeWidth = 4f
                                )
                                drawLine(
                                    color = Color.White,
                                    start = Offset(sX, sY),
                                    end = Offset(eX, eY),
                                    strokeWidth = 1.8f
                                )
                            }
                        } else if (proj.type == "yasuo_q_tornado") {
                            // Spinning greyish/cyan wind tornado
                            val infiniteTransition = rememberInfiniteTransition(label = "tornado_rot")
                            val rotationAngle by infiniteTransition.animateFloat(
                                initialValue = 0f,
                                targetValue = 360f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(400),
                                    repeatMode = RepeatMode.Restart
                                ),
                                label = "rot"
                            )
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .offset(
                                        x = boardWidth * (proj.x / 100f) - 18.dp,
                                        y = boardHeight * (proj.y / 100f) - 18.dp
                                    )
                                    .graphicsLayer(rotationZ = rotationAngle),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🌪️", fontSize = 24.sp)
                            }
                        } else if (proj.type == "alpha_s1_wave") {
                            // Tech shockwave slash
                            Canvas(
                                modifier = Modifier
                                    .size(48.dp)
                                    .offset(
                                        x = boardWidth * (proj.x / 100f) - 24.dp,
                                        y = boardHeight * (proj.y / 100f) - 24.dp
                                    )
                            ) {
                                drawArc(
                                    color = Color(0xFF22D3EE),
                                    startAngle = -60f,
                                    sweepAngle = 120f,
                                    useCenter = false,
                                    style = Stroke(width = 6f, cap = StrokeCap.Round)
                                )
                                drawArc(
                                    color = Color.White,
                                    startAngle = -45f,
                                    sweepAngle = 90f,
                                    useCenter = false,
                                    style = Stroke(width = 2.5f, cap = StrokeCap.Round)
                                )
                            }
                        } else if (proj.type == "alpha_basic" || proj.type == "alpha_s2_sweep") {
                            // "vết chém công nghệ tương lai" (future tech slash) for Alpha
                            val sXDp = boardWidth * (proj.x / 100f)
                            val sYDp = boardHeight * (proj.y / 100f)
                            val eXDp = boardWidth * (proj.targetX / 100f)
                            val eYDp = boardHeight * (proj.targetY / 100f)
                            
                            Box(modifier = Modifier.fillMaxSize()) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    val sX = size.width * (proj.x / 100f)
                                    val sY = size.height * (proj.y / 100f)
                                    val eX = size.width * (proj.targetX / 100f)
                                    val eY = size.height * (proj.targetY / 100f)
                                    
                                    val slashPath = Path().apply {
                                        moveTo(sX, sY)
                                        quadraticTo(
                                            (sX + eX) / 2f + 15f,
                                            (sY + eY) / 2f - 15f,
                                            eX,
                                            eY
                                        )
                                    }
                                    // Outer neon-cyber glow
                                    drawPath(
                                        path = slashPath,
                                        color = Color(0xFF22D3EE).copy(alpha = 0.4f),
                                        style = Stroke(width = 48f, cap = StrokeCap.Round)
                                    )
                                    // Mid purple-tech line
                                    drawPath(
                                        path = slashPath,
                                        color = Color(0xFFD946EF).copy(alpha = 0.7f),
                                        style = Stroke(width = 22f, cap = StrokeCap.Round)
                                    )
                                    // Inner white-hot laser core
                                    drawPath(
                                        path = slashPath,
                                        color = Color(0xFFFFFFFF),
                                        style = Stroke(width = 10f, cap = StrokeCap.Round)
                                    )
                                }
                                // Cyber symbols floating off the slash
                                Text(
                                    text = "⚡",
                                    fontSize = 11.sp,
                                    modifier = Modifier.offset(
                                        x = (sXDp + eXDp) / 2f - 10.dp,
                                        y = (sYDp + eYDp) / 2f - 18.dp
                                    )
                                )
                                Text(
                                    text = "🤖",
                                    fontSize = 10.sp,
                                    modifier = Modifier.offset(
                                        x = eXDp - 6.dp,
                                        y = eYDp - 10.dp
                                    )
                                )
                            }
                        } else if (proj.type == "xiao_slash_visual" || proj.type == "xiao_dash") {
                            // "vết chém phong ba dạ xoa" for Xiao (green jade, glowing style)
                            val sXDp = boardWidth * (proj.x / 100f)
                            val sYDp = boardHeight * (proj.y / 100f)
                            val eXDp = boardWidth * (proj.targetX / 100f)
                            val eYDp = boardHeight * (proj.targetY / 100f)
                            
                            Box(modifier = Modifier.fillMaxSize()) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    val sX = size.width * (proj.x / 100f)
                                    val sY = size.height * (proj.y / 100f)
                                    val eX = size.width * (proj.targetX / 100f)
                                    val eY = size.height * (proj.targetY / 100f)
                                    
                                    val slashPath = Path().apply {
                                        moveTo(sX, sY)
                                        quadraticTo(
                                            (sX + eX) / 2f - 20f,
                                            (sY + eY) / 2f - 20f,
                                            eX,
                                            eY
                                        )
                                    }
                                    // Jade Green outer wind
                                    drawPath(
                                        path = slashPath,
                                        color = Color(0xFF10B981).copy(alpha = 0.5f),
                                        style = Stroke(width = 46f, cap = StrokeCap.Round)
                                    )
                                    // Glowing emerald core
                                    drawPath(
                                        path = slashPath,
                                        color = Color(0xFF6EE7B7),
                                        style = Stroke(width = 20f, cap = StrokeCap.Round)
                                    )
                                    // Bright white-teal laser center
                                    drawPath(
                                        path = slashPath,
                                        color = Color(0xFFFFFFFF),
                                        style = Stroke(width = 9f, cap = StrokeCap.Round)
                                    )
                                }
                                Text(
                                    text = "🟢",
                                    fontSize = 11.sp,
                                    modifier = Modifier.offset(
                                        x = eXDp - 6.dp,
                                        y = eYDp - 10.dp
                                    )
                                )
                            }
                        } else if (proj.type == "xiao_plunge") {
                            // Genshin-style Ultimate Plunge: Glowing green field + Yaksha's spears thrusting up
                            val pXDp = boardWidth * (proj.x / 100f)
                            val pYDp = boardHeight * (proj.y / 100f)
                            val radiusDp = boardWidth * (proj.radius / 100f)
                            
                            val progress = (proj.durationTicks.toFloat() / 24f).coerceIn(0f, 1f)
                            // Thrust up rapidly in the first 25% of duration, then hold
                            val thrust = if (progress < 0.25f) (progress / 0.25f) else 1.0f
                            // Fade out after 50% duration
                            val alphaValue = if (progress < 0.5f) 0.85f else (0.85f * (1.0f - (progress - 0.5f) / 0.5f)).coerceIn(0f, 0.85f)
                            val fieldAlpha = if (progress < 0.5f) 0.45f else (0.45f * (1.0f - (progress - 0.5f) / 0.5f)).coerceIn(0f, 0.45f)
                            
                            Box(modifier = Modifier.fillMaxSize()) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    val centerX = size.width * (proj.x / 100f)
                                    val centerY = size.height * (proj.y / 100f)
                                    val radiusPx = size.width * (proj.radius / 100f)
                                    
                                    // 1. Draw glowing green AOE circular zone
                                    drawCircle(
                                        brush = Brush.radialGradient(
                                            colors = listOf(
                                                Color(0xFF10B981).copy(alpha = fieldAlpha),
                                                Color(0xFF047857).copy(alpha = fieldAlpha * 0.4f),
                                                Color.Transparent
                                            )
                                        ),
                                        radius = radiusPx,
                                        center = Offset(centerX, centerY)
                                    )
                                    // Outer ring edge
                                    drawCircle(
                                        color = Color(0xFF6EE7B7).copy(alpha = fieldAlpha * 1.5f),
                                        radius = radiusPx,
                                        center = Offset(centerX, centerY),
                                        style = Stroke(width = 3f)
                                    )
                                    
                                    // 2. Draw multiple Yaksha's spears rising up
                                    val spearOffsets = listOf(
                                        Offset(0.0f, 0.0f),       // Center
                                        Offset(-0.5f, -0.3f),     // Top-Left
                                        Offset(0.5f, -0.4f),      // Top-Right
                                        Offset(-0.4f, 0.4f),      // Bottom-Left
                                        Offset(0.4f, 0.3f),       // Bottom-Right
                                        Offset(0.0f, -0.6f),      // Top
                                        Offset(-0.6f, 0.1f)       // Left
                                    )
                                    
                                    spearOffsets.forEach { offset ->
                                        val baseX = centerX + offset.x * radiusPx
                                        val baseY = centerY + offset.y * radiusPx
                                        
                                        val spearLen = 32f
                                        val tipY = baseY - spearLen * thrust
                                        
                                        // Draw shaft of the spear
                                        drawLine(
                                            color = Color(0xFF047857).copy(alpha = alphaValue),
                                            start = Offset(baseX, baseY),
                                            end = Offset(baseX, tipY + 8f * thrust),
                                            strokeWidth = 3f,
                                            cap = StrokeCap.Round
                                        )
                                        
                                        // Draw glowing emerald diamond spearhead
                                        val headPath = Path().apply {
                                            moveTo(baseX, tipY) // Pointed tip
                                            lineTo(baseX - 4.5f, tipY + 9f * thrust)
                                            lineTo(baseX, tipY + 12f * thrust)
                                            lineTo(baseX + 4.5f, tipY + 9f * thrust)
                                            close()
                                        }
                                        drawPath(
                                            path = headPath,
                                            color = Color(0xFF10B981).copy(alpha = alphaValue)
                                        )
                                        // Outer glowing border of spearhead
                                        drawPath(
                                            path = headPath,
                                            color = Color(0xFFE6F4EA).copy(alpha = alphaValue),
                                            style = Stroke(width = 1f)
                                        )
                                        // White-hot glowing core line down the spearhead
                                        drawLine(
                                            color = Color.White.copy(alpha = alphaValue),
                                            start = Offset(baseX, tipY),
                                            end = Offset(baseX, tipY + 12f * thrust),
                                            strokeWidth = 1.5f
                                        )
                                    }
                                }
                                
                                // Floating Anemo sparkles around the plunge
                                if (progress < 0.75f) {
                                    Text(
                                        text = "✨",
                                        fontSize = 11.sp,
                                        modifier = Modifier.offset(
                                            x = pXDp - 4.dp,
                                            y = pYDp - radiusDp / 2f - 14.dp
                                        )
                                    )
                                }
                            }
                        } else if (proj.type == "alpha_ult_laser") {
                            // Orbital Laser: Gigantic high-tech vertical cyber pillar
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .width(36.dp)
                                    .offset(
                                        x = boardWidth * (proj.x / 100f) - 18.dp,
                                        y = 0.dp
                                    )
                            ) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    // Massive glowing background column
                                    drawRect(
                                        brush = Brush.horizontalGradient(
                                            colors = listOf(
                                                Color.Transparent,
                                                Color(0xFF00FFFF).copy(alpha = 0.1f),
                                                Color(0xFF22D3EE).copy(alpha = 0.4f),
                                                Color(0xFFE0F2FE).copy(alpha = 0.8f),
                                                Color(0xFF22D3EE).copy(alpha = 0.4f),
                                                Color(0xFF00FFFF).copy(alpha = 0.1f),
                                                Color.Transparent
                                            )
                                        )
                                    )
                                    // Bright white laser core
                                    drawLine(
                                        color = Color.White,
                                        start = Offset(size.width / 2f, 0f),
                                        end = Offset(size.width / 2f, size.height),
                                        strokeWidth = 6f,
                                        cap = StrokeCap.Square
                                    )
                                    // Tech horizontal particles pulsing along the line
                                    val count = 8
                                    for (i in 0..count) {
                                        val yPos = (size.height / count) * i
                                        drawCircle(
                                            color = Color(0xFF22D3EE),
                                            radius = 8f,
                                            center = Offset(size.width / 2f, yPos)
                                        )
                                    }
                                }
                            }
                        } else if (proj.type == "alpha_beta_laser") {
                            // Cybernetic Laser (Passive Attack)
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val sX = size.width * (proj.x / 100f)
                                val sY = size.height * (proj.y / 100f)
                                val eX = size.width * (proj.targetX / 100f)
                                val eY = size.height * (proj.targetY / 100f)
                                // Thick cyan beam
                                drawLine(
                                    color = Color(0xFF00FFFF).copy(alpha = 0.8f),
                                    start = Offset(sX, sY),
                                    end = Offset(eX, eY),
                                    strokeWidth = 6f,
                                    cap = StrokeCap.Round
                                )
                                // Intensely bright core
                                drawLine(
                                    color = Color.White,
                                    start = Offset(sX, sY),
                                    end = Offset(eX, eY),
                                    strokeWidth = 2.5f,
                                    cap = StrokeCap.Round
                                )
                            }
                        } else if (proj.type == "alpha_s1_laser") {
                            // Beta precision auxiliary laser
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val sX = size.width * (proj.x / 100f)
                                val sY = size.height * (proj.y / 100f)
                                val eX = size.width * (proj.targetX / 100f)
                                val eY = size.height * (proj.targetY / 100f)
                                drawLine(
                                    color = Color(0xFF00FFFF),
                                    start = Offset(sX, sY),
                                    end = Offset(eX, eY),
                                    strokeWidth = 4.5f,
                                    cap = StrokeCap.Round
                                )
                                drawLine(
                                    color = Color.White,
                                    start = Offset(sX, sY),
                                    end = Offset(eX, eY),
                                    strokeWidth = 1.5f,
                                    cap = StrokeCap.Round
                                )
                            }
                        } else if (proj.type == "tulen_s1") {
                            // "hiệu ứng sấm sét" - Chiêu 1 Lôi Quang (zigzag lightning bolts)
                            val headX = boardWidth * (proj.x / 100f)
                            val headY = boardHeight * (proj.y / 100f)
                            
                            Box(modifier = Modifier.fillMaxSize()) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    val hX = size.width * (proj.x / 100f)
                                    val hY = size.height * (proj.y / 100f)
                                    
                                    // Glow electric halo
                                    drawCircle(
                                        color = Color(0xFF22D3EE).copy(alpha = 0.35f),
                                        radius = proj.radius * 6.5f,
                                        center = Offset(hX, hY)
                                    )
                                    drawCircle(
                                        color = Color.White,
                                        radius = proj.radius * 2.8f,
                                        center = Offset(hX, hY)
                                    )
                                    
                                    // Generate zigzag lightning bolts around the orb
                                    val random = java.util.Random(proj.hashCode().toLong() + System.currentTimeMillis() / 150)
                                    for (j in 0..3) {
                                        val angle = (j * Math.PI / 2) + (random.nextDouble() * 0.4 - 0.2)
                                        val length = proj.radius * 13f
                                        val midX = hX + Math.cos(angle).toFloat() * length * 0.5f + (random.nextFloat() * 12f - 6f)
                                        val midY = hY + Math.sin(angle).toFloat() * length * 0.5f + (random.nextFloat() * 12f - 6f)
                                        val endX = hX + Math.cos(angle).toFloat() * length
                                        val endY = hY + Math.sin(angle).toFloat() * length
                                        
                                        val boltPath = Path().apply {
                                            moveTo(hX, hY)
                                            lineTo(midX, midY)
                                            lineTo(endX, endY)
                                        }
                                        drawPath(
                                            path = boltPath,
                                            color = Color(0xFF00FFFF),
                                            style = Stroke(width = 3.5f, cap = StrokeCap.Round)
                                        )
                                        drawPath(
                                            path = boltPath,
                                            color = Color.White,
                                            style = Stroke(width = 1.2f, cap = StrokeCap.Round)
                                        )
                                    }
                                }
                                Text(
                                    text = "⚡",
                                    fontSize = 12.sp,
                                    modifier = Modifier.offset(
                                        x = headX - 6.dp,
                                        y = headY - 14.dp
                                    )
                                )
                            }
                        } else if (proj.type == "tulen_ult") {
                            // "hiệu ứng sấm sét" - Chiêu 3 Lôi Điểu (Thunderbird)
                            val headX = boardWidth * (proj.x / 100f)
                            val headY = boardHeight * (proj.y / 100f)
                            
                            Box(modifier = Modifier.fillMaxSize()) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    val hX = size.width * (proj.x / 100f)
                                    val hY = size.height * (proj.y / 100f)
                                    
                                    // Glowing purple/violet electric aura
                                    drawCircle(
                                        color = Color(0xFFE020FF).copy(alpha = 0.4f),
                                        radius = proj.radius * 5.5f,
                                        center = Offset(hX, hY)
                                    )
                                    drawCircle(
                                        color = Color.White,
                                        radius = proj.radius * 2.2f,
                                        center = Offset(hX, hY)
                                    )
                                    
                                    // Outer crackling violent storm bolts
                                    val random = java.util.Random(proj.hashCode().toLong() + System.currentTimeMillis() / 100)
                                    for (j in 0..4) {
                                        val angle = (j * 2 * Math.PI / 5) + (random.nextDouble() * 0.3 - 0.15)
                                        val length = proj.radius * 11f
                                        val midX = hX + Math.cos(angle).toFloat() * length * 0.6f + (random.nextFloat() * 10f - 5f)
                                        val midY = hY + Math.sin(angle).toFloat() * length * 0.6f + (random.nextFloat() * 10f - 5f)
                                        val endX = hX + Math.cos(angle).toFloat() * length
                                        val endY = hY + Math.sin(angle).toFloat() * length
                                        
                                        val boltPath = Path().apply {
                                            moveTo(hX, hY)
                                            lineTo(midX, midY)
                                            lineTo(endX, endY)
                                        }
                                        drawPath(
                                            path = boltPath,
                                            color = Color(0xFFC084FC),
                                            style = Stroke(width = 4.5f, cap = StrokeCap.Round)
                                        )
                                        drawPath(
                                            path = boltPath,
                                            color = Color.White,
                                            style = Stroke(width = 1.5f, cap = StrokeCap.Round)
                                        )
                                    }
                                }
                                // Center magical thunderbird / lightning bird emoji with extra purple electricity
                                Row(
                                    modifier = Modifier.offset(
                                        x = headX - 14.dp,
                                        y = headY - 14.dp
                                    ),
                                    horizontalArrangement = Arrangement.spacedBy(1.dp)
                                ) {
                                    Text(
                                        text = "⚡🦅",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }
                        } else if (proj.type == "valhein_s1" || proj.type == "valhein_s2") {
                            // Valhein S1/S2: Spinning Ninja Shurikens!
                            val pXDp = boardWidth * (proj.x / 100f)
                            val pYDp = boardHeight * (proj.y / 100f)
                            val color = if (proj.type == "valhein_s1") Color(0xFFFF3333) else Color(0xFFFACC15) // Red or Yellow shuriken glow
                            
                            Box(
                                modifier = Modifier
                                    .offset(x = pXDp - 12.dp, y = pYDp - 12.dp)
                                    .size(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    val rotation = (System.currentTimeMillis() % 360).toFloat()
                                    rotate(rotation) {
                                        // Draw a classic 4-pointed ninja shuriken star!
                                        val path = Path().apply {
                                            val r = size.width / 2f
                                            val cx = size.width / 2f
                                            val cy = size.height / 2f
                                            // Draw 4 curved points
                                            moveTo(cx, cy - r)
                                            quadraticTo(cx + r*0.25f, cy - r*0.25f, cx + r, cy)
                                            quadraticTo(cx + r*0.25f, cy + r*0.25f, cx, cy + r)
                                            quadraticTo(cx - r*0.25f, cy + r*0.25f, cx - r, cy)
                                            quadraticTo(cx - r*0.25f, cy - r*0.25f, cx, cy - r)
                                            close()
                                        }
                                        drawPath(
                                            path = path,
                                            color = color
                                        )
                                        drawPath(
                                            path = path,
                                            color = Color.White,
                                            style = Stroke(width = 2f)
                                        )
                                        drawCircle(
                                            color = Color.Black,
                                            radius = size.width * 0.15f,
                                            center = Offset(size.width / 2f, size.height / 2f)
                                        )
                                    }
                                }
                            }
                        } else if (proj.type == "valhein_ult") {
                            // Valhein S3: White Silver Bullets!
                            val pXDp = boardWidth * (proj.x / 100f)
                            val pYDp = boardHeight * (proj.y / 100f)
                            Box(
                                modifier = Modifier
                                    .offset(x = pXDp - 7.dp, y = pYDp - 7.dp)
                                    .size(14.dp)
                                    .background(Color.White, CircleShape)
                                    .border(2.dp, Color(0xFFE2E8F0), CircleShape) // Silver gray border
                            ) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    drawCircle(
                                        color = Color.White.copy(alpha = 0.6f),
                                        radius = size.width * 0.8f,
                                        center = Offset(size.width / 2f, size.height / 2f)
                                    )
                                }
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .size((proj.radius * 2.5f).dp)
                                    .offset(
                                        x = boardWidth * (proj.x / 100f) - (proj.radius * 1.25f).dp,
                                        y = boardHeight * (proj.y / 100f) - (proj.radius * 1.25f).dp
                                    )
                                    .background(Color(proj.color), CircleShape)
                            )
                        }
                    }

                    // 8. Floating Damage Text overlays
                    mobaDamageTexts.forEach { txt ->
                        Text(
                            text = txt.text,
                            color = Color(txt.color),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 11.sp,
                            modifier = Modifier.offset(
                                x = boardWidth * (txt.x / 100f) - 15.dp,
                                y = boardHeight * (txt.y / 100f) - 8.dp
                            )
                        )
                    }

                    // 8b. Floating Zoom button on top-left of the game screen
                    IconButton(
                        onClick = { viewModel.setMobaZoomed(!mobaIsZoomed) },
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                            .size(32.dp)
                            .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                            .border(1.dp, NeonCyan.copy(alpha = 0.6f), CircleShape)
                    ) {
                        Icon(
                            imageVector = if (mobaIsZoomed) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                            contentDescription = "Zoom Game",
                            tint = NeonCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Top-Right Status Stack (Ping + Combo Counter HUD)
                    Column(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp),
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // 9. High-Ping warning overlay on the game screen
                        if (isSimulating && currentPing > 120) {
                            Box(
                                modifier = Modifier
                                    .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                                    .border(1.dp, Color.Red.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Box(modifier = Modifier.size(6.dp).background(Color.Red, CircleShape))
                                    Text(
                                        text = "PING: ${currentPing}ms",
                                        color = Color.Red,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }

                        // Persistent Combo Counter HUD
                        val comboColor = if (mobaComboActive && mobaComboCount > 0) {
                            when (mobaHero) {
                                "Tulen" -> Color(0xFF00FFFF) // Electric Cyan
                                "Murad" -> Color(0xFFFBBF24) // Gold
                                "Yasuo" -> Color(0xFFFFCC33) // Elegant Yellow
                                "Alpha" -> Color(0xFF22D3EE) // Bright Cyan
                                "Xiao" -> Color(0xFF10B981) // Jade Green
                                else -> Color(0xFFE11D48) // Red
                            }
                        } else {
                            Color.Gray
                        }

                        // Flash effect blending to pure white
                        val flashVal = comboFlash.value
                        val blendedColor = if (mobaComboActive && mobaComboCount > 0) {
                            if (flashVal > 0f) {
                                Color(
                                    red = comboColor.red + (1f - comboColor.red) * flashVal,
                                    green = comboColor.green + (1f - comboColor.green) * flashVal,
                                    blue = comboColor.blue + (1f - comboColor.blue) * flashVal,
                                    alpha = comboColor.alpha
                                )
                            } else {
                                comboColor
                            }
                        } else {
                            Color.Gray
                        }

                        Box(
                            modifier = Modifier
                                .width(65.dp)
                                .graphicsLayer(
                                    scaleX = comboScale.value,
                                    scaleY = comboScale.value
                                )
                                .background(Color.Black.copy(alpha = 0.75f), RoundedCornerShape(6.dp))
                                .border(
                                    1.dp,
                                    if (mobaComboActive && mobaComboCount > 0) blendedColor.copy(alpha = 0.8f) else Color.DarkGray.copy(alpha = 0.5f),
                                    RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 5.dp, vertical = 3.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                // Title & Status text
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "COMBO",
                                        fontSize = 7.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (mobaComboActive && mobaComboCount > 0) Color.White else Color.Gray
                                    )
                                    // Bullet glow indicator
                                    Box(
                                        modifier = Modifier
                                            .size(3.5.dp)
                                            .background(
                                                if (mobaComboActive && mobaComboCount > 0) blendedColor else Color.DarkGray,
                                                CircleShape
                                            )
                                    )
                                }

                                // Combo Number count
                                Text(
                                    text = if (mobaComboActive && mobaComboCount > 0) "x$mobaComboCount" else "x0",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = blendedColor
                                )

                                // Progress Timer Bar
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(2.dp)
                                        .background(Color(0xFF1E293B), RoundedCornerShape(1.dp))
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(mobaComboTimeProgress)
                                            .fillMaxHeight()
                                            .background(blendedColor, RoundedCornerShape(1.dp))
                                    )
                                }
                            }
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .then(
                            if (isZoomed) Modifier.weight(1f).verticalScroll(mobaScrollState) else Modifier
                        ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 10. Tactical Status HUD (Premium status & objective hub outside the canvas)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A).copy(alpha = 0.85f)),
                    border = BorderStroke(1.dp, CardSpaceBorder.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Top Row: Hero Info, Stacks & Objective Integrity
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Hero Name and Passive Badges
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .background(
                                            if (mobaHero == "Tulen") Color(0xFF06B6D4).copy(alpha = 0.2f)
                                            else if (mobaHero == "Murad") Color(0xFFF59E0B).copy(alpha = 0.2f)
                                            else if (mobaHero == "Yasuo") Color(0xFF38BDF8).copy(alpha = 0.2f)
                                            else if (mobaHero == "Alpha") Color(0xFF22D3EE).copy(alpha = 0.2f)
                                            else if (mobaHero == "Xiao") Color(0xFF10B981).copy(alpha = 0.2f)
                                            else Color(0xFFE11D48).copy(alpha = 0.2f),
                                            RoundedCornerShape(4.dp)
                                        )
                                        .border(
                                            1.dp,
                                            if (mobaHero == "Tulen") Color(0xFF06B6D4)
                                            else if (mobaHero == "Murad") Color(0xFFF59E0B)
                                            else if (mobaHero == "Yasuo") Color(0xFF38BDF8)
                                            else if (mobaHero == "Alpha") Color(0xFF22D3EE)
                                            else if (mobaHero == "Xiao") Color(0xFF10B981)
                                            else Color(0xFFE11D48),
                                            RoundedCornerShape(4.dp)
                                        )
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = mobaHero,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }

                                // Passive Stacks details with dynamic color indication
                                        val passiveText = when (mobaHero) {
                                            "Yasuo" -> if (mobaPassiveStacks == 2) "🌪️ HASAGI!" else "Tụ bão: $mobaPassiveStacks/2"
                                            "Murad" -> if (mobaPassiveStacks >= 4) "🔓 KHAI ẤN!" else "Tích ấn: $mobaPassiveStacks/4"
                                            "Tulen" -> if (mobaPassiveStacks >= 5) "⚡ LÔI ẤN SẴN SÀNG" else "Lôi ấn: $mobaPassiveStacks/5"
                                            "Alpha" -> if (mobaPassiveStacks >= 2) "🛸 BETA LASER!" else "Cyber Mark: $mobaPassiveStacks/2"
                                            "Xiao" -> "🌀 PHONG THẦN DẠ XOA"
                                            else -> "Tốc đánh: +${mobaPassiveStacks * 6}%"
                                        }
                                        val passiveColor = when (mobaHero) {
                                            "Yasuo" -> if (mobaPassiveStacks == 2) Color(0xFF38BDF8) else Color.LightGray
                                            "Murad" -> if (mobaPassiveStacks >= 4) Color(0xFFF59E0B) else Color.LightGray
                                            "Tulen" -> if (mobaPassiveStacks >= 5) Color(0xFF22D3EE) else Color.LightGray
                                            "Alpha" -> if (mobaPassiveStacks >= 2) Color(0xFF00FFFF) else Color.LightGray
                                            "Xiao" -> Color(0xFF10B981)
                                            else -> Color(0xFFFCD34D)
                                        }
                                Text(
                                    text = passiveText,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = passiveColor
                                )
                            }

                            // Objective Health Indicators (Towers & Castles)
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                    Box(modifier = Modifier.size(5.dp).background(Color(0xFF38BDF8), CircleShape))
                                    Text(
                                        text = "L.Đài Ta: ${mobaAllyCastleHP.toInt()}",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (mobaAllyCastleHP < 1000f) Color(0xFFEF4444) else Color.LightGray
                                    )
                                }
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                    Box(modifier = Modifier.size(5.dp).background(Color(0xFFFCA5A5), CircleShape))
                                    Text(
                                        text = "L.Đài Địch: ${mobaEnemyCastleHP.toInt()}",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (mobaEnemyCastleHP < 1000f) Color(0xFF10B981) else Color.LightGray
                                    )
                                }
                            }
                        }

                        // Bottom Row: Beautiful HP & MP Progress Bars
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // HP Progress bar
                            val hpFraction = if (mobaHeroMaxHP > 0f) (mobaHeroHP / mobaHeroMaxHP).coerceIn(0f, 1f) else 0f
                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("HP (SINH MỆNH)", fontSize = 8.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                                    Text("${mobaHeroHP.toInt()}/${mobaHeroMaxHP.toInt()}", fontSize = 9.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                }
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .background(Color(0xFF1F2937), RoundedCornerShape(4.dp))
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(hpFraction)
                                            .fillMaxHeight()
                                            .background(
                                                Brush.horizontalGradient(
                                                    colors = listOf(Color(0xFF047857), Color(0xFF10B981))
                                                ),
                                                RoundedCornerShape(4.dp)
                                            )
                                    )
                                }
                            }

                            // MP Progress bar
                            val mpFraction = if (mobaHeroMaxMP > 0f) (mobaHeroMP / mobaHeroMaxMP).coerceIn(0f, 1f) else 0f
                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("MP (NĂNG LƯỢNG)", fontSize = 8.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                                    Text("${mobaHeroMP.toInt()}/${mobaHeroMaxMP.toInt()}", fontSize = 9.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                }
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .background(Color(0xFF1F2937), RoundedCornerShape(4.dp))
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(mpFraction)
                                            .fillMaxHeight()
                                            .background(
                                                Brush.horizontalGradient(
                                                    colors = listOf(Color(0xFF1D4ED8), Color(0xFF3B82F6))
                                                ),
                                                RoundedCornerShape(4.dp)
                                            )
                                    )
                                }
                            }
                        }
                    }

                    // MOBA Pause Overlay
                    val mobaIsPaused by viewModel.mobaIsPaused.collectAsState()
                    if (mobaIsPaused) {
                        val targetPing by viewModel.targetPing.collectAsState()
                        val targetJitter by viewModel.targetJitter.collectAsState()
                        val targetLoss by viewModel.targetLoss.collectAsState()
                        
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.9f))
                                .pointerInput(Unit) { detectTapGestures { } },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(14.dp),
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Pause,
                                        contentDescription = "Game Paused",
                                        tint = NeonPink,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Text(
                                        text = t("moba_arena_paused"),
                                        color = NeonPink,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    )
                                }
                                
                                Text(
                                    text = t("fps_paused_current_config"),
                                    color = Color.LightGray,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth(0.9f)
                                        .background(Color(0xFF151324), RoundedCornerShape(8.dp))
                                        .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                                        .padding(10.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(t("fps_paused_latency"), color = Color.Gray, fontSize = 10.sp)
                                        Text("${targetPing} ms", color = NeonCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(t("fps_paused_jitter"), color = Color.Gray, fontSize = 10.sp)
                                        Text("${targetJitter} ms", color = ElegantGold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(t("fps_paused_loss"), color = Color.Gray, fontSize = 10.sp)
                                        Text("${targetLoss} %", color = NeonPink, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                // Button to Reset delay/latency
                                Button(
                                    onClick = { 
                                        viewModel.resetNetworkDelay()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth(0.9f),
                                    contentPadding = PaddingValues(vertical = 4.dp, horizontal = 12.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "Reset Network",
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(t("fps_paused_reset_network_btn"), color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(0.9f),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    // Resume Button
                                    Button(
                                        onClick = { viewModel.setMobaPaused(false) },
                                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f),
                                        contentPadding = PaddingValues(vertical = 4.dp, horizontal = 8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PlayArrow,
                                            contentDescription = "Resume",
                                            tint = Color.Black,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(t("fps_paused_resume_btn"), color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }

                                    // Exit Button
                                    Button(
                                        onClick = { 
                                            viewModel.stopMobaGame()
                                            viewModel.setMobaPaused(false)
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.8f)),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f),
                                        contentPadding = PaddingValues(vertical = 4.dp, horizontal = 8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Stop,
                                            contentDescription = "Exit",
                                            tint = Color.White,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(t("fps_paused_exit_btn"), color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                // Log display bubble (with fixed height to prevent vertical shifting during notifications)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(38.dp)
                        .background(Color(0xFF1E293B).copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                        .border(1.dp, CardSpaceBorder, RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = getLocalizedText(mobaLog),
                        fontSize = 11.sp,
                        color = if (mobaLog.contains("CẢNH BÁO") || mobaLog.contains("WARNING") || mobaLog.contains("RỤNG") || mobaLog.contains("MẤT") || mobaLog.contains("LOSS")) Color(0xFFF43F5E) else Color.LightGray,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Controller Mode Selector & Match Utilities Row
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Controller Switch Tabs
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { controllerMode = "mobile" },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (controllerMode == "mobile") NeonCyan.copy(alpha = 0.2f) else Color.Transparent,
                                contentColor = if (controllerMode == "mobile") NeonCyan else Color.Gray
                            ),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                            modifier = Modifier.height(26.dp)
                        ) {
                            Text(t("moba_phone_keys"), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = { controllerMode = "classic" },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (controllerMode == "classic") NeonCyan.copy(alpha = 0.2f) else Color.Transparent,
                                contentColor = if (controllerMode == "classic") NeonCyan else Color.Gray
                            ),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                            modifier = Modifier.height(26.dp)
                        ) {
                            Text(t("moba_classic_keys"), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Quit Button (Surrender Match)
                    Button(
                        onClick = { viewModel.stopMobaGame() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFEF4444).copy(alpha = 0.15f),
                            contentColor = Color(0xFFFCA5A5)
                        ),
                        border = BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.4f)),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                        modifier = Modifier.height(26.dp)
                    ) {
                        Text(t("moba_surrender"), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                if (controllerMode == "classic") {
                    // Live Controls: Classic Columnar Skill Panels
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Skill buttons (Left column)
                        Column(
                            modifier = Modifier.weight(1.3f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val isTulen = mobaHero == "Tulen"
                            val isMurad = mobaHero == "Murad"
                            val isYasuo = mobaHero == "Yasuo"
                            val isAlpha = mobaHero == "Alpha"
                            val isXiao = mobaHero == "Xiao"
                            val skills = if (isTulen) {
                                listOf(
                                    Triple("Chiêu 1: Lôi Quang", "⚡ Sát thương fan-shape", 55f),
                                    Triple("Chiêu 2: Lôi Động", "✨ Blink dịch chuyển", 60f),
                                    Triple("Ult: Lôi Điểu", "🔥 Kết liễu cực đau", 100f)
                                )
                            } else if (isMurad) {
                                listOf(
                                    Triple("Chiêu 1: Vô Ảnh Vực", "⚔️ Lướt & Bóng ảo (Stun)", 55f),
                                    Triple("Chiêu 2: Vô Ảnh Trận", "🛡️ Né đòn & Chậm rìa", 60f),
                                    Triple("Ult: Ảo Ảnh Trảm", "🔥 Trảm liên hoàn (Cần 4 Ấn)", 80f)
                                )
                            } else if (isYasuo) {
                                listOf(
                                    Triple("Chiêu 1: Bão Kiếm", "⚔️ Đâm kiếm tích lũy tụ bão phóng lốc xoáy", 0f),
                                    Triple("Chiêu 2: Tường Gió", "🌪️ Dựng tường chắn mọi chiêu thức địch", 0f),
                                    Triple("Ult: Trăn Trối", "⚡ Bay chém địch bị hất tung (Cần khống chế)", 0f)
                                )
                            } else if (isAlpha) {
                                listOf(
                                    Triple("Chiêu 1: Đao Quét Thăng Hoa", "🤖 Quét đao thăng hoa & Beta phụ kích", 50f),
                                    Triple("Chiêu 2: Đao Quét Năng Lượng", "🛡️ Vung thương quét tròn & Hồi HP", 60f),
                                    Triple("Ult: Mũi Giáo Alpha", "🔥 Lao thẳng hất tung & Beta xả siêu Orbital Laser", 100f)
                                )
                            } else if (isXiao) {
                                listOf(
                                    Triple("Chiêu 1: Vũ Điệu Chinh Phục", "🟢 Chém 3 đường Gió Xanh phong ấn & Hồi 10% HP", 0f),
                                    Triple("Chiêu 2: Gió Tung Hoành", "💨 Lướt kép 2 lần né tránh đột kích", 0f),
                                    Triple("Ult: Vũ Điệu Đại Thánh", "🔥 Nhảy vút lên không & Đâm plunge 2 lần chấn động", 0f)
                                )
                            } else {
                                listOf(
                                    Triple("Chiêu 1: Chuyến Săn", "🏹 Tiêu đỏ nổ lan", 50f),
                                    Triple("Chiêu 2: Lời Nguyền", "🌀 Tiêu vàng gây choáng", 65f),
                                    Triple("Ult: Bão Đạn", "🔥 Xả bão 6 đạn bạc", 110f)
                                )
                            }

                            skills.forEachIndexed { idx, skill ->
                                val cd = mobaSkillCooldowns[idx]
                                val isCd = cd > 0f
                                val ultLocked = (isMurad && idx == 2 && mobaPassiveStacks < 4) || (isYasuo && idx == 2 && !mobaEnemyIsStunned)

                                Button(
                                    onClick = { viewModel.castMobaSkill(idx) },
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = !isCd && !ultLocked && mobaHeroHP > 0f,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (ultLocked) Color.Red.copy(alpha = 0.2f) else if (idx == 2) Color(0xFF701A75) else Color(0xFF1E293B),
                                        disabledContainerColor = Color(0xFF0F172A),
                                        contentColor = Color.White,
                                        disabledContentColor = Color.Gray
                                    ),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            val locFirst = getLocalizedText(skill.first)
                                            val sealText = t("moba_seal_locked")
                                            Text(
                                                text = if (ultLocked) "$locFirst$sealText" else if (isCd) "$locFirst (${String.format("%.1f", cd)}s)" else locFirst,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (ultLocked) Color.Red else if (isCd) Color.Gray else NeonCyan
                                            )
                                            Text(
                                                text = getLocalizedText(skill.second),
                                                fontSize = 9.sp,
                                                color = Color.LightGray
                                            )
                                        }
                                        Text(
                                            text = "💧 ${skill.third.toInt()}",
                                            fontSize = 10.sp,
                                            color = Color(0xFF60A5FA),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }

                        // Basic attack Column (Right column - Cleared from match quit button for ergonomics)
                        Column(
                            modifier = Modifier.weight(0.7f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            // Giant attack button
                            Button(
                                onClick = { viewModel.triggerMobaBasicAttack() },
                                modifier = Modifier
                                    .size(76.dp)
                                    .border(2.dp, ElegantGold, CircleShape),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFF59E0B),
                                    contentColor = Color.Black
                                ),
                                shape = CircleShape,
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("ĐÁNH", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                                    Text("THƯỜNG", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                } else {
                    // Live Controls: Mobile Phone Split Joystick Layout (Highly optimized for compact screens)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 1. D-Pad Joystick (Left Side) - UX improved with circular shape and clean arrows
                        Box(
                            modifier = Modifier
                                .size(125.dp)
                                .background(Color(0xFF1E293B).copy(alpha = 0.5f), CircleShape)
                                .border(1.5.dp, NeonCyan.copy(alpha = 0.3f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            // Up Button
                            IconButton(
                                onClick = { viewModel.moveHeroInDirection(MobaMoveDirection.UP) },
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .size(44.dp)
                                    .pointerInput(Unit) {
                                        detectTapGestures(
                                            onPress = {
                                                viewModel.setMobaMoveDirection(MobaMoveDirection.UP)
                                                try {
                                                    awaitRelease()
                                                } finally {
                                                    viewModel.setMobaMoveDirection(MobaMoveDirection.NONE)
                                                }
                                            }
                                        )
                                    }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowUp,
                                    contentDescription = "Di chuyển Lên",
                                    tint = NeonCyan,
                                    modifier = Modifier.size(26.dp)
                                )
                            }

                            // Down Button
                            IconButton(
                                onClick = { viewModel.moveHeroInDirection(MobaMoveDirection.DOWN) },
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .size(44.dp)
                                    .pointerInput(Unit) {
                                        detectTapGestures(
                                            onPress = {
                                                viewModel.setMobaMoveDirection(MobaMoveDirection.DOWN)
                                                try {
                                                    awaitRelease()
                                                } finally {
                                                    viewModel.setMobaMoveDirection(MobaMoveDirection.NONE)
                                                }
                                            }
                                        )
                                    }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Di chuyển Xuống",
                                    tint = NeonCyan,
                                    modifier = Modifier.size(26.dp)
                                )
                            }

                            // Left Button
                            IconButton(
                                onClick = { viewModel.moveHeroInDirection(MobaMoveDirection.LEFT) },
                                modifier = Modifier
                                    .align(Alignment.CenterStart)
                                    .size(44.dp)
                                    .pointerInput(Unit) {
                                        detectTapGestures(
                                            onPress = {
                                                viewModel.setMobaMoveDirection(MobaMoveDirection.LEFT)
                                                try {
                                                    awaitRelease()
                                                } finally {
                                                    viewModel.setMobaMoveDirection(MobaMoveDirection.NONE)
                                                }
                                            }
                                        )
                                    }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowLeft,
                                    contentDescription = "Di chuyển Trái",
                                    tint = NeonCyan,
                                    modifier = Modifier.size(26.dp)
                                )
                            }

                            // Right Button
                            IconButton(
                                onClick = { viewModel.moveHeroInDirection(MobaMoveDirection.RIGHT) },
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .size(44.dp)
                                    .pointerInput(Unit) {
                                        detectTapGestures(
                                            onPress = {
                                                viewModel.setMobaMoveDirection(MobaMoveDirection.RIGHT)
                                                try {
                                                    awaitRelease()
                                                } finally {
                                                    viewModel.setMobaMoveDirection(MobaMoveDirection.NONE)
                                                }
                                            }
                                        )
                                    }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowRight,
                                    contentDescription = "Di chuyển Phải",
                                    tint = NeonCyan,
                                    modifier = Modifier.size(26.dp)
                                )
                            }

                            // Up-Left Button
                            IconButton(
                                onClick = { viewModel.moveHeroInDirection(MobaMoveDirection.UP_LEFT) },
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .size(36.dp)
                                    .pointerInput(Unit) {
                                        detectTapGestures(
                                            onPress = {
                                                viewModel.setMobaMoveDirection(MobaMoveDirection.UP_LEFT)
                                                try {
                                                    awaitRelease()
                                                } finally {
                                                    viewModel.setMobaMoveDirection(MobaMoveDirection.NONE)
                                                }
                                            }
                                        )
                                    }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowUp,
                                    contentDescription = "Di chuyển Lên Trái",
                                    tint = NeonCyan.copy(alpha = 0.8f),
                                    modifier = Modifier
                                        .size(20.dp)
                                        .graphicsLayer(rotationZ = -45f)
                                )
                            }

                            // Up-Right Button
                            IconButton(
                                onClick = { viewModel.moveHeroInDirection(MobaMoveDirection.UP_RIGHT) },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(36.dp)
                                    .pointerInput(Unit) {
                                        detectTapGestures(
                                            onPress = {
                                                viewModel.setMobaMoveDirection(MobaMoveDirection.UP_RIGHT)
                                                try {
                                                    awaitRelease()
                                                } finally {
                                                    viewModel.setMobaMoveDirection(MobaMoveDirection.NONE)
                                                }
                                            }
                                        )
                                    }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowUp,
                                    contentDescription = "Di chuyển Lên Phải",
                                    tint = NeonCyan.copy(alpha = 0.8f),
                                    modifier = Modifier
                                        .size(20.dp)
                                        .graphicsLayer(rotationZ = 45f)
                                )
                            }

                            // Down-Left Button
                            IconButton(
                                onClick = { viewModel.moveHeroInDirection(MobaMoveDirection.DOWN_LEFT) },
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .size(36.dp)
                                    .pointerInput(Unit) {
                                        detectTapGestures(
                                            onPress = {
                                                viewModel.setMobaMoveDirection(MobaMoveDirection.DOWN_LEFT)
                                                try {
                                                    awaitRelease()
                                                } finally {
                                                    viewModel.setMobaMoveDirection(MobaMoveDirection.NONE)
                                                }
                                            }
                                        )
                                    }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Di chuyển Xuống Trái",
                                    tint = NeonCyan.copy(alpha = 0.8f),
                                    modifier = Modifier
                                        .size(20.dp)
                                        .graphicsLayer(rotationZ = 45f)
                                )
                            }

                            // Down-Right Button
                            IconButton(
                                onClick = { viewModel.moveHeroInDirection(MobaMoveDirection.DOWN_RIGHT) },
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .size(36.dp)
                                    .pointerInput(Unit) {
                                        detectTapGestures(
                                            onPress = {
                                                viewModel.setMobaMoveDirection(MobaMoveDirection.DOWN_RIGHT)
                                                try {
                                                    awaitRelease()
                                                } finally {
                                                    viewModel.setMobaMoveDirection(MobaMoveDirection.NONE)
                                                }
                                            }
                                        )
                                    }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Di chuyển Xuống Phải",
                                    tint = NeonCyan.copy(alpha = 0.8f),
                                    modifier = Modifier
                                        .size(20.dp)
                                        .graphicsLayer(rotationZ = -45f)
                                )
                            }

                            // Center STOP Button
                            IconButton(
                                onClick = {
                                    viewModel.setMobaMoveDirection(MobaMoveDirection.NONE)
                                    viewModel.moveHeroTo(mobaHeroX, mobaHeroY)
                                },
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .size(34.dp)
                                    .background(Color.Red.copy(alpha = 0.25f), CircleShape)
                                    .border(1.dp, Color.Red.copy(alpha = 0.5f), CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Stop,
                                    contentDescription = "Dừng Lại",
                                    tint = Color.Red,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }

                        // Decorative Center Spacing (Adds realistic gaming console flavor and splits columns)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(60.dp)
                                .padding(horizontal = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "ARENA 🎮 CONSOLE",
                                    fontSize = 8.sp,
                                    color = Color.Gray.copy(alpha = 0.6f),
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Box(
                                    modifier = Modifier
                                        .size(32.dp, 2.dp)
                                        .background(Color.Gray.copy(alpha = 0.2f), RoundedCornerShape(1.dp))
                                )
                            }
                        }

                        // 3. Compact Curved Skill Cluster (Right Side - Fully ergonomic curved placement)
                        Box(modifier = Modifier.size(135.dp, 115.dp)) {
                            val isMurad = mobaHero == "Murad"
                            val isYasuo = mobaHero == "Yasuo"
                            
                            // S1 Button
                            val cd1 = mobaSkillCooldowns[0]
                            val isCd1 = cd1 > 0f
                            Button(
                                onClick = { viewModel.castMobaSkill(0) },
                                modifier = Modifier.size(38.dp).offset(x = 4.dp, y = 62.dp),
                                enabled = mobaHeroHP > 0f,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isCd1) Color(0xFF0F172A) else Color(0xFF1D4ED8),
                                    contentColor = Color.White
                                ),
                                shape = CircleShape,
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text(if (isCd1) "${cd1.toInt()}" else "S1", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (isCd1) Color.Gray else NeonCyan)
                            }

                            // S2 Button
                            val cd2 = mobaSkillCooldowns[1]
                            val isCd2 = cd2 > 0f
                            Button(
                                onClick = { viewModel.castMobaSkill(1) },
                                modifier = Modifier.size(38.dp).offset(x = 32.dp, y = 16.dp),
                                enabled = mobaHeroHP > 0f,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isCd2) Color(0xFF0F172A) else Color(0xFF1D4ED8),
                                    contentColor = Color.White
                                ),
                                shape = CircleShape,
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text(if (isCd2) "${cd2.toInt()}" else "S2", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (isCd2) Color.Gray else NeonCyan)
                            }

                            // Ult Button
                            val cd3 = mobaSkillCooldowns[2]
                            val isCd3 = cd3 > 0f
                            val ultLocked = (isMurad && mobaPassiveStacks < 4) || (isYasuo && !mobaEnemyIsStunned)
                            Button(
                                onClick = { viewModel.castMobaSkill(2) },
                                modifier = Modifier.size(44.dp).offset(x = 84.dp, y = 2.dp),
                                enabled = mobaHeroHP > 0f,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (ultLocked) Color.Red.copy(alpha = 0.3f) else if (isCd3) Color(0xFF0F172A) else Color(0xFF701A75),
                                    contentColor = Color.White
                                ),
                                shape = CircleShape,
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text(if (ultLocked) "🔒" else if (isCd3) "${cd3.toInt()}" else "ULT", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (isCd3) Color.Gray else Color.White)
                            }

                            // Giant Basic Attack Button
                            Button(
                                onClick = { viewModel.triggerMobaBasicAttack() },
                                modifier = Modifier.size(54.dp).offset(x = 80.dp, y = 56.dp).border(1.5.dp, ElegantGold, CircleShape),
                                enabled = mobaHeroHP > 0f,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFF59E0B),
                                    contentColor = Color.Black
                                ),
                                shape = CircleShape,
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text(t("moba_attack_btn"), fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
                            }
                        }
                    }
                }
                } // End of inner Column
            }
            "victory", "defeat" -> {
                val mobaGameOverShowSplash by viewModel.mobaGameOverShowSplash.collectAsState()
                if (mobaGameOverShowSplash) {
                    val isVictory = mobaState == "victory"
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.95f))
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                            Text(
                                text = if (isVictory) t("moba_big_victory") else t("moba_big_defeat"),
                                fontSize = 54.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 4.sp,
                                color = if (isVictory) Color(0xFF10B981) else Color(0xFFEF4444)
                            )
                            
                            Text(
                                text = if (isVictory) t("moba_congrats_victory") else t("moba_congrats_defeat"),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.LightGray
                            )
                            
                            Icon(
                                imageVector = if (isVictory) Icons.Filled.Info else Icons.Filled.Warning,
                                contentDescription = null,
                                tint = if (isVictory) Color(0xFF10B981) else Color(0xFFEF4444),
                                modifier = Modifier.size(96.dp)
                            )
                            
                            Button(
                                onClick = { viewModel.dismissMobaSplash() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isVictory) Color(0xFF10B981) else Color(0xFFEF4444),
                                    contentColor = Color.Black
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth(0.8f)
                                    .height(44.dp)
                            ) {
                                Text(
                                    text = t("moba_confirm_action") ?: "Xác nhận",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MobaHeroSelection(
    mobaHero: String,
    mobaSelectedEnemy: String,
    mobaUnlockedHeroes: Set<String>,
    mobaWinsCount: Int,
    mobaWinsForBoss: Int,
    onSelectHero: (String) -> Unit,
    onSelectEnemy: (String) -> Unit,
    onResetBossProgress: () -> Unit,
    onStartGame: () -> Unit
) {
    var detailedHeroName by remember { mutableStateOf<String?>(null) }
    var isViewDetailsForEnemy by remember { mutableStateOf(false) }

    detailedHeroName?.let { heroName ->
        MobaHeroDetailsDialog(
            heroName = heroName,
            isEnemy = isViewDetailsForEnemy,
            isMalochUnlocked = mobaUnlockedHeroes.contains("Maloch"),
            onSelectHero = {
                onSelectHero(it)
                detailedHeroName = null
            },
            onSelectEnemy = {
                onSelectEnemy(it)
                detailedHeroName = null
            },
            onDismiss = {
                detailedHeroName = null
            }
        )
    }

    fun getHeroColor(name: String): Color {
        return when (name) {
            "Tulen" -> Color(0xFF06B6D4)
            "Tulen hắc pháp sư" -> Color(0xFF8B5CF6)
            "Valhein" -> Color(0xFFF59E0B)
            "Valhein ma cà rồng" -> Color(0xFFEF4444)
            "Murad" -> Color(0xFFF59E0B)
            "Murad hoàng tử suy tàn" -> Color(0xFFA855F7)
            "Yasuo" -> Color(0xFF38BDF8)
            "Yasuo cơn gió cuồng ma" -> Color(0xFF64748B)
            "Alpha" -> Color(0xFF22D3EE)
            "Alpha kẻ kí sinh" -> Color(0xFFEC4899)
            "Xiao" -> Color(0xFF10B981)
            "Xiao nghiệp chướng" -> Color(0xFF059669)
            "Maloch" -> Color(0xFFDC2626)
            else -> Color.Gray
        }
    }

    fun getHeroIcon(name: String): String {
        return when (name) {
            "Tulen" -> "⚡"
            "Tulen hắc pháp sư" -> "🔮"
            "Valhein" -> "🏹"
            "Valhein ma cà rồng" -> "🧛"
            "Murad" -> "⚔️"
            "Murad hoàng tử suy tàn" -> "⏳"
            "Yasuo" -> "🌪️"
            "Yasuo cơn gió cuồng ma" -> "💀"
            "Alpha" -> "🤖"
            "Alpha kẻ kí sinh" -> "👾"
            "Xiao" -> "🟢"
            "Xiao nghiệp chướng" -> "👹"
            "Maloch" -> "👿"
            else -> "👤"
        }
    }

    val playableHeroes = listOf(
        Triple("Tulen", "⚡", "Pháp Sư"),
        Triple("Valhein", "🏹", "Xạ Thủ"),
        Triple("Murad", "⚔️", "Sát Thủ"),
        Triple("Yasuo", "🌪️", "Đấu Sĩ"),
        Triple("Alpha", "🤖", "Đấu Sĩ"),
        Triple("Xiao", "🟢", "Sát Thủ"),
        Triple("Maloch", "👿", "Đỡ Đòn")
    )

    val enemiesList = listOf(
        "Tulen", "Valhein", "Murad", "Yasuo", "Alpha", "Xiao", "Maloch",
        "Tulen hắc pháp sư", "Valhein ma cà rồng", "Murad hoàng tử suy tàn",
        "Yasuo cơn gió cuồng ma", "Alpha kẻ kí sinh", "Xiao nghiệp chướng"
    )

    var enemyTab by remember { mutableStateOf(0) } // 0 = Thường, 1 = Boss

    val filteredEnemies = if (enemyTab == 0) {
        listOf("Tulen", "Valhein", "Murad", "Yasuo", "Alpha", "Xiao", "Maloch")
    } else {
        listOf(
            "Tulen hắc pháp sư", "Valhein ma cà rồng", "Murad hoàng tử suy tàn",
            "Yasuo cơn gió cuồng ma", "Alpha kẻ kí sinh", "Xiao nghiệp chướng"
        )
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        // --- MY CHAMPION SECTION ---
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = t("moba_select_champion_for_battle") ?: "Chọn Tướng Thi Đấu",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = ElegantGold
                )
                Box(
                    modifier = Modifier
                        .background(Color(0xFF1E293B), RoundedCornerShape(4.dp))
                        .border(0.5.dp, ElegantGold.copy(alpha = 0.8f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "👥 Tổng: ${playableHeroes.size} tướng",
                        fontSize = 9.sp,
                        color = ElegantGold,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    playableHeroes.forEach { (heroName, icon, role) ->
                        val isSelected = mobaHero == heroName
                        val isUnlocked = heroName != "Maloch" || mobaUnlockedHeroes.contains("Maloch")
                        
                        Column(
                            modifier = Modifier
                                .width(60.dp)
                                .clickable(enabled = isUnlocked) { onSelectHero(heroName) }
                                .padding(vertical = 2.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            if (isSelected) getHeroColor(heroName).copy(alpha = 0.2f)
                                            else Color(0xFF0F172A),
                                            CircleShape
                                        )
                                        .border(
                                            width = if (isSelected) 2.dp else 1.dp,
                                            color = if (isSelected) getHeroColor(heroName) else Color.DarkGray.copy(alpha = 0.6f),
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(if (isUnlocked) icon else "🔒", fontSize = 18.sp)
                                }
                                
                                // Tiny top-right 'i' button for details
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .offset(x = 2.dp, y = (-2).dp)
                                        .size(14.dp)
                                        .background(Color(0xFF1E293B), CircleShape)
                                        .border(1.dp, Color.Gray.copy(alpha = 0.5f), CircleShape)
                                        .clickable {
                                            detailedHeroName = heroName
                                            isViewDetailsForEnemy = false
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "i",
                                        color = Color.LightGray,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            
                            Text(
                                text = heroName,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) getHeroColor(heroName) else if (isUnlocked) Color.White else Color.Gray,
                                maxLines = 1,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                // Left scroll hint arrow
                Text(
                    text = "◀",
                    color = ElegantGold.copy(alpha = 0.6f),
                    fontSize = 11.sp,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 2.dp)
                )

                // Right scroll hint arrow
                Text(
                    text = "▶",
                    color = ElegantGold.copy(alpha = 0.6f),
                    fontSize = 11.sp,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 2.dp)
                )
            }
        }

        // --- ENEMY SECTION ---
        val hasBossChallenge = mobaWinsForBoss >= 4
        
        if (hasBossChallenge) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF450A0A)
                ),
                border = BorderStroke(1.dp, Color.Red)
            ) {
                Column(
                    modifier = Modifier.padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = t("moba_boss_warning") ?: "CẢNH BÁO BOSS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Red
                    )
                    Text(
                        text = t("moba_boss_mode_desc") ?: "Boss hắc ám đang đợi bạn khiêu chiến!",
                        fontSize = 9.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .background(Color.DarkGray, RoundedCornerShape(2.dp))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight()
                                .background(Color.Red, RoundedCornerShape(2.dp))
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Button(
                        onClick = onResetBossProgress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(28.dp)
                            .testTag("reset_boss_progress_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.DarkGray.copy(alpha = 0.6f),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(4.dp),
                        contentPadding = PaddingValues(vertical = 2.dp)
                    ) {
                        Text(
                            text = t("moba_reset_boss") ?: "Đặt lại Boss",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // Always show normal enemy selector!
        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = t("moba_select_enemy_for_battle") ?: "Chọn Đối Thủ Quyết Đấu",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ElegantGold
                    )
                    Box(
                        modifier = Modifier
                            .background(Color(0xFF450A0A).copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                            .border(0.5.dp, Color.Red.copy(alpha = 0.8f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "💀 Tổng: ${enemiesList.size} đối thủ",
                            fontSize = 9.sp,
                            color = Color.Red,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Text(
                    text = t("moba_wins_towards_boss").format(mobaWinsForBoss),
                    fontSize = 9.sp,
                    color = Color.LightGray
                )
            }

            // High-tech Selector Tab Bar for Enemies
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0F172A), RoundedCornerShape(8.dp))
                    .border(1.dp, Color.DarkGray.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                    .padding(2.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf("Thường", "Boss Cường Hóa 🔥").forEachIndexed { index, title ->
                    val isTabSelected = enemyTab == index
                    val tabColor = if (index == 1) Color(0xFFEF4444) else ElegantGold
                    
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(30.dp)
                            .background(
                                if (isTabSelected) tabColor.copy(alpha = 0.15f) else Color.Transparent,
                                RoundedCornerShape(6.dp)
                            )
                            .clickable { enemyTab = index }
                            .padding(vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = title,
                            fontSize = 11.sp,
                            fontWeight = if (isTabSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isTabSelected) tabColor else Color.Gray
                        )
                    }
                }
            }

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    filteredEnemies.forEach { enemyName ->
                        val isEnemySelected = mobaSelectedEnemy == enemyName
                        val color = getHeroColor(enemyName)
                        val icon = getHeroIcon(enemyName)
                        
                        val displayName = when (enemyName) {
                            "Tulen hắc pháp sư" -> "Tulen Hắc"
                            "Valhein ma cà rồng" -> "Valhein MCR"
                            "Murad hoàng tử suy tàn" -> "Murad HTS"
                            "Yasuo cơn gió cuồng ma" -> "Yasuo CG"
                            "Alpha kẻ kí sinh" -> "Alpha KS"
                            "Xiao nghiệp chướng" -> "Xiao NC"
                            else -> enemyName
                        }
                        
                        val isEnhanced = enemyName.length > 7

                        Column(
                            modifier = Modifier
                                .width(60.dp)
                                .clickable { onSelectEnemy(enemyName) }
                                .padding(vertical = 2.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            if (isEnemySelected) color.copy(alpha = 0.2f)
                                            else Color(0xFF0F172A),
                                            CircleShape
                                        )
                                        .border(
                                            width = if (isEnemySelected) 2.dp else 1.dp,
                                            color = if (isEnemySelected) color else Color.DarkGray.copy(alpha = 0.6f),
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(icon, fontSize = 18.sp)
                                }
                                
                                // Tiny top-right 'i' button for details
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .offset(x = 2.dp, y = (-2).dp)
                                        .size(14.dp)
                                        .background(Color(0xFF1E293B), CircleShape)
                                        .border(1.dp, Color.Gray.copy(alpha = 0.5f), CircleShape)
                                        .clickable {
                                            detailedHeroName = enemyName
                                            isViewDetailsForEnemy = true
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "i",
                                        color = Color.LightGray,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            
                            Text(
                                text = displayName,
                                fontSize = 10.sp,
                                fontWeight = if (isEnemySelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isEnemySelected) color else Color.White,
                                maxLines = 1,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                // Left scroll hint arrow
                Text(
                    text = "◀",
                    color = Color.Red.copy(alpha = 0.6f),
                    fontSize = 11.sp,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 2.dp)
                )

                // Right scroll hint arrow
                Text(
                    text = "▶",
                    color = Color.Red.copy(alpha = 0.6f),
                    fontSize = 11.sp,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 2.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        // Play Button
        Button(
            onClick = onStartGame,
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .testTag("start_moba_game_button"),
            colors = ButtonDefaults.buttonColors(
                containerColor = getHeroColor(mobaHero),
                contentColor = if (getHeroColor(mobaHero) == Color(0xFF06B6D4) || getHeroColor(mobaHero) == Color(0xFF38BDF8) || getHeroColor(mobaHero) == Color(0xFF22D3EE)) Color.Black else Color.White
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = t("moba_launch_combat").format(mobaHero),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun MobaBaseView(hp: Float, maxHp: Float, isEnemy: Boolean, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.width(36.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        // Castle Name Tag
        Text(
            text = if (isEnemy) t("moba_enemy_castle") else t("moba_my_castle"),
            color = if (isEnemy) Color(0xFFFCA5A5) else Color(0xFF7DD3FC),
            fontSize = 6.5.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
        // HP bar
        val progress = (hp / maxHp).coerceIn(0f, 1f)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .background(Color.DarkGray)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progress)
                    .background(if (isEnemy) Color.Red else Color(0xFF10B981))
            )
        }

        Box(
            modifier = Modifier
                .size(26.dp)
                .background(
                    if (isEnemy) Color(0xFF7F1D1D) else Color(0xFF1E3A8A),
                    RoundedCornerShape(4.dp)
                )
                .border(2.dp, if (isEnemy) Color.Red else Color(0xFF38BDF8), RoundedCornerShape(4.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "🏰",
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun MobaTurretView(hp: Float, maxHp: Float, isEnemy: Boolean, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.width(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        // HP mini progress bar
        val progress = (hp / maxHp).coerceIn(0f, 1f)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .background(Color.DarkGray)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progress)
                    .background(if (isEnemy) Color.Red else Color(0xFF10B981))
            )
        }

        Box(
            modifier = Modifier
                .size(26.dp)
                .background(
                    if (isEnemy) Color(0xFF991B1B).copy(alpha = 0.8f) else Color(0xFF075985).copy(alpha = 0.8f),
                    CircleShape
                )
                .border(1.5.dp, if (isEnemy) Color.Red else Color(0xFF38BDF8), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "🛡️", fontSize = 11.sp)
        }
    }
}

@Composable
fun MobaCreepView(creep: MobaCreep, modifier: Modifier = Modifier) {
    val isEnhanced = creep.maxHp > 900f
    val size = if (isEnhanced) 20.dp else 13.dp
    val emoji = if (isEnhanced) {
        if (creep.isEnemy) "👹" else "🦁"
    } else {
        if (creep.isEnemy) "⚔️" else "🛡️"
    }
    val width = if (isEnhanced) 28.dp else 18.dp
    val hpHeight = if (isEnhanced) 3.5.dp else 2.dp
    val progress = (creep.hp / creep.maxHp).coerceIn(0f, 1f)

    Column(
        modifier = modifier.width(width),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(1.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(hpHeight)
                .background(Color.DarkGray)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progress)
                    .background(if (creep.isEnemy) Color.Red else Color(0xFF10B981))
            )
        }

        Box(
            modifier = Modifier
                .size(size)
                .background(if (creep.isEnemy) Color(0xFFEF4444) else Color(0xFF3B82F6), CircleShape)
                .then(
                    if (isEnhanced) {
                        Modifier.border(1.5.dp, Color(0xFFFFD700), CircleShape)
                    } else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = emoji,
                fontSize = if (isEnhanced) 11.sp else 7.sp,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Composable
fun MobaChampionView(
    name: String,
    isEnemy: Boolean,
    hp: Float,
    maxHp: Float,
    mp: Float = 0f,
    maxMp: Float = 0f,
    modifier: Modifier = Modifier,
    color: Color,
    hasGlow: Boolean = false,
    shield: Float = 0f,
    isEnchanted: Boolean = false,
    isXiaoMaskActive: Boolean = false
) {
    Column(
        modifier = modifier.width(42.dp), // slightly wider for better accessibility and text layout
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        // HP / Shield Bar
        val hpProgress = (hp / maxHp).coerceIn(0f, 1f)
        val shieldProgress = (shield / maxHp).coerceIn(0f, 1f)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .background(Color.DarkGray, RoundedCornerShape(1.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(hpProgress)
                    .background(if (isEnemy) Color.Red else Color(0xFF10B981))
            )
            if (shield > 0f) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth((hpProgress + shieldProgress).coerceAtMost(1f))
                        .background(Color(0xFF38BDF8).copy(alpha = 0.85f))
                )
            }
        }

        // Optional MP Bar
        if (!isEnemy && maxMp > 0) {
            val mpProgress = (mp / maxMp).coerceIn(0f, 1f)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.5.dp)
                    .background(Color.DarkGray, RoundedCornerShape(1.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(mpProgress)
                        .background(Color(0xFF3B82F6))
                )
            }
        }

        // Avatar
        val borderColor = if (isXiaoMaskActive) {
            Color(0xFF10B981) // Glowing emerald aura
        } else if (isEnchanted) {
            Color(0xFFEF4444) // Enchanted sword red aura
        } else if (hasGlow) {
            Color(0xFF38BDF8) // Lightning/passive cyan aura
        } else {
            if (isEnemy) Color(0xFF991B1B) else Color(0xFF166534)
        }
        val borderWidth = if (isXiaoMaskActive || isEnchanted || hasGlow) 2.5.dp else 1.5.dp

        Box(
            modifier = Modifier
                .size(30.dp)
                .background(color, CircleShape)
                .border(
                    width = borderWidth,
                    color = borderColor,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            val avatarEmoji = if (isXiaoMaskActive) {
                "👺" // Yaksha mask
            } else {
                when {
                    name.contains("Tulen") -> "⚡"
                    name.contains("Yasuo") -> "⚔️"
                    name.contains("Murad") -> "🗡️"
                    name.contains("Alpha") -> "🤖"
                    name.contains("Xiao") -> "🟢"
                    name.contains("Valhein") -> "🏹"
                    name.contains("Maloch") -> "👿"
                    isEnemy -> "👿"
                    else -> "🏹"
                }
            }
            Text(
                text = avatarEmoji,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        val displayName = if (isXiaoMaskActive) "👺 DẠ XOA" else if (isEnchanted) "🔥 $name 🔥" else name
        Text(
            text = displayName,
            fontSize = 7.sp,
            color = if (isXiaoMaskActive) Color(0xFF6EE7B7) else if (isEnchanted) Color(0xFFFCA5A5) else Color.White,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
    }
}

@Composable
fun MobaDiagnosticView(
    report: MobaDiagnostic,
    onRestart: () -> Unit,
    onBack: () -> Unit,
    viewModel: MainViewModel
) {
    val isWin = report.turretStatus.contains("Chiến Thắng") || report.turretStatus.contains("Victory")

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        // Outcome text
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    if (isWin) Color(0xFF064E3B).copy(alpha = 0.5f) else Color(0xFF4C0519).copy(alpha = 0.5f),
                    RoundedCornerShape(8.dp)
                )
                .border(1.dp, if (isWin) Color(0xFF059669) else Color(0xFFBE123C), RoundedCornerShape(8.dp))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = if (isWin) "VICTORY 🎉" else "DEFEAT 💀",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isWin) Color(0xFF34D399) else Color(0xFFF87171)
                )
                Text(
                    text = getLocalizedText(report.turretStatus),
                    fontSize = 12.sp,
                    color = Color.LightGray,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Quick Stats
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            border = BorderStroke(1.dp, CardSpaceBorder)
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = t("moba_combat_statistics"),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = ElegantGold
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text(text = t("moba_kills_deaths"), fontSize = 10.sp, color = Color.Gray)
                        Text(text = "⚔️ ${report.kills} / ${report.deaths}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Column {
                        Text(text = t("moba_accuracy_rate"), fontSize = 10.sp, color = Color.Gray)
                        Text(text = "🎯 ${report.accuracy}%", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Column {
                        Text(text = t("moba_interrupted_casts"), fontSize = 10.sp, color = Color.Gray)
                        Text(
                            text = t("moba_interrupted_times").format(report.skillsInterruptedCount),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (report.skillsInterruptedCount > 0) Color(0xFFF87171) else Color.White
                        )
                    }
                }
            }
        }

        // Linh Chi evaluation
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B).copy(alpha = 0.5f)),
            border = BorderStroke(1.dp, Color(0xFFEC4899).copy(alpha = 0.3f))
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.Top
            ) {
                Text(text = "🌸", fontSize = 24.sp)
                Column {
                    Text(text = t("moba_linh_chi_evaluation"), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFEC4899))
                    Text(
                        text = getLocalizedText(report.linhChiEvaluation),
                        fontSize = 11.sp,
                        color = Color.LightGray,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // Match Medals / Huy hiệu Trận đấu
        val matchMedals = remember(report) {
            val list = mutableListOf<Triple<String, String, Color>>()
            val isWin = report.turretStatus.contains("Chiến Thắng") || report.turretStatus.contains("Victory")
            if (isWin) {
                list.add(Triple(t("medal_victory_perfect"), t("medal_victory_perfect_desc"), Color(0xFF34D399)))
                if (report.kills >= 5) {
                    list.add(Triple(t("medal_warrior_clash"), t("medal_warrior_clash_desc"), Color(0xFFFFD700)))
                } else {
                    list.add(Triple(t("medal_conqueror"), t("medal_conqueror_desc"), Color(0xFF60A5FA)))
                }
                if (report.skillsInterruptedCount == 0) {
                    list.add(Triple(t("medal_perfect_mage"), t("medal_perfect_mage_desc"), Color(0xFFA78BFA)))
                }
            } else {
                list.add(Triple(t("medal_defeat_regret"), t("medal_defeat_regret_desc"), Color(0xFFF87171)))
                if (report.skillsInterruptedCount > 2) {
                    list.add(Triple(t("medal_red_ping_victim"), t("medal_red_ping_victim_desc"), Color(0xFFF59E0B)))
                }
                list.add(Triple(t("medal_persistence"), t("medal_persistence_desc"), Color(0xFF94A3B8)))
            }
            list
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            matchMedals.forEach { (name, desc, color) ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(color.copy(alpha = 0.12f))
                        .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Column {
                        Text(text = name, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = color)
                        Text(text = desc, fontSize = 7.5.sp, color = Color.LightGray)
                    }
                }
            }
        }

        // Diagnostics
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            border = BorderStroke(1.dp, CardSpaceBorder)
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = t("moba_lag_diagnostics"),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = ElegantGold
                )

                Text(
                    text = "• " + t("moba_main_cause") + getLocalizedText(report.mainIssue),
                    fontSize = 11.sp,
                    color = Color.LightGray
                )

                if (report.detailedTips.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = t("moba_recommended_solutions"),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    report.detailedTips.forEach { tip ->
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(text = "✔", color = NeonCyan, fontSize = 11.sp)
                            Column {
                                Text(text = getLocalizedText(tip.first), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                                Text(text = getLocalizedText(tip.second), fontSize = 10.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            }
        }

        // Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onRestart,
                modifier = Modifier.weight(1f).height(42.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(t("moba_rematch_revenge"), fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }

            Button(
                onClick = onBack,
                modifier = Modifier.weight(1f).height(42.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray, contentColor = Color.White),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(t("moba_select_another_hero"), fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}

data class MobaHeroDetails(
    val name: String,
    val title: String,
    val icon: String,
    val color: Color,
    val passiveName: String,
    val passiveDesc: String,
    val s1Name: String,
    val s1Desc: String,
    val s2Name: String,
    val s2Desc: String,
    val ultName: String,
    val ultDesc: String
)

@Composable
fun MobaHeroDetailsDialog(
    heroName: String,
    isEnemy: Boolean,
    isMalochUnlocked: Boolean,
    onSelectHero: (String) -> Unit,
    onSelectEnemy: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val details = when (heroName) {
        "Tulen" -> MobaHeroDetails(
            name = "Tulen",
            title = getLocalizedText("Lôi Điện Pháp Sư"),
            icon = "⚡",
            color = Color(0xFF06B6D4),
            passiveName = getLocalizedText("Nội Tại: Lôi Điện"),
            passiveDesc = getLocalizedText("Khi tung chiêu trúng địch tích lũy dấu ấn. Đạt 5 tầng sẽ kích hoạt vòng sét tự động oanh tạc mục tiêu lân cận."),
            s1Name = getLocalizedText("Chiêu 1: Lôi Quang"),
            s1Desc = getLocalizedText("⚡ Sát thương phép lan tỏa hình quạt mạnh mẽ."),
            s2Name = getLocalizedText("Chiêu 2: Lôi Động"),
            s2Desc = getLocalizedText("✨ Biến ảnh dịch chuyển tức thời chớp nhoáng né chiêu hoặc truy kích."),
            ultName = getLocalizedText("Ult: Lôi Điểu"),
            ultDesc = getLocalizedText("🔥 Triệu hồi chú chim lôi điểu khổng lồ dồn sát thương kết liễu cực đau.")
        )
        "Tulen hắc pháp sư" -> MobaHeroDetails(
            name = "Tulen hắc pháp sư",
            title = getLocalizedText("Pháp Sư Hắc Ám"),
            icon = "⚡",
            color = Color(0xFF8B5CF6),
            passiveName = getLocalizedText("Nội Tại: Lôi Điện Hắc Ám"),
            passiveDesc = getLocalizedText("Tích lũy lôi ấn bóng tối. Đạt 5 tầng sẽ oanh tạc sấm sét bóng tối liên tục vào kẻ địch xung quanh."),
            s1Name = getLocalizedText("Chiêu 1: Lôi Quang Tối"),
            s1Desc = getLocalizedText("⚡ Bắn 3 tia điện đen hình quạt cực mạnh."),
            s2Name = getLocalizedText("Chiêu 2: Lôi Động Quỷ"),
            s2Desc = getLocalizedText("✨ Dịch chuyển hắc ám né chiêu và gây sát thương sấm sét tối."),
            ultName = getLocalizedText("Ult: Lôi Điểu Vong Hồn"),
            ultDesc = getLocalizedText("🔥 Phóng lôi điểu vong hồn hắc ám kết liễu tàn bạo.")
        )
        "Valhein" -> MobaHeroDetails(
            name = "Valhein",
            title = getLocalizedText("Xạ Thủ Ám Khí"),
            icon = "🏹",
            color = Color(0xFFF59E0B),
            passiveName = getLocalizedText("Nội Tại: Ám Khí"),
            passiveDesc = getLocalizedText("Mỗi đòn đánh thường thứ 3 đổi ngẫu nhiên thành phi tiêu đỏ (nổ lan) hoặc phi tiêu vàng (gây choáng)."),
            s1Name = getLocalizedText("Chiêu 1: Chuyến Săn"),
            s1Desc = getLocalizedText("🏹 Ném phi tiêu đỏ gây sát thương phép lan rộng."),
            s2Name = getLocalizedText("Chiêu 2: Lời Nguyền"),
            s2Desc = getLocalizedText("🌀 Ném phi tiêu vàng làm choáng mục tiêu chắc chắn."),
            ultName = getLocalizedText("Ult: Bão Đạn"),
            ultDesc = getLocalizedText("🔥 Xả cơn bão đạn gồm 6 viên đạn bạc gây sát thương phép khổng lồ tầm gần.")
        )
        "Valhein ma cà rồng" -> MobaHeroDetails(
            name = "Valhein ma cà rồng",
            title = getLocalizedText("Thợ Săn Huyết Tộc"),
            icon = "🏹",
            color = Color(0xFFEF4444),
            passiveName = getLocalizedText("Nội Tại: Ám Khí Huyết Tộc"),
            passiveDesc = getLocalizedText("Phi tiêu đỏ hồi máu cho bản thân, phi tiêu vàng gây choáng và tước giáp."),
            s1Name = getLocalizedText("Chiêu 1: Chuyến Săn Đêm"),
            s1Desc = getLocalizedText("🏹 Ném phi tiêu huyết sắc phát nổ diện rộng."),
            s2Name = getLocalizedText("Chiêu 2: Lời Nguyền Ác Quỷ"),
            s2Desc = getLocalizedText("🌀 Phi tiêu vàng làm choáng chắc chắn và hút máu."),
            ultName = getLocalizedText("Ult: Bão Đạn Huyết Sắc"),
            ultDesc = getLocalizedText("🔥 Xả cơn bão đạn huyết tộc tầm gần gây sát thương bùng nổ cực rát.")
        )
        "Murad" -> MobaHeroDetails(
            name = "Murad",
            title = getLocalizedText("Sát Thủ Lãng Khách"),
            icon = "⚔️",
            color = Color(0xFFF59E0B),
            passiveName = getLocalizedText("Nội Tại: Ảnh Hồn"),
            passiveDesc = getLocalizedText("Đánh thường đủ 4 lần liên tiếp giúp mở phong ấn chiêu cuối Ảo Ảnh Trảm trong 5 giây."),
            s1Name = getLocalizedText("Chiêu 1: Vô Ảnh Vực"),
            s1Desc = getLocalizedText("⚔️ Lướt hai lần gây sát thương vật lý và để lại bóng ảo ảnh, lần thứ 3 giật bóng trở lại vị trí cũ."),
            s2Name = getLocalizedText("Chiêu 2: Vô Ảnh Trận"),
            s2Desc = getLocalizedText("🛡️ Tạo vùng không gian làm chậm địch, bản thân miễn nhiễm sát thương khi kích hoạt."),
            ultName = getLocalizedText("Ult: Ảo Ảnh Trảm"),
            ultDesc = getLocalizedText("🔥 Giải phóng ảo ảnh chém liên tục 5 lần cực mạnh, bản thân không thể bị chọn làm mục tiêu.")
        )
        "Murad hoàng tử suy tàn" -> MobaHeroDetails(
            name = "Murad hoàng tử suy tàn",
            title = getLocalizedText("Kẻ Lãng Quên Thời Gian"),
            icon = "⚔️",
            color = Color(0xFFD97706),
            passiveName = getLocalizedText("Nội Tại: Ảnh Hồn Đọa Lạc"),
            passiveDesc = getLocalizedText("Chém thường đủ 4 lần mở phong ấn Ảo Ảnh Trảm hắc ám tước đoạt sinh lực."),
            s1Name = getLocalizedText("Chiêu 1: Tàn Ảnh Vô Hình"),
            s1Desc = getLocalizedText("⚔️ Lướt chém ảo ảnh 2 lần, lần thứ 3 biến ảo giật bóng về vị trí cũ."),
            s2Name = getLocalizedText("Chiêu 2: Vô Ảnh Trận Đọa"),
            s2Desc = getLocalizedText("🛡️ Dựng cấm trận làm chậm tột độ và tước hồn tạo lá chắn dày."),
            ultName = getLocalizedText("Ult: Ảo Ảnh Trảm Hắc Ám"),
            ultDesc = getLocalizedText("🔥 Giải phóng bóng tối chém liên tiếp 5 lần vô địch, càn quét diện rộng.")
        )
        "Yasuo" -> MobaHeroDetails(
            name = "Yasuo",
            title = getLocalizedText("Kiếm Sĩ Gió Phương Bắc"),
            icon = "🌪️",
            color = Color(0xFF38BDF8),
            passiveName = getLocalizedText("Nội Tại: Đạo Của Lãng Khách"),
            passiveDesc = getLocalizedText("Nhận khiên chắn khi di chuyển đủ khoảng cách. Đòn chí mạng được nhân đôi sát thương. Không dùng Mana."),
            s1Name = getLocalizedText("Chiêu 1: Bão Kiếm"),
            s1Desc = getLocalizedText("⚔️ Đâm kiếm tích lũy Bão Kiếm. Đạt 2 tầng phóng Lốc Xoáy hất tung mục tiêu."),
            s2Name = getLocalizedText("Chiêu 2: Tường Gió"),
            s2Desc = getLocalizedText("🌪️ Dựng tường gió ngăn chặn toàn bộ đạn và chiêu thức tầm xa của đối phương."),
            ultName = getLocalizedText("Ult: Trăn Trối"),
            ultDesc = getLocalizedText("⚡ Bay lên không trung chém liên hoàn kẻ địch bị hất tung, tăng mạnh sát thương chí mạng.")
        )
        "Yasuo cơn gió cuồng ma" -> MobaHeroDetails(
            name = "Yasuo cơn gió cuồng ma",
            title = getLocalizedText("Kiếm Khách Quỷ Phong"),
            icon = "🌪️",
            color = Color(0xFF475569),
            passiveName = getLocalizedText("Nội Tại: Đạo Của Quỷ Phong"),
            passiveDesc = getLocalizedText("Di chuyển tích lũy nộ phong kiếm nhận khiên ma cực dày khi đầy thanh."),
            s1Name = getLocalizedText("Chiêu 1: Bão Kiếm Ma"),
            s1Desc = getLocalizedText("⚔️ Đâm kiếm ma quái tích tụ phong lốc. Tầng 3 phóng Lốc Xoáy Cuồng Ma hất tung đối thủ cực cao."),
            s2Name = getLocalizedText("Chiêu 2: Tường Gió Quỷ"),
            s2Desc = getLocalizedText("🌪️ Dựng bức tường phong ma hóa giải mọi luồng đạn tấn công."),
            ultName = getLocalizedText("Ult: Trăn Trối Cuồng Ma"),
            ultDesc = getLocalizedText("⚡ Bay lên chém xé xác đối thủ bị hất tung tàn nhẫn, tăng cực mạnh chí mạng.")
        )
        "Alpha" -> MobaHeroDetails(
            name = "Alpha",
            title = getLocalizedText("Kẻ Đi Săn Tương Lai"),
            icon = "🤖",
            color = Color(0xFF22D3EE),
            passiveName = getLocalizedText("Nội Tại: Đánh Dấu Công Nghệ"),
            passiveDesc = getLocalizedText("Khi tung chiêu trúng mục tiêu sẽ đánh dấu cyber, giúp Drone Beta tự động phóng la-zer phụ kích gây sát thương chuẩn."),
            s1Name = getLocalizedText("Chiêu 1: Đao Quét Thăng Hoa"),
            s1Desc = getLocalizedText("🤖 Quét đao cơ khí gây sát thương mạnh mẽ và Drone Beta bắn phụ kích."),
            s2Name = getLocalizedText("Chiêu 2: Đao Quét Năng Lượng"),
            s2Desc = getLocalizedText("🛡️ Quét thương cyber hình tròn gây sát thương lớn và hồi HP mạnh mẽ."),
            ultName = getLocalizedText("Ult: Mũi Giáo Alpha"),
            ultDesc = getLocalizedText("🔥 Lao thẳng hất tung mục tiêu và Drone Beta xả siêu la-zer Orbital hủy diệt diện rộng.")
        )
        "Alpha kẻ kí sinh" -> MobaHeroDetails(
            name = "Alpha kẻ kí sinh",
            title = getLocalizedText("Sinh Vật Cyber Ký Sinh"),
            icon = "🤖",
            color = Color(0xFFEC4899),
            passiveName = getLocalizedText("Nội Tại: Ký Sinh Công Nghệ"),
            passiveDesc = getLocalizedText("Gây dấu ấn sinh học ký sinh, kích hoạt Beta laser phụ kích liên tục gây sát thương chuẩn rát buốt."),
            s1Name = getLocalizedText("Chiêu 1: Đao Quét Ký Sinh"),
            s1Desc = getLocalizedText("🤖 Quét đao cơ khí đột biến bám dính lấy mục tiêu."),
            s2Name = getLocalizedText("Chiêu 2: Lá Chắn Ký Sinh"),
            s2Desc = getLocalizedText("🛡️ Quét đao hình tròn hút HP cực mạnh mẽ để sinh trưởng lá chắn giáp dày."),
            ultName = getLocalizedText("Ult: Mũi Giáo Ký Sinh"),
            ultDesc = getLocalizedText("🔥 Lao thẳng hất tung giam giữ mục tiêu và Beta dội bão la-zer ký sinh hủy diệt.")
        )
        "Xiao" -> MobaHeroDetails(
            name = "Xiao",
            title = getLocalizedText("Hộ Pháp Dạ Xoa"),
            icon = "🟢",
            color = Color(0xFF10B981),
            passiveName = getLocalizedText("Nội Tại: Mặt Nạ Dạ Xoa"),
            passiveDesc = getLocalizedText("Tung chiêu giúp cường hóa sức mạnh, tăng khả năng cơ động lướt ảo diệu và nhảy đâm plunge chấn động."),
            s1Name = getLocalizedText("Chiêu 1: Vũ Điệu Chinh Phục"),
            s1Desc = getLocalizedText("🟢 Chém 3 đường Gió Xanh phong ấn, mỗi nhịp trúng đích hồi phục ngay 10% HP."),
            s2Name = getLocalizedText("Chiêu 2: Gió Tung Hoành"),
            s2Desc = getLocalizedText("💨 Lướt dạ xoa liên tiếp 2 lần cực kỳ cơ động để né tránh và đột kích."),
            ultName = getLocalizedText("Ult: Vũ Điệu Đại Thánh"),
            ultDesc = getLocalizedText("🔥 Nhảy cao lên không trung hóa Dạ Xoa gầm thét, đâm plunge 2 lần hất tung diện rộng.")
        )
        "Xiao nghiệp chướng" -> MobaHeroDetails(
            name = "Xiao nghiệp chướng",
            title = getLocalizedText("Dạ Xoa Sa Đọa"),
            icon = "🟢",
            color = Color(0xFF065F46),
            passiveName = getLocalizedText("Nội Tại: Mặt Nạ Nghiệp Chướng"),
            passiveDesc = getLocalizedText("Cường hóa sức mạnh dạ xoa khiêu chiến tăng tốc độ lướt lách và hất tung Plunge tàn khốc."),
            s1Name = getLocalizedText("Chiêu 1: Vũ Điệu Chinh Phục Hắc"),
            s1Desc = getLocalizedText("🟢 Chém gió độc 3 lần cực rộng, hút cạn sinh khí đối thủ hồi phục HP."),
            s2Name = getLocalizedText("Chiêu 2: Gió Độc Tung Hoành"),
            s2Desc = getLocalizedText("💨 Lướt chớp nhoáng liên tục 2 lần cực linh hoạt để tập kích mục tiêu."),
            ultName = getLocalizedText("Ult: Vũ Điệu Đại Thánh Hắc"),
            ultDesc = getLocalizedText("🔥 Đâm plunge chấn động 2 lần hất tung diện rộng dẫm nát trận địa kẻ thù.")
        )
        else -> MobaHeroDetails(
            name = "Maloch",
            title = getLocalizedText("Ma Vương Hủy Diệt"),
            icon = "👿",
            color = Color(0xFFDC2626),
            passiveName = getLocalizedText("Nội Tại: Ma Vương"),
            passiveDesc = getLocalizedText("Quỷ Kiếm trúng tướng địch giúp cường hóa gươm, các đòn đánh kế tiếp gây sát thương chuẩn và hồi phục máu."),
            s1Name = getLocalizedText("Chiêu 1: Quỷ Kiếm"),
            s1Desc = getLocalizedText("⚔️ Vung đao chém quét hình bán nguyệt gây sát thương chuẩn khổng lồ."),
            s2Name = getLocalizedText("Chiêu 2: Đoạt Hồn"),
            s2Desc = getLocalizedText("🛡️ Đoạt hồn phách kẻ địch lân cận tạo thành lớp lá chắn giáp cực dày hấp thụ mọi sát thương."),
            ultName = getLocalizedText("Ult: Luyện Ngục"),
            ultDesc = getLocalizedText("👿 Nhảy đáp hất tung diện rộng dồn sát thương khủng khiếp và làm chậm trận địa.")
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = true)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(16.dp)
                .border(2.dp, details.color, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF0F172A)
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(details.color.copy(alpha = 0.2f), CircleShape)
                            .border(2.dp, details.color, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(details.icon, fontSize = 32.sp)
                    }
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = details.name,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = details.title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = details.color
                        )
                    }
                }

                // Divider replacement
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color.DarkGray)
                )

                // Skills List
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 280.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Passive
                    SkillItem(index = 0, name = details.passiveName, desc = details.passiveDesc, color = details.color)
                    // Skill 1
                    SkillItem(index = 1, name = details.s1Name, desc = details.s1Desc, color = details.color)
                    // Skill 2
                    SkillItem(index = 2, name = details.s2Name, desc = details.s2Desc, color = details.color)
                    // Ult
                    SkillItem(index = 3, name = details.ultName, desc = details.ultDesc, color = details.color)
                }

                // Divider replacement
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color.DarkGray)
                )

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Button Select as Hero
                    val isHeroSelectable = details.name != "Maloch" || isMalochUnlocked
                    Button(
                        onClick = { onSelectHero(details.name) },
                        enabled = isHeroSelectable,
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .testTag("select_hero_confirm_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = details.color,
                            disabledContainerColor = Color.Gray.copy(alpha = 0.3f)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (isHeroSelectable) t("moba_select_hero") else t("moba_locked"),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isHeroSelectable) Color.White else Color.LightGray
                        )
                    }

                    // Button Select as Enemy
                    Button(
                        onClick = { onSelectEnemy(details.name) },
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .testTag("select_enemy_confirm_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.DarkGray
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        val selEnemyText = t("moba_select_enemy_for_battle").replace(":", "")
                        Text(
                            text = if (selEnemyText.length > 15) "Chọn Địch" else selEnemyText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                // Close Button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(38.dp)
                        .testTag("close_hero_details_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    border = BorderStroke(1.dp, Color.Gray),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    Text(
                        text = t("close") ?: "Close",
                        fontSize = 12.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}

data class SkillTips(
    val difficulty: String,
    val cooldown: String,
    val tip: String
)

fun getStatsAndTipsForSkill(name: String): SkillTips {
    return when {
        name.contains("Nội Tại") || name.contains("Passive") -> {
            SkillTips(
                difficulty = "★★☆☆☆",
                cooldown = "Không tốn hồi chiêu / Thụ động",
                tip = "Kích hoạt tự động khi đạt đủ số đòn đánh hoặc tầng dấu ấn kỹ năng. Tận dụng để tối ưu hóa sát thương đột biến liên tục."
            )
        }
        name.contains("Chiêu 1") -> {
            SkillTips(
                difficulty = "★★★☆☆",
                cooldown = "4.5s - 6s / Sát thương chính",
                tip = "Kỹ năng dọn lính và cấu rỉa chủ đạo. Sử dụng liên tục để quấy rối kẻ địch từ khoảng cách an toàn và tích lũy nộ phong/lôi ấn."
            )
        }
        name.contains("Chiêu 2") -> {
            SkillTips(
                difficulty = "★★★★☆",
                cooldown = "8.0s - 12.0s / Cơ động & Khống chế",
                tip = "Tạo bất ngờ bằng cách lướt đột kích, dịch chuyển tức thời hoặc tạo tường gió hóa giải đòn đánh nguy hiểm để outplay đối thủ."
            )
        }
        name.contains("Ult") || name.contains("Chiêu cuối") -> {
            SkillTips(
                difficulty = "★★★★★",
                cooldown = "30s - 45s / Sát thương hủy diệt",
                tip = "Dồn sát thương dứt điểm tàn bạo lên các mục tiêu thấp máu hoặc phối hợp khống chế diện rộng cùng đồng đội để lật kèo."
            )
        }
        else -> {
            SkillTips(
                difficulty = "★★★☆☆",
                cooldown = "Thời gian hồi trung bình",
                tip = "Nghiên cứu kỹ bộ kỹ năng độc quyền của vị tướng này để tạo ra chuỗi combo kết liễu trơn tru nhất."
            )
        }
    }
}

@Composable
fun SkillItem(index: Int, name: String, desc: String, color: Color) {
    var isExpanded by remember { mutableStateOf(false) }
    
    // Staggered layout animation sequence (similar to Framer Motion stagger)
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(index * 110L) // Beautiful staggered delay for each list item
        isVisible = true
    }
    
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 400, easing = LinearOutSlowInEasing),
        label = "alpha"
    )
    
    val translationY by animateFloatAsState(
        targetValue = if (isVisible) 0f else 35f, // Smooth slide up from 35dp
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "translationY"
    )

    val rotationAngle by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy),
        label = "rotation"
    )
    
    val borderColor by animateColorAsState(
        targetValue = if (isExpanded) color.copy(alpha = 0.5f) else Color.Transparent,
        animationSpec = tween(300),
        label = "borderColor"
    )
    
    val cardBgColor by animateColorAsState(
        targetValue = if (isExpanded) color.copy(alpha = 0.08f) else Color.White.copy(alpha = 0.03f),
        animationSpec = tween(300),
        label = "bgColor"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer(
                alpha = alpha,
                translationY = translationY
            )
            .background(cardBgColor, RoundedCornerShape(8.dp))
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .clickable { isExpanded = !isExpanded }
            .padding(10.dp)
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            }
            
            // Rotating Chevron Indicator
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Expand Skill Details",
                tint = color.copy(alpha = 0.7f),
                modifier = Modifier
                    .size(18.dp)
                    .graphicsLayer(rotationZ = rotationAngle)
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = desc,
            fontSize = 11.sp,
            color = Color.LightGray
        )
        
        if (isExpanded) {
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(color.copy(alpha = 0.2f))
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            val tips = getStatsAndTipsForSkill(name)
            
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "⭐ Đánh giá độ khó:",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                    Text(
                        text = tips.difficulty,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = color
                    )
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "⏱️ Cooldown & Loại Chiêu:",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                    Text(
                        text = tips.cooldown,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.LightGray
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "💡 Mẹo & Chiến thuật combo:",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
                
                Text(
                    text = tips.tip,
                    fontSize = 10.sp,
                    color = Color.LightGray,
                    lineHeight = 13.sp
                )
            }
        }
    }
}
