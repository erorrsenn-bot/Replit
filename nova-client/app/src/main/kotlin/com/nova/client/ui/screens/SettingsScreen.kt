package com.nova.client.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.nova.client.ui.components.*
import com.nova.client.viewmodel.MainViewModel

@Composable
fun SettingsScreen(viewModel: MainViewModel, onBack: () -> Unit) {
    val config by viewModel.config.collectAsState()

    Scaffold(topBar = { NovaTopBar("Settings", onBack = onBack) }) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // ── HUD ─────────────────────────────────────────────────────────
            item { SectionHeader("HUD Overlay") }

            item {
                FeatureCard(
                    icon = Icons.Default.Camera,
                    title = "Screenshot Mode",
                    subtitle = if (config.screenshotMode) "Enabled — HUD hidden" else "Hides all HUD elements for clean screenshots",
                    enabled = config.screenshotMode,
                    tint = Color(0xFF6C63FF),
                    onToggle = { viewModel.toggleScreenshotMode(it) },
                    onClick = null
                )
            }

            // ── Chat ─────────────────────────────────────────────────────────
            item { SectionHeader("Chat") }

            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        SettingsToggleRow(
                            label = "Chat Timestamps",
                            subtitle = "Show HH:MM:SS next to each message",
                            checked = config.chatTimestamps,
                            onToggle = { /* update via config */ }
                        )
                    }
                }
            }

            // ── Quick replies ─────────────────────────────────────────────────
            item { SectionHeader("Quick Replies") }

            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        config.quickReplies.forEach { reply ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Chat, null, Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                                Spacer(Modifier.width(8.dp))
                                Text(reply, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            // ── Profiles ──────────────────────────────────────────────────────
            item { SectionHeader("Profiles") }

            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        config.profiles.forEach { profile ->
                            Row(
                                Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    if (profile.id == config.activeProfileId) Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked,
                                    null,
                                    tint = if (profile.id == config.activeProfileId) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(0.4f),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(Modifier.width(12.dp))
                                Text(
                                    profile.name,
                                    fontWeight = if (profile.id == config.activeProfileId) FontWeight.Bold else FontWeight.Normal,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    "${profile.hudElements.count { it.enabled }} HUD • ${profile.waypoints.size} WP",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(0.4f)
                                )
                            }
                        }
                    }
                }
            }

            // ── About ──────────────────────────────────────────────────────────
            item { SectionHeader("About") }

            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        InfoRow("App", "Nova Client")
                        InfoRow("Version", "1.0.0")
                        InfoRow("Platform", "Minecraft Bedrock Edition")
                        InfoRow("Language", "Kotlin + Jetpack Compose")
                        InfoRow("HUD Method", "System Overlay (TYPE_APPLICATION_OVERLAY)")
                        InfoRow("Protocol", "RakNet UDP (Bedrock ping)")
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsToggleRow(label: String, subtitle: String, checked: Boolean, onToggle: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(0.5f))
        }
        Switch(checked = checked, onCheckedChange = onToggle)
    }
}
