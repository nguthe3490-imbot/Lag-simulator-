package com.example.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun NetworkTestScreen(viewModel: MainViewModel) {
    val testState by viewModel.networkTestState.collectAsState()
    val progress by viewModel.currentTestProgress.collectAsState()
    val currentSpeed by viewModel.currentTestSpeed.collectAsState()
    val currentPing by viewModel.currentTestPing.collectAsState()
    val currentJitter by viewModel.currentTestJitter.collectAsState()

    val finalPing by viewModel.finalPing.collectAsState()
    val finalJitter by viewModel.finalJitter.collectAsState()
    val finalDownloadSpeed by viewModel.finalDownloadSpeed.collectAsState()
    val finalUploadSpeed by viewModel.finalUploadSpeed.collectAsState()
    val analyzerReport by viewModel.analyzerReport.collectAsState()

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ElegantBackground)
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App Header Info
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = ElegantCardBackground),
            border = BorderStroke(1.dp, ElegantCardBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Speed,
                    contentDescription = "Network Diagnostic Icon",
                    tint = NeonCyan,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Phân Tích Đường Truyền",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Đo lường độ trễ ping, jitter, tốc độ tải và phân tích tối ưu liên hệ mạng.",
                        color = ElegantTextSecondary,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Main Speedometer Analyzer Widget
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = ElegantCardBackground),
            border = BorderStroke(1.dp, ElegantCardBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp, horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (testState == "IDLE") {
                    Text(
                        text = "Hệ thống đã sẵn sàng",
                        color = ElegantTextSecondary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Big Start Button inside circular layout
                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .clip(CircleShape)
                            .background(Brush.radialGradient(listOf(ElegantAccent.copy(alpha = 0.4f), Color.Transparent)))
                            .border(2.dp, NeonCyan.copy(alpha = 0.5f), CircleShape)
                            .testTag("test_network_start_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Button(
                            onClick = { viewModel.startNetworkTest() },
                            modifier = Modifier.size(150.dp),
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(containerColor = ElegantAccent)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Start",
                                    tint = NeonCyan,
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "BẮT ĐẦU ĐO",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Nhấp để đo thông số mạng thực tế",
                        color = ElegantTextSecondary,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )

                } else if (testState == "COMPLETED") {
                    // Completed status presentation
                    Text(
                        text = "Phân tích hoàn tất!",
                        color = NeonCyan,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Displays metrics details
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        MetricCard(
                            title = "Ping",
                            value = "${finalPing ?: 0} ms",
                            icon = Icons.Default.CompareArrows,
                            color = if ((finalPing ?: 0) < 30) Color(0xFF10B981) else if ((finalPing ?: 0) < 70) Color(0xFFF59E0B) else Color(0xFFEF4444)
                        )
                        MetricCard(
                            title = "Jitter",
                            value = "${finalJitter ?: 0} ms",
                            icon = Icons.Default.WifiTethering,
                            color = if ((finalJitter ?: 0) < 5) Color(0xFF10B981) else if ((finalJitter ?: 0) < 12) Color(0xFFF59E0B) else Color(0xFFEF4444)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        MetricCard(
                            title = "Tải xuống",
                            value = "${finalDownloadSpeed ?: 0.0} Mbps",
                            icon = Icons.Default.ArrowDownward,
                            color = if ((finalDownloadSpeed ?: 0.0) >= 35.0) Color(0xFF10B981) else if ((finalDownloadSpeed ?: 0.0) >= 15.0) Color(0xFF3B82F6) else Color(0xFFF59E0B)
                        )
                        MetricCard(
                            title = "Tải lên",
                            value = "${finalUploadSpeed ?: 0.0} Mbps",
                            icon = Icons.Default.ArrowUpward,
                            color = if ((finalUploadSpeed ?: 0.0) >= 15.0) Color(0xFF10B981) else if ((finalUploadSpeed ?: 0.0) >= 8.0) Color(0xFF3B82F6) else Color(0xFFF59E0B)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { viewModel.startNetworkTest() },
                        colors = ButtonDefaults.buttonColors(containerColor = ElegantAccent),
                        modifier = Modifier.fillMaxWidth(0.7f)
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Test Again")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Đo lại kết nối", fontWeight = FontWeight.Bold)
                    }

                } else {
                    // Active testing mode: testing ping, download or upload
                    val activeLabel = when (testState) {
                        "TESTING_PING" -> "Đang đo độ trễ & độ ổn định..."
                        "TESTING_DOWNLOAD" -> "Đang kiểm tra tốc độ tải xuống..."
                        "TESTING_UPLOAD" -> "Đang kiểm tra tốc độ tải lên..."
                        else -> "Đang tiến hành phân tích..."
                    }

                    Text(
                        text = activeLabel,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Spinning / pulsing indicator dial using Canvas
                    Box(
                        modifier = Modifier.size(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // We animate the needle / background arc
                        val infiniteTransition = rememberInfiniteTransition(label = "")
                        val pulseScale by infiniteTransition.animateFloat(
                            initialValue = 0.95f,
                            targetValue = 1.05f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(800, easing = LinearEasing),
                                repeatMode = RepeatMode.Reverse
                            ), label = ""
                        )

                        Canvas(modifier = Modifier.size(180.dp)) {
                            // Circular background track
                            drawArc(
                                color = Color.Gray.copy(alpha = 0.2f),
                                startAngle = 135f,
                                sweepAngle = 270f,
                                useCenter = false,
                                style = Stroke(width = 12f, cap = StrokeCap.Round)
                            )

                            // Dynamic active progress arc
                            val activeSweep = 270f * progress
                            val arcColor = when (testState) {
                                "TESTING_PING" -> NeonPink
                                "TESTING_DOWNLOAD" -> NeonCyan
                                else -> ElegantGold
                            }
                            drawArc(
                                color = arcColor,
                                startAngle = 135f,
                                sweepAngle = activeSweep,
                                useCenter = false,
                                style = Stroke(width = 12f, cap = StrokeCap.Round)
                            )
                        }

                        // Text readouts inside the dial
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = when (testState) {
                                    "TESTING_PING" -> Icons.Default.CompareArrows
                                    "TESTING_DOWNLOAD" -> Icons.Default.ArrowDownward
                                    else -> Icons.Default.ArrowUpward
                                },
                                contentDescription = "Active Speed icon",
                                tint = when (testState) {
                                    "TESTING_PING" -> NeonPink
                                    "TESTING_DOWNLOAD" -> NeonCyan
                                    else -> ElegantGold
                                },
                                modifier = Modifier
                                    .size(36.dp)
                                    .rotate(if (testState == "TESTING_DOWNLOAD" || testState == "TESTING_UPLOAD") pulseScale * 15f else 0f)
                            )
                            Spacer(modifier = Modifier.height(4.dp))

                            if (testState == "TESTING_PING") {
                                Text(
                                    text = "${currentPing} ms",
                                    color = Color.White,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Jitter: ${currentJitter}ms",
                                    color = ElegantTextSecondary,
                                    fontSize = 11.sp
                                )
                            } else {
                                Text(
                                    text = "${String.format("%.1f", currentSpeed)}",
                                    color = Color.White,
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Mbps",
                                    color = ElegantTextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Linear overall progress indicator
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth(0.85f)
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = NeonCyan,
                            trackColor = Color.Gray.copy(alpha = 0.2f)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Tiến độ: ${(progress * 100).toInt()}%",
                            color = ElegantTextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Comprehensive Connection Status Report Card
        analyzerReport?.let { report ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .testTag("test_network_report_card"),
                colors = CardDefaults.cardColors(containerColor = ElegantCardBackground),
                border = BorderStroke(1.5.dp, Color(report.statusColor))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // Header status
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "BÁO CÁO KẾT NỐI MẠNG",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = ElegantGold
                        )

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(report.statusColor).copy(alpha = 0.15f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = report.statusText,
                                color = Color(report.statusColor),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = report.description,
                        color = Color.White,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )

                    Divider(
                        modifier = Modifier.padding(vertical = 16.dp),
                        color = ElegantCardBorder
                    )

                    // Gaming Performance Subsection
                    EvaluationRow(
                        title = "Hiệu suất chơi game online",
                        description = report.gamingPerformance,
                        icon = Icons.Default.SportsEsports,
                        iconColor = NeonPink
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Streaming/Media Performance Subsection
                    EvaluationRow(
                        title = "Xem phim & truyền phát video",
                        description = report.streamingPerformance,
                        icon = Icons.Default.PlayCircle,
                        iconColor = NeonCyan
                    )

                    Divider(
                        modifier = Modifier.padding(vertical = 16.dp),
                        color = ElegantCardBorder
                    )

                    // Suggestions / Troubleshooting steps
                    Text(
                        text = "Đề xuất khắc phục & cải thiện mạng:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    report.suggestions.forEachIndexed { index, suggestion ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(top = 2.dp)
                                    .size(18.dp)
                                    .clip(CircleShape)
                                    .background(ElegantAccent),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${index + 1}",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = suggestion,
                                color = ElegantTextPrimary,
                                fontSize = 13.sp,
                                lineHeight = 18.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Ask Linh Chi Integration Button
                    Button(
                        onClick = {
                            val textToSend = "Linh Chi ơi, anh vừa đo kết nối mạng được Ping ${report.ping}ms, Jitter ${report.jitter}ms, Tải xuống ${report.downloadSpeed}Mbps, Tải lên ${report.uploadSpeed}Mbps. Đánh giá mạng của anh thuộc mức '${report.statusText}'. Hãy tư vấn dỗ dành anh và khuyên anh cách chơi game hay hơn đi!"
                            viewModel.setTab(2) // Switch to Linh Chi chat tab
                            viewModel.sendMessage(textToSend)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F378B)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.Forum, contentDescription = "Consult Assistant")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Tư vấn mạng với trợ lý Linh Chi", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color
) {
    Card(
        modifier = Modifier
            .width(155.dp)
            .height(80.dp),
        colors = CardDefaults.cardColors(containerColor = ElegantCardBackground.copy(alpha = 0.5f)),
        border = BorderStroke(1.dp, ElegantCardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    color = ElegantTextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = value,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun EvaluationRow(
    title: String,
    description: String,
    icon: ImageVector,
    iconColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier
                .size(24.dp)
                .padding(top = 2.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = title,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                color = ElegantTextSecondary,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
        }
    }
}
