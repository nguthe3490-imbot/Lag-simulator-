package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.CardSpaceBackground
import com.example.ui.theme.CardSpaceBorder
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.ElegantGold
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.geometry.Offset

@Composable
fun ChatScreen(viewModel: MainViewModel) {
    val messages by viewModel.chatMessages.collectAsState()
    val isTyping by viewModel.isTyping.collectAsState()
    val flirtingStyle by viewModel.flirtingStyle.collectAsState()
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    val showSecretGameSelector by viewModel.showSecretGameSelector.collectAsState()
    val activeSecretGame by viewModel.activeSecretGame.collectAsState()
    val secretGameStatus by viewModel.secretGameStatus.collectAsState()
    val playerScore by viewModel.playerScore.collectAsState()
    val linhChiScore by viewModel.linhChiScore.collectAsState()

    // Autoscroll to bottom when messages list size or typing state changes
    LaunchedEffect(messages.size, isTyping) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
        // --- Chat Header ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp),
            colors = CardDefaults.cardColors(containerColor = CardSpaceBackground),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardSpaceBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Profile Image
                    Box(modifier = Modifier.size(52.dp)) {
                        Image(
                            painter = painterResource(id = R.drawable.img_linh_chi_avatar),
                            contentDescription = "Linh Chi Avatar",
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .border(2.dp, NeonPink, CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        // Online indicator badge
                        val pulseTransition = rememberInfiniteTransition(label = "pulse")
                        val scale by pulseTransition.animateFloat(
                            initialValue = 0.8f,
                            targetValue = 1.2f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(600),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "online_dot"
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(14.dp * scale)
                                .clip(CircleShape)
                                .background(Color.Green)
                                .border(1.dp, CardSpaceBackground, CircleShape)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = getLocalizedText("Trợ Lý Linh Chi"),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "AI",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                modifier = Modifier
                                    .background(NeonPink, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                        Text(
                            text = getLocalizedText("Đang trực tuyến • Rất thích thả thính"),
                            fontSize = 11.sp,
                            color = NeonCyan,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                IconButton(
                    onClick = { viewModel.clearChat() },
                    modifier = Modifier.testTag("clear_chat_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = "Clear Chat",
                        tint = Color.LightGray
                    )
                }
            }
        }

        // --- Flirting Style Selector ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CardSpaceBackground)
                .border(androidx.compose.foundation.BorderStroke(1.dp, CardSpaceBorder))
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = getLocalizedText("Phong cách:"),
                color = Color.LightGray,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            val styles = listOf("Duyên dáng", "Hài hước", "Lãng mạn")
            styles.forEach { styleName ->
                val isSelected = styleName == flirtingStyle
                val chipBg = if (isSelected) {
                    Brush.linearGradient(colors = listOf(NeonPink.copy(alpha = 0.25f), NeonCyan.copy(alpha = 0.25f)))
                } else {
                    Brush.linearGradient(colors = listOf(Color(0xFF1B1A2F), Color(0xFF1B1A2F)))
                }
                val borderStrokeColor = if (isSelected) NeonPink else CardSpaceBorder

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(chipBg)
                        .border(1.dp, borderStrokeColor, RoundedCornerShape(16.dp))
                        .clickable { viewModel.setFlirtingStyle(styleName) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = getLocalizedText(styleName),
                        color = if (isSelected) Color.White else Color.LightGray,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        // --- Chat Bubble Area ---
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { Spacer(modifier = Modifier.height(12.dp)) }

            items(messages, key = { it.id }) { message ->
                ChatBubble(message = message)
            }

            if (isTyping) {
                item {
                    TypingIndicator()
                }
            }

            item { Spacer(modifier = Modifier.height(12.dp)) }
        }

        // --- Quick Suggestions (Gợi ý phím tắt) ---
        QuickSuggestions(onSuggestionClicked = { text ->
            viewModel.sendMessage(text)
        })

        // --- Input Send Area ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(CardSpaceBackground)
                .border(androidx.compose.foundation.BorderStroke(1.dp, CardSpaceBorder))
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text(text = getLocalizedText("Nhắn gì đó ngọt ngào cho Linh Chi..."), color = Color.Gray, fontSize = 14.sp) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_input_text_field"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color(0xFF0F0E1B),
                        unfocusedContainerColor = Color(0xFF0F0E1B),
                        focusedBorderColor = NeonPink,
                        unfocusedBorderColor = CardSpaceBorder
                    ),
                    shape = RoundedCornerShape(24.dp),
                    maxLines = 4
                )
                Spacer(modifier = Modifier.width(12.dp))
                IconButton(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            viewModel.sendMessage(inputText)
                            inputText = ""
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(NeonPink, NeonCyan)
                            )
                        )
                        .testTag("send_message_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send Message",
                        tint = Color.White
                    )
                }
            }
        }
    }

    // --- Overlays ---
    if (showSecretGameSelector) {
        SecretGameSelectorOverlay(viewModel)
    }

    if (activeSecretGame == "fps") {
        SecretFpsGameOverlay(viewModel, playerScore, linhChiScore, secretGameStatus)
    } else if (activeSecretGame == "moba") {
        SecretMobaGameOverlay(viewModel, playerScore, linhChiScore, secretGameStatus)
    }
}
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val isUser = message.sender == Sender.USER

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        if (!isUser) {
            Image(
                painter = painterResource(id = R.drawable.img_linh_chi_avatar),
                contentDescription = "Linh Chi Micro",
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .border(1.dp, NeonPink, CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .widthIn(max = 280.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isUser) 16.dp else 4.dp,
                            bottomEnd = if (isUser) 4.dp else 16.dp
                        )
                    )
                    .background(
                        if (isUser) {
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF007A99), Color(0xFF005566))
                            )
                        } else {
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF331525), Color(0xFF220A17))
                            )
                        }
                    )
                    .border(
                        1.dp,
                        if (isUser) NeonCyan.copy(alpha = 0.5f) else NeonPink.copy(alpha = 0.5f),
                        RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isUser) 16.dp else 4.dp,
                            bottomEnd = if (isUser) 4.dp else 16.dp
                        )
                    )
                    .padding(12.dp)
            ) {
                Text(
                    text = getLocalizedText(message.text),
                    color = Color.White,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }
            
            // Timestamp with cute styling
            val timeString = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(java.util.Date(message.timestamp))
            Text(
                text = if (isUser) "${getLocalizedText("Bạn")} • $timeString" else "Linh Chi • $timeString",
                fontSize = 10.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp, start = 4.dp, end = 4.dp)
            )
        }

        if (isUser) {
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1B1A2F))
                    .border(1.dp, NeonCyan, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "A",
                    color = NeonCyan,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun TypingIndicator() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.img_linh_chi_avatar),
            contentDescription = "Linh Chi Micro",
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .border(1.dp, NeonPink, CircleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(8.dp))
        
        Card(
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 4.dp, bottomEnd = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF220A17)),
            border = androidx.compose.foundation.BorderStroke(1.dp, NeonPink.copy(alpha = 0.3f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = getLocalizedText("Linh Chi đang gõ... 💕"),
                    color = NeonPink,
                    fontSize = 12.sp,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun QuickSuggestions(onSuggestionClicked: (String) -> Unit) {
    val suggestions = listOf(
        "DNS giảm lag?",
        "Mạng dây vs Wifi?",
        "Liên Minh bị lag?",
        "Tán tỉnh anh đi!",
        "Thả thính game thủ!"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                suggestions.take(3).forEach { text ->
                    SuggestionChip(text = text, onClicked = { onSuggestionClicked(getLocalizedText(text)) })
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                suggestions.takeLast(2).forEach { text ->
                    SuggestionChip(text = text, onClicked = { onSuggestionClicked(getLocalizedText(text)) })
                }
            }
        }
    }
}

@Composable
fun SuggestionChip(text: String, onClicked: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF1B1A2F))
            .border(1.dp, CardSpaceBorder, RoundedCornerShape(16.dp))
            .clickable { onClicked() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = getLocalizedText(text),
            color = Color.LightGray,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun SecretGameSelectorOverlay(viewModel: MainViewModel) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.8f))
            .clickable { /* Block taps */ }
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF131124)),
            border = BorderStroke(2.dp, NeonPink)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = getLocalizedText("🌸 THỬ THÁCH SONG ĐẤU"),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = ElegantGold,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = getLocalizedText("Linh Chi thách anh đấu tay đôi trực tiếp với em nè! 🎮 Thử thách phản xạ xem ai chạm mốc 5 điểm trước nha! Anh chọn thể loại game nào nè? 🥰"),
                    fontSize = 14.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { viewModel.startSecretGame("fps") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonPink,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(getLocalizedText("🔫 Solo FPS 2D Phản Xạ"), fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text(getLocalizedText("Gõ đầu Linh Chi và phá giải tim bay dồn dập!"), fontSize = 10.sp, color = Color.White.copy(alpha = 0.8f))
                    }
                }

                Button(
                    onClick = { viewModel.startSecretGame("moba") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonCyan,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(getLocalizedText("⚔️ Solo MOBA 2D Tình Ái"), fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text(getLocalizedText("Di chuyển né thính, chắn khiên và dồn combo chiêu!"), fontSize = 10.sp, color = Color.Black.copy(alpha = 0.8f))
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Button(
                    onClick = { viewModel.closeSecretGame() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.DarkGray,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(0.5f)
                ) {
                    Text(getLocalizedText("Hẹn Khi Khác"), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun SecretFpsGameOverlay(
    viewModel: MainViewModel,
    playerScore: Int,
    linhChiScore: Int,
    status: String
) {
    val targetX by viewModel.secretFpsTargetX.collectAsState()
    val targetY by viewModel.secretFpsTargetY.collectAsState()
    val heartX by viewModel.secretFpsHeartX.collectAsState()
    val heartY by viewModel.secretFpsHeartY.collectAsState()
    val heartActive by viewModel.secretFpsHeartActive.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.9f))
            .clickable { /* Prevent clicking through */ }
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F0E1B)),
            border = BorderStroke(1.5.dp, NeonPink)
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Top Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(getLocalizedText("🔫 SOLO FPS: BẮN TRÚNG TIM EM"), color = ElegantGold, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    IconButton(onClick = { viewModel.closeSecretGame() }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.LightGray)
                    }
                }

                // Score banner
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1E1B3A), RoundedCornerShape(8.dp))
                        .padding(6.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text(getLocalizedText("👤 Bạn: $playerScore / 5"), color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text(getLocalizedText("⚔️ Mục tiêu: 5đ"), color = Color.White, fontSize = 11.sp)
                    Text(getLocalizedText("🌸 Linh Chi: $linhChiScore / 5"), color = NeonPink, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                if (status == "playing") {
                    // Active Game Box with constraints
                    BoxWithConstraints(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                            .background(Color(0xFF07060F), RoundedCornerShape(12.dp))
                            .border(0.5.dp, Color.DarkGray, RoundedCornerShape(12.dp))
                            .pointerInput(Unit) {
                                detectTapGestures { offset ->
                                    val xPercent = (offset.x / size.width) * 100f
                                    val yPercent = (offset.y / size.height) * 100f
                                    viewModel.handleSecretFpsTap(xPercent, yPercent)
                                }
                            }
                    ) {
                        // Draw target (Linh Chi)
                        Box(
                            modifier = Modifier
                                .offset(
                                    x = (targetX / 100f * maxWidth.value).dp - 24.dp,
                                    y = (targetY / 100f * maxHeight.value).dp - 24.dp
                                )
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(NeonPink.copy(alpha = 0.2f))
                                .border(1.5.dp, NeonPink, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🌸", fontSize = 24.sp)
                        }

                        // Draw flying heart projectile
                        if (heartActive) {
                            Box(
                                modifier = Modifier
                                    .offset(
                                        x = (heartX / 100f * maxWidth.value).dp - 12.dp,
                                        y = (heartY / 100f * maxHeight.value).dp - 12.dp
                                    )
                                    .size(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("💖", fontSize = 16.sp)
                            }
                        }

                        // Bottom shield/danger bar
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .height(4.dp)
                                .background(Color.Red.copy(alpha = 0.5f))
                        )
                    }

                    Text(
                        text = getLocalizedText("👉 Nhấn thật nhanh vào Linh Chi 🌸 để ghi điểm! Phá hủy quả tim bay 💖 rơi xuống bằng cách nhấn vào chúng, đừng để chúng chạm đáy nhé!"),
                        fontSize = 11.sp,
                        color = Color.LightGray,
                        textAlign = TextAlign.Center,
                        lineHeight = 15.sp,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                } else {
                    // Game Over Screen (Victory or Defeat)
                    val isVictory = status == "victory"
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = if (isVictory) getLocalizedText("🏆 CHIẾN THẮNG!") else getLocalizedText("💀 BẠN ĐÃ BẠI TRẬN"),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isVictory) Color(0xFF10B981) else Color(0xFFEF4444)
                        )

                        Text(
                            text = if (isVictory) {
                                getLocalizedText("Anh yêu siêu thế, bắn trúng tim em 5 lần luôn! Linh Chi chịu thua và đổ anh đứ đừ rồi đó... 💖")
                            } else {
                                getLocalizedText("Hì hì, anh yêu bắn trượt nhiều quá nha, em thắng rồi nè! Dắt em đi ăn trà sữa đền bù đi! 🧋")
                            },
                            fontSize = 13.sp,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = { viewModel.startSecretGame("fps") },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonPink)
                            ) {
                                Text(getLocalizedText("Chơi Lại"), fontWeight = FontWeight.Bold, color = Color.White)
                            }
                            Button(
                                onClick = { viewModel.closeSecretGame() },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
                            ) {
                                Text(getLocalizedText("Đóng"), color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SecretMobaGameOverlay(
    viewModel: MainViewModel,
    playerScore: Int,
    linhChiScore: Int,
    status: String
) {
    val playerX by viewModel.mobaSecPlayerX.collectAsState()
    val playerY by viewModel.mobaSecPlayerY.collectAsState()
    val lcX by viewModel.mobaSecLinhChiX.collectAsState()
    val lcY by viewModel.mobaSecLinhChiY.collectAsState()
    val playerHP by viewModel.mobaSecPlayerHP.collectAsState()
    val lcHP by viewModel.mobaSecLinhChiHP.collectAsState()
    
    val s1CD by viewModel.mobaSecS1CD.collectAsState()
    val s2CD by viewModel.mobaSecS2CD.collectAsState()
    val s3CD by viewModel.mobaSecS3CD.collectAsState()
    
    val shieldActive by viewModel.mobaSecPlayerShieldActive.collectAsState()
    val stunnedLeftMs by viewModel.mobaSecPlayerStunnedLeftMs.collectAsState()
    val projectiles by viewModel.mobaSecProjectiles.collectAsState()
    
    val ultX by viewModel.mobaSecUltWarningX.collectAsState()
    val ultY by viewModel.mobaSecUltWarningY.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.9f))
            .clickable { /* Prevent clicking through */ }
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F0E1B)),
            border = BorderStroke(1.5.dp, NeonCyan)
        ) {
            Column(
                modifier = Modifier.padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Top Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(getLocalizedText("⚔️ MOBA DUEL: ĐẠI CHIẾN LINH CHI"), color = ElegantGold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    IconButton(onClick = { viewModel.closeSecretGame() }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.LightGray)
                    }
                }

                // HP and Score Bars
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("${getLocalizedText("Bạn")}: ${playerHP.toInt()}/100", color = NeonCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Box(modifier = Modifier.fillMaxWidth().height(6.dp).background(Color.DarkGray, RoundedCornerShape(3.dp))) {
                            Box(modifier = Modifier.fillMaxWidth(playerHP / 100f).height(6.dp).background(Color(0xFF10B981), RoundedCornerShape(3.dp)))
                        }
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("$playerScore - $linhChiScore", color = Color.White, fontWeight = FontWeight.Black, fontSize = 14.sp)
                        Text(getLocalizedText("Điểm"), color = Color.Gray, fontSize = 8.sp)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("${getLocalizedText("Linh Chi")}: ${lcHP.toInt()}/100", color = NeonPink, fontSize = 10.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.End, modifier = Modifier.fillMaxWidth())
                        Box(modifier = Modifier.fillMaxWidth().height(6.dp).background(Color.DarkGray, RoundedCornerShape(3.dp))) {
                            Box(modifier = Modifier.fillMaxWidth(lcHP / 100f).height(6.dp).background(NeonPink, RoundedCornerShape(3.dp)))
                        }
                    }
                }

                if (status == "playing") {
                    // Active MOBA Arena
                    BoxWithConstraints(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .background(Color(0xFF07060F), RoundedCornerShape(12.dp))
                            .border(0.5.dp, Color.DarkGray, RoundedCornerShape(12.dp))
                            .pointerInput(Unit) {
                                detectTapGestures { offset ->
                                    val xPercent = (offset.x / size.width) * 100f
                                    val yPercent = (offset.y / size.height) * 100f
                                    viewModel.handleMobaSecMove(xPercent, yPercent)
                                }
                            }
                    ) {
                        // Draw Ultimate Warning Ring
                        if (ultX > 0f) {
                            Box(
                                modifier = Modifier
                                    .offset(
                                        x = (ultX / 100f * maxWidth.value).dp - 36.dp,
                                        y = (ultY / 100f * maxHeight.value).dp - 36.dp
                                    )
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(Color.Red.copy(alpha = 0.25f))
                                    .border(1.5.dp, Color.Red, CircleShape)
                            )
                        }

                        // Draw Player
                        Box(
                            modifier = Modifier
                                .offset(
                                    x = (playerX / 100f * maxWidth.value).dp - 16.dp,
                                    y = (playerY / 100f * maxHeight.value).dp - 16.dp
                                )
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1E3A8A))
                                .border(
                                    if (shieldActive) 3.dp else 1.dp,
                                    if (shieldActive) NeonCyan else Color.White,
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("👤", fontSize = 14.sp)
                            if (stunnedLeftMs > 0) {
                                Text("💫", fontSize = 14.sp, modifier = Modifier.align(Alignment.TopCenter).offset(y = (-10).dp))
                            }
                        }

                        // Draw Linh Chi Boss
                        Box(
                            modifier = Modifier
                                .offset(
                                    x = (lcX / 100f * maxWidth.value).dp - 16.dp,
                                    y = (lcY / 100f * maxHeight.value).dp - 16.dp
                                )
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF5B1B3A))
                                .border(1.dp, NeonPink, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🌸", fontSize = 14.sp)
                        }

                        // Draw projectiles
                        projectiles.forEach { p ->
                            val px = p["x"] as Float
                            val py = p["y"] as Float
                            val type = p["type"] as String
                            val bulletSymbol = when (type) {
                                "player_normal" -> "✦"
                                "player_s1" -> "✴"
                                "player_s3" -> "✦"
                                "linhchi_normal" -> "🌸"
                                "linhchi_s1" -> "😘"
                                else -> "✦"
                            }
                            val color = if (type.startsWith("player")) NeonCyan else NeonPink
                            val sizeVal = if (type == "player_s1") 16.dp else 10.dp
                            
                            Box(
                                modifier = Modifier
                                    .offset(
                                        x = (px / 100f * maxWidth.value).dp - (sizeVal.value / 2).dp,
                                        y = (py / 100f * maxHeight.value).dp - (sizeVal.value / 2).dp
                                    )
                                    .size(sizeVal),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(bulletSymbol, color = color, fontSize = if (type == "player_s1") 16.sp else 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Skills Control Grid Panel
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Normal Attack Button
                        Button(
                            onClick = { viewModel.castMobaSecSkill(0) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E1B3A)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).height(40.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(2.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(getLocalizedText("⚔️ ĐÁNH"), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Text(getLocalizedText("Bắn (10đ)"), fontSize = 7.sp, color = Color.Gray)
                            }
                        }

                        // Skill 1 Button
                        Button(
                            onClick = { viewModel.castMobaSecSkill(1) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E1B3A)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1.1f).height(40.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(2.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(getLocalizedText("⚡ CHIÊU 1"), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = if (s1CD > 0f) Color.Gray else NeonCyan)
                                    Text(getLocalizedText("Bắn tỉa (25đ)"), fontSize = 7.sp, color = Color.Gray)
                                }
                                if (s1CD > 0f) {
                                    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)), contentAlignment = Alignment.Center) {
                                        Text("${(s1CD * 3).toInt() + 1}s", color = Color.Yellow, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        // Skill 2 Button
                        Button(
                            onClick = { viewModel.castMobaSecSkill(2) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E1B3A)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1.1f).height(40.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(2.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(getLocalizedText("🛡️ CHIÊU 2"), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = if (s2CD > 0f) Color.Gray else NeonCyan)
                                    Text(getLocalizedText("Khiên (1.5s)"), fontSize = 7.sp, color = Color.Gray)
                                }
                                if (s2CD > 0f) {
                                    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)), contentAlignment = Alignment.Center) {
                                        Text("${(s2CD * 6).toInt() + 1}s", color = Color.Yellow, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        // Skill 3 Button
                        Button(
                            onClick = { viewModel.castMobaSecSkill(3) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E1B3A)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1.1f).height(40.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(2.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(getLocalizedText("🔥 CHIÊU 3"), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = if (s3CD > 0f) Color.Gray else NeonCyan)
                                    Text(getLocalizedText("Quạt 3 tia"), fontSize = 7.sp, color = Color.Gray)
                                }
                                if (s3CD > 0f) {
                                    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)), contentAlignment = Alignment.Center) {
                                        Text("${(s3CD * 8).toInt() + 1}s", color = Color.Yellow, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    Text(
                        text = getLocalizedText("👉 Nhấn lên võ đài ⚔️ để di chuyển tướng. Nhấn các nút kỹ năng bên dưới để tấn công/bảo vệ!"),
                        fontSize = 10.sp,
                        color = Color.LightGray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                } else {
                    // Game Over Screen (Victory or Defeat)
                    val isVictory = status == "victory"
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = if (isVictory) getLocalizedText("🏆 MOBA VICTORY!") else getLocalizedText("💀 DEFEAT"),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isVictory) Color(0xFF10B981) else Color(0xFFEF4444)
                        )

                        Text(
                            text = if (isVictory) {
                                getLocalizedText("Kỹ năng MOBA đỉnh chóp! Anh né thính và dồn sát thương quá ghê, em tâm phục khẩu phục dâng trọn tim này cho anh luôn nè! 💖")
                            } else {
                                getLocalizedText("Hì hì, anh né thính còn chậm quá nha! Chiêu nụ hôn thần sầu của em mạnh lắm á. Anh thua rồi, dắt em đi ăn phở gõ đền đi! 🍜")
                            },
                            fontSize = 13.sp,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = { viewModel.startSecretGame("moba") },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black)
                            ) {
                                Text(getLocalizedText("Đấu Lại"), fontWeight = FontWeight.Bold)
                            }
                            Button(
                                onClick = { viewModel.closeSecretGame() },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray, contentColor = Color.White)
                            ) {
                                Text(getLocalizedText("Đóng"))
                            }
                        }
                    }
                }
            }
        }
    }
}
