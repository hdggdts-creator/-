package com.example.ui.screens

import android.content.Intent
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EpisodePack
import com.example.data.repository.QuizDataProvider
import com.example.ui.components.ElegantBrandWatermark
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    onSelectEpisode: (String, Boolean) -> Unit,
    onSelectRandom: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isTwoPlayerMode by remember { mutableStateOf(false) }
    var showRulesDialog by remember { mutableStateOf(false) }

    fun shareApp() {
        try {
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(
                    Intent.EXTRA_TEXT,
                    "⚽ اختبر معلوماتك الكروية معي في تطبيق «تحدي الـ 30 كروي»! 5 جولات حماسية ورقعة تكتيكية حية، هل تستطيع تحقيق 50 من 50 نقطة؟\n\nتواصل وحمل اللعبة الآن: https://t.me/Mos_mohh"
                )
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, "مشاركة لعبة تحدي الـ 30 كروي")
            context.startActivity(shareIntent)
        } catch (_: Exception) {}
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
            .testTag("home_screen"),
        contentPadding = PaddingValues(top = 16.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Action Bar: Header Logo, Theme Switch & Share Button
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // App Logo / Title
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .border(1.dp, PitchGreen, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "⚽", fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "تحدي الـ 30 كروي",
                            style = MaterialTheme.typography.titleMedium,
                            color = if (isDarkTheme) Color.White else ArenaLightTextPrimary,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "الموسوعة الكروية التنافسية",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Controls: Share & Theme Toggle (Flat icon buttons)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Share App Button
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                            .clickable { shareApp() }
                            .testTag("share_app_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "مشاركة اللعبة",
                            tint = PitchGreen,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Dark / Light Theme Toggle Switch
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                            .clickable { onToggleTheme() }
                            .testTag("theme_toggle_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "تبديل المظهر",
                            tint = TrophyGoldBright,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // Hero Banner (Flat Design)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .testTag("hero_banner"),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    // Trophy Avatar
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .border(1.5.dp, TrophyGold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🏆", fontSize = 28.sp)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "برنامج تحدي الـ 30",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "اختبر معلوماتك في 5 جولات كروية متنوعة مع رقعة تكتيكية حية",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // 5 Rounds Quick Chips
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

        // Action Buttons: Quick Random & Rules
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Quick Random Match (Flat Solid Green Button)
                Button(
                    onClick = { onSelectRandom(isTwoPlayerMode) },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("quick_match_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PitchGreen,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🎲", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "مباراة عشوائية",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Rules button (Flat Outlined/Surface button)
                OutlinedButton(
                    onClick = { showRulesDialog = true },
                    modifier = Modifier
                        .height(48.dp)
                        .testTag("rules_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "📜", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "القوانين",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Single vs 1v1 Segmented Mode Switcher (Clean Flat Design)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.outline,
                        RoundedCornerShape(10.dp)
                    ),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "نمط التحدي:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Segmented Tabs Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(3.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Solo Mode Tab
                        val isSolo = !isTwoPlayerMode
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    if (isSolo) PitchGreen else Color.Transparent
                                )
                                .clickable { isTwoPlayerMode = false }
                                .testTag("mode_solo_tab"),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "👤", fontSize = 13.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "فردي (ضد المذيع)",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (isSolo) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = if (isSolo) FontWeight.ExtraBold else FontWeight.Medium
                                )
                            }
                        }

                        // 1v1 Mode Tab
                        val is1v1 = isTwoPlayerMode
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    if (is1v1) Player1Color else Color.Transparent
                                )
                                .clickable { isTwoPlayerMode = true }
                                .testTag("mode_1v1_tab"),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "⚔️", fontSize = 13.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "زوجي (1 ضد 1)",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (is1v1) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = if (is1v1) FontWeight.ExtraBold else FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section Title: Episodes & Anti-Repetition Tag
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "اختر الحلقة الكروية 📺",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "10 نسخ متجددة ومحمية ضد التكرار في كل حلقة",
                        style = MaterialTheme.typography.labelSmall,
                        color = PitchGreen
                    )
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Text(
                        text = "${QuizDataProvider.episodePacks.size} حلقات • 50 تحدياً",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // Episodes List (Clean Flat Cards)
        items(QuizDataProvider.episodePacks) { pack ->
            EpisodePackCard(
                pack = pack,
                onClick = { onSelectEpisode(pack.id, isTwoPlayerMode) }
            )
        }

        // Creator Credits Flat Watermark Footer (t.me/Mos_mohh)
        item {
            ElegantBrandWatermark()
        }
    }

    // Rules Dialog (Clean Flat Design)
    if (showRulesDialog) {
        AlertDialog(
            onDismissRequest = { showRulesDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "📜", fontSize = 22.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "قوانين تحدي الـ 30 كروي",
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "اللعبة مكونة من 5 جولات كروية نارية (التقييم من 50 نقطة):",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text("• الجولة 1: 'من أنا؟' (10 نقاط) - 4 تلميحات تدريجية. كل تلميح يكلف خصم في النقاط.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                    Text("• الجولة 2: 'الرابط العجيب' (10 نقاط) - مسيرة لاعب عبر أنديته بالترتيب الزمني.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                    Text("• الجولة 3: 'التشكيلة الناقصة' (10 نقاط) - تشكيلة نهائي تاريخي مع لاعب مجهول على رقعة التكتيك.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                    Text("• الجولة 4: 'تحدي المزاد' (10 نقاط) - ذكر عدة أسماء تطابق الشرط (كل اسم صحيح بنقطتين).", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                    Text("• الجولة 5: 'أسئلة السرعة' (10 نقاط) - 5 معلومات سريعة للإجابة بـ صح أو خطأ.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = PitchGreenDark.copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, PitchGreen.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = "✨ ميزة منع التكرار: كل حلقة تحتوي على 10 مجموعات أسئلة مختلفة تماماً يتم اختيارها عشوائياً بدون تكرار حتى استهلاك كامل المجموعات!",
                            color = PitchGreenBright,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showRulesDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = PitchGreen)
                ) {
                    Text("فهمت، لنبدأ التحدي! ⚽", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            shape = RoundedCornerShape(12.dp),
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}

@Composable
fun RoundMiniChip(title: String, emoji: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = emoji, fontSize = 11.sp)
            Spacer(modifier = Modifier.width(3.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun EpisodePackCard(
    pack: EpisodePack,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                MaterialTheme.colorScheme.outline,
                RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .testTag("episode_card_${pack.id}"),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(1.dp, TrophyGold, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = pack.iconEmoji, fontSize = 22.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = pack.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = pack.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Text(
                            text = pack.era,
                            style = MaterialTheme.typography.labelSmall,
                            color = TrophyGoldBright,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = PitchGreenDark.copy(alpha = 0.25f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, PitchGreen.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = "${pack.variants.size} نسخ متجددة 🔄",
                            style = MaterialTheme.typography.labelSmall,
                            color = PitchGreenBright,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "بدء الحلقة",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
