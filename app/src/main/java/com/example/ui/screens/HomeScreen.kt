package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.QuizEpisode
import com.example.data.repository.QuizDataProvider
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    onSelectEpisode: (QuizEpisode, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var isTwoPlayerMode by remember { mutableStateOf(false) }
    var showRulesDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(StadiumDark)
            .padding(horizontal = 16.dp)
            .testTag("home_screen"),
        contentPadding = PaddingValues(top = 24.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hero_banner"),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = StadiumCard
                ),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.linearGradient(
                        listOf(TrophyGold, PitchGreenDark, NeonCyan)
                    )
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(TrophyGoldDark.copy(alpha = 0.25f), StadiumDark.copy(alpha = 0.85f))
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Show Logo Badge
                        Box(
                            modifier = Modifier
                                .size(68.dp)
                                .clip(CircleShape)
                                .background(StadiumDark)
                                .border(2.5.dp, TrophyGold, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "⚽", fontSize = 34.sp)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "تحدي الـ 30 كروي",
                            style = MaterialTheme.typography.headlineMedium,
                            color = TrophyGoldBright,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "برنامج المسابقات الكروية الأقوى في الوطن العربي",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // 5 Rounds quick chips
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            RoundMiniChip("1. من أنا؟", "💡")
                            RoundMiniChip("2. مسيرة", "⏱️")
                            RoundMiniChip("3. تشكيلة", "📋")
                            RoundMiniChip("4. مزاد", "🔨")
                            RoundMiniChip("5. سرعة", "⚡")
                        }
                    }
                }
            }
        }

        // Mode Switcher & Quick Start
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Quick Random Match
                Button(
                    onClick = {
                        onSelectEpisode(QuizDataProvider.getRandomEpisode(), isTwoPlayerMode)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("quick_match_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PitchGreenDark
                    ),
                    border = ButtonDefaults.outlinedButtonBorder().copy(
                        brush = Brush.horizontalGradient(listOf(PitchGreen, PitchGreenBright))
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = PitchGreenBright,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "مباراة عشوائية",
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Rules button
                OutlinedButton(
                    onClick = { showRulesDialog = true },
                    modifier = Modifier
                        .height(52.dp)
                        .testTag("rules_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = TrophyGoldBright
                    ),
                    border = ButtonDefaults.outlinedButtonBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(StadiumBorder)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "القوانين", fontWeight = FontWeight.Bold)
                }
            }
        }

        // Single vs 1v1 Mode Toggle
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = StadiumCard)
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = if (isTwoPlayerMode) "👥" else "👤", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isTwoPlayerMode) "وضع التحدي الثنائي (1 ضد 1)" else "وضع الفردي (ضد مقدم البرنامج)",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isTwoPlayerMode) "تنافس مع صديقك بالتبادل في كل جولة" else "اختبر معلوماتك لتقييمك من 50 نقطة",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Switch(
                        checked = isTwoPlayerMode,
                        onCheckedChange = { isTwoPlayerMode = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = TrophyGoldBright,
                            checkedTrackColor = TrophyGoldDark
                        ),
                        modifier = Modifier.testTag("mode_switch")
                    )
                }
            }
        }

        // Section Title: Episodes
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "اختر الحلقة الكروية 📺",
                    style = MaterialTheme.typography.titleMedium,
                    color = TrophyGoldBright,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${QuizDataProvider.episodes.size} حلقات متاحة",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
            }
        }

        // Episodes List
        items(QuizDataProvider.episodes) { episode ->
            EpisodeItemCard(
                episode = episode,
                onClick = { onSelectEpisode(episode, isTwoPlayerMode) }
            )
        }
    }

    // Rules Dialog
    if (showRulesDialog) {
        AlertDialog(
            onDismissRequest = { showRulesDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "📜", fontSize = 22.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "قوانين تحدي الـ 30",
                        fontWeight = FontWeight.Bold,
                        color = TrophyGoldBright
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("اللعبة مكونة من 5 جولات كروية نارية (التقييم من 50 نقطة):", fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text("• الجولة 1: 'من أنا؟' (10 نقاط) - 4 تلميحات متدرجة عن لاعب. كل تلميح يكلف خصم في النقاط.", color = TextSecondary, fontSize = 12.sp)
                    Text("• الجولة 2: 'الرابط العجيب' (10 نقاط) - مسيرة لاعب عبر أنديته بالترتيب الزمني.", color = TextSecondary, fontSize = 12.sp)
                    Text("• الجولة 3: 'التشكيلة الناقصة' (10 نقاط) - تشكيلة نهائي تاريخي مع لاعب واحد مجهول على رقعة التكتيك.", color = TextSecondary, fontSize = 12.sp)
                    Text("• الجولة 4: 'تحدي المزاد' (10 نقاط) - ذكر عدة أسماء تطابق الشرط (كل اسم صحيح بنقطتين).", color = TextSecondary, fontSize = 12.sp)
                    Text("• الجولة 5: 'أسئلة السرعة' (10 نقاط) - 5 معلومات سريعة للإجابة بـ صح أو خطأ.", color = TextSecondary, fontSize = 12.sp)
                }
            },
            confirmButton = {
                TextButton(onClick = { showRulesDialog = false }) {
                    Text("فهمت، لنبدأ التحدي!", fontWeight = FontWeight.Bold, color = TrophyGoldBright)
                }
            },
            containerColor = StadiumCard
        )
    }
}

@Composable
fun RoundMiniChip(title: String, emoji: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = StadiumDark.copy(alpha = 0.8f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = emoji, fontSize = 11.sp)
            Spacer(modifier = Modifier.width(3.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
fun EpisodeItemCard(
    episode: QuizEpisode,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("episode_card_${episode.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = StadiumCard),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(StadiumBorder)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(StadiumCardHover)
                    .border(1.5.dp, TrophyGold, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = episode.iconEmoji, fontSize = 22.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = episode.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = episode.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    fontSize = 12.sp,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = StadiumDark
                    ) {
                        Text(
                            text = episode.era,
                            style = MaterialTheme.typography.labelSmall,
                            color = TrophyGoldBright,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = StadiumDark
                    ) {
                        Text(
                            text = episode.difficulty,
                            style = MaterialTheme.typography.labelSmall,
                            color = PitchGreenBright,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = TrophyGoldBright,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
