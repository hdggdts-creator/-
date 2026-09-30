package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EpisodePack
import com.example.data.repository.QuizDataProvider
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

    fun openCreatorLink() {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/Mos_mohh"))
            context.startActivity(intent)
        } catch (_: Exception) {}
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
            .testTag("home_screen"),
        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Action Bar: Theme Switch & Share Button
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // App Title Small / Logo
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface)
                            .border(1.5.dp, TrophyGold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "⚽", fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "تحدي الـ 30 كروي",
                        style = MaterialTheme.typography.titleMedium,
                        color = TrophyGoldBright,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Controls: Share & Theme Toggle
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Share App Button
                    IconButton(
                        onClick = { shareApp() },
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface)
                            .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                            .testTag("share_app_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "مشاركة اللعبة",
                            tint = TrophyGoldBright,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Dark / Light Theme Toggle Switch
                    IconButton(
                        onClick = onToggleTheme,
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface)
                            .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                            .testTag("theme_toggle_button")
                    ) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "تبديل المظهر",
                            tint = if (isDarkTheme) TrophyGoldBright else NeonCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // Hero Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hero_banner"),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
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
                                listOf(
                                    TrophyGoldDark.copy(alpha = if (isDarkTheme) 0.25f else 0.15f),
                                    MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                                )
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .border(2.5.dp, TrophyGold, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🏆", fontSize = 32.sp)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "برنامج تحدي الـ 30",
                            style = MaterialTheme.typography.headlineSmall,
                            color = TrophyGoldBright,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "اختبار العقول الكروية بنظام منع التكرار ومقدم حماسي",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
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

        // Action Buttons: Quick Random & Share & Rules
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Quick Random Match
                Button(
                    onClick = { onSelectRandom(isTwoPlayerMode) },
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

                // Share Button (Quick Action)
                FilledTonalButton(
                    onClick = { shareApp() },
                    modifier = Modifier
                        .height(52.dp)
                        .testTag("share_app_row_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "مشاركة",
                        tint = TrophyGoldBright,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "مشاركة", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
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
                        brush = SolidColor(MaterialTheme.colorScheme.outline)
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
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = SolidColor(MaterialTheme.colorScheme.outline)
                )
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
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isTwoPlayerMode) "تنافس مع صديقك بالتبادل في كل جولة" else "اختبر معلوماتك لتقييمك من 50 نقطة",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                        color = TrophyGoldBright,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "5 مجموعات أسئلة متجددة بدون تكرار في كل حلقة",
                        style = MaterialTheme.typography.labelSmall,
                        color = PitchGreenBright
                    )
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = "${QuizDataProvider.episodePacks.size} حلقات • 25 تحدي",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // Episodes List
        items(QuizDataProvider.episodePacks) { pack ->
            EpisodePackCard(
                pack = pack,
                onClick = { onSelectEpisode(pack.id, isTwoPlayerMode) }
            )
        }

        // Creator Credits Footer (Clickable -> t.me/Mos_mohh)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { openCreatorLink() }
                    .testTag("footer_author_credits"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.horizontalGradient(
                        listOf(TrophyGoldDark, MaterialTheme.colorScheme.outline, NeonCyan)
                    )
                )
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(text = "👑", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "حقوق صانع اللعبة | Made by",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Telegram: t.me/Mos_mohh",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TrophyGoldBright,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                            contentDescription = "فتح الرابط",
                            tint = TrophyGoldBright,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Text(
                        text = "انقر هنا للتواصل ومتابعة أحدث التحديثات الكروية",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                        fontSize = 10.sp
                    )
                }
            }
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
                        text = "قوانين تحدي الـ 30 كروي",
                        fontWeight = FontWeight.Bold,
                        color = TrophyGoldBright
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("اللعبة مكونة من 5 جولات كروية نارية (التقييم من 50 نقطة):", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Text("• الجولة 1: 'من أنا؟' (10 نقاط) - 4 تلميحات متدرجة. كل تلميح يكلف خصم في النقاط.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                    Text("• الجولة 2: 'الرابط العجيب' (10 نقاط) - مسيرة لاعب عبر أنديته بالترتيب الزمني.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                    Text("• الجولة 3: 'التشكيلة الناقصة' (10 نقاط) - تشكيلة نهائي تاريخي مع لاعب مجهول على رقعة التكتيك.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                    Text("• الجولة 4: 'تحدي المزاد' (10 نقاط) - ذكر عدة أسماء تطابق الشرط (كل اسم صحيح بنقطتين).", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                    Text("• الجولة 5: 'أسئلة السرعة' (10 نقاط) - 5 معلومات سريعة للإجابة بـ صح أو خطأ.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("✨ ميزة منع التكرار: كل حلقة تحتوي على 5 مجموعات أسئلة مختلفة تماماً يتم اختيارها عشوائياً بدون تكرار!", color = PitchGreenBright, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            },
            confirmButton = {
                TextButton(onClick = { showRulesDialog = false }) {
                    Text("فهمت، لنبدأ التحدي!", fontWeight = FontWeight.Bold, color = TrophyGoldBright)
                }
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}

@Composable
fun RoundMiniChip(title: String, emoji: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
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
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 10.sp
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
            .clickable { onClick() }
            .testTag("episode_card_${pack.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = SolidColor(MaterialTheme.colorScheme.outline)
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
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(1.5.dp, TrophyGold, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = pack.iconEmoji, fontSize = 24.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = pack.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = pack.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = pack.era,
                            style = MaterialTheme.typography.labelSmall,
                            color = TrophyGoldBright,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = "${pack.variants.size} مجموعات متجددة",
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
