package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CardSpaceBackground
import com.example.ui.theme.CardSpaceBorder
import com.example.ui.theme.ElegantGold
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: MainViewModel) {
    val appLanguage by viewModel.appLanguage.collectAsState()
    val soundVolume by viewModel.soundVolume.collectAsState()
    val scrollState = rememberScrollState()

    var showPolicyDialog by remember { mutableStateOf(false) }
    var policyDialogTitle by remember { mutableStateOf("") }
    var policyDialogText by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0E17)) // Deep space visual theme matching other screens
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // Header Settings Title
            Text(
                text = t("settings_title"),
                color = NeonPink,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .testTag("settings_screen_header")
            )

            // Status Card (Decoration visual polish)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CardSpaceBackground, RoundedCornerShape(12.dp))
                    .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(Color.Green, CircleShape)
                    )
                    Column {
                        Text(
                            text = t("device_status"),
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = t("system_optimized"),
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // 1. Sound Settings Card
            Card(
                colors = CardDefaults.cardColors(containerColor = CardSpaceBackground),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CardSpaceBorder, RoundedCornerShape(12.dp))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = if (soundVolume == 0f) Icons.Default.VolumeMute else Icons.Default.VolumeUp,
                            contentDescription = t("sound_settings_title"),
                            tint = NeonCyan,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = t("sound_settings_title"),
                            color = NeonCyan,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    HorizontalDivider(color = Color.White.copy(alpha = 0.1f))

                    Text(
                        text = t("sound_volume_label") + ": " + if (soundVolume == 0f) t("sound_status_muted") else "${(soundVolume * 100).toInt()}%",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        IconButton(
                            onClick = { viewModel.setSoundVolume(0f) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeMute,
                                contentDescription = "Mute",
                                tint = Color.Gray,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Slider(
                            value = soundVolume,
                            onValueChange = { viewModel.setSoundVolume(it) },
                            onValueChangeFinished = {
                                SoundManager.playSound("hit") // Satisfying interactive feedback
                            },
                            colors = SliderDefaults.colors(
                                thumbColor = NeonCyan,
                                activeTrackColor = NeonCyan,
                                inactiveTrackColor = Color.White.copy(alpha = 0.15f)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("sound_volume_slider")
                        )

                        IconButton(
                            onClick = { viewModel.setSoundVolume(1f) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = "Max Volume",
                                tint = NeonCyan,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // 2. Language Settings Card
            Card(
                colors = CardDefaults.cardColors(containerColor = CardSpaceBackground),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CardSpaceBorder, RoundedCornerShape(12.dp))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = t("language_settings_title"),
                            tint = ElegantGold,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = t("language_settings_title"),
                            color = ElegantGold,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    HorizontalDivider(color = Color.White.copy(alpha = 0.1f))

                    Text(
                        text = t("select_language"),
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Vietnamese Option
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (appLanguage == AppLanguage.VI) ElegantGold.copy(alpha = 0.15f)
                                    else Color.White.copy(alpha = 0.02f)
                                )
                                .border(
                                    1.dp,
                                    if (appLanguage == AppLanguage.VI) ElegantGold else Color.White.copy(alpha = 0.1f),
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { viewModel.setLanguage(AppLanguage.VI) }
                                .testTag("lang_vi_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "🇻🇳",
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = "Tiếng Việt",
                                    color = if (appLanguage == AppLanguage.VI) ElegantGold else Color.Gray,
                                    fontWeight = if (appLanguage == AppLanguage.VI) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 13.sp
                                )
                            }
                        }

                        // English Option
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (appLanguage == AppLanguage.EN) ElegantGold.copy(alpha = 0.15f)
                                    else Color.White.copy(alpha = 0.02f)
                                )
                                .border(
                                    1.dp,
                                    if (appLanguage == AppLanguage.EN) ElegantGold else Color.White.copy(alpha = 0.1f),
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { viewModel.setLanguage(AppLanguage.EN) }
                                .testTag("lang_en_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "🇬🇧",
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = "English",
                                    color = if (appLanguage == AppLanguage.EN) ElegantGold else Color.Gray,
                                    fontWeight = if (appLanguage == AppLanguage.EN) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }

            // 3. Privacy Policy & Terms Card
            Card(
                colors = CardDefaults.cardColors(containerColor = CardSpaceBackground),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CardSpaceBorder, RoundedCornerShape(12.dp))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = t("policy_and_terms"),
                            tint = NeonPink,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = t("policy_and_terms"),
                            color = NeonPink,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    HorizontalDivider(color = Color.White.copy(alpha = 0.1f))

                    // Privacy Policy list row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clickable {
                                policyDialogTitle = LocaleManager.getString("privacy_policy_title", appLanguage)
                                policyDialogText = LocaleManager.getString("privacy_policy_text", appLanguage)
                                showPolicyDialog = true
                            }
                            .padding(horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = Color.LightGray,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = t("privacy_policy_title"),
                                color = Color.White,
                                fontSize = 13.sp
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = t("view_policy"),
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    HorizontalDivider(color = Color.White.copy(alpha = 0.05f))

                    // Terms list row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clickable {
                                policyDialogTitle = LocaleManager.getString("terms_of_service_title", appLanguage)
                                policyDialogText = LocaleManager.getString("terms_of_service_text", appLanguage)
                                showPolicyDialog = true
                            }
                            .padding(horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = null,
                                tint = Color.LightGray,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = t("terms_of_service_title"),
                                color = Color.White,
                                fontSize = 13.sp
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = t("view_policy"),
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // App Brand info footer (visual polish)
            Spacer(modifier = Modifier.height(12.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = t("app_name"),
                    color = Color.Gray,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "v1.4.0 • Offline Lag Simulation Protocol",
                    color = Color.DarkGray,
                    fontSize = 10.sp
                )
            }
        }
    }

    // Policy details Modal dialog
    if (showPolicyDialog) {
        AlertDialog(
            onDismissRequest = { showPolicyDialog = false },
            title = {
                Text(
                    text = policyDialogTitle,
                    color = NeonPink,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = policyDialogText,
                    color = Color.LightGray,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    textAlign = TextAlign.Justify
                )
            },
            confirmButton = {
                Button(
                    onClick = { showPolicyDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = t("close"),
                        color = Color.Black,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            containerColor = Color(0xFF1B1A24),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
        )
    }
}
