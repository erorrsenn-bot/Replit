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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.nova.client.data.models.FpsBoosterConfig
import com.nova.client.ui.components.*
import com.nova.client.viewmodel.MainViewModel

@Composable
fun FpsBoosterScreen(viewModel: MainViewModel, onBack: () -> Unit) {
    val fps by viewModel.fpsBooster.collectAsState()

    Scaffold(topBar = { NovaTopBar("FPS Booster", onBack = onBack) }) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                // Main toggle
                FeatureCard(
                    icon = Icons.Default.Speed,
                    title = "FPS Booster",
                    subtitle = "Optimize render distance and effects",
                    enabled = fps.enabled,
                    tint = Color(0xFF00CC66),
                    onToggle = { viewModel.updateFpsBooster(fps.copy(enabled = it)) }
                )
            }

            if (fps.enabled) {
                item { SectionHeader("Render Distance") }

                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            LabeledSlider(
                                label = "Render Distance",
                                value = fps.renderDistance.toFloat(),
                                valueRange = 4f..16f,
                                onValueChange = { viewModel.updateFpsBooster(fps.copy(renderDistance = it.toInt())) },
                                valueText = "${fps.renderDistance} chunks"
                            )

                            Spacer(Modifier.height(8.dp))

                            // Performance estimate
                            val estimate = when {
                                fps.renderDistance <= 4  -> "Extreme boost (~3x FPS)" to Color(0xFF00FF88)
                                fps.renderDistance <= 6  -> "High boost (~2x FPS)"    to Color(0xFF00CC66)
                                fps.renderDistance <= 8  -> "Medium boost (~1.5x FPS)" to Color(0xFFFFAA00)
                                fps.renderDistance <= 12 -> "Mild boost (~1.2x FPS)"  to Color(0xFFFF8800)
                                else                     -> "No boost (vanilla)"       to Color(0xFF888888)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.TrendingUp, null, tint = estimate.second, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(8.dp))
                                Text(estimate.first, style = MaterialTheme.typography.bodySmall, color = estimate.second, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }

                item { SectionHeader("Graphics Options") }

                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            GraphicsToggleRow(
                                label = "Particle Reduction",
                                subtitle = "Reduce particle effects (fire, smoke, water)",
                                checked = fps.particleReduction,
                                boost = "+5-10 FPS",
                                onToggle = { viewModel.updateFpsBooster(fps.copy(particleReduction = it)) }
                            )
                            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                            GraphicsToggleRow(
                                label = "Smooth Lighting",
                                subtitle = "Disable for better performance",
                                checked = fps.smoothLighting,
                                boost = "+3-8 FPS when off",
                                onToggle = { viewModel.updateFpsBooster(fps.copy(smoothLighting = it)) }
                            )
                            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                            GraphicsToggleRow(
                                label = "Fancy Graphics",
                                subtitle = "Transparent leaves and water",
                                checked = fps.fancyGraphics,
                                boost = "+5-15 FPS when off",
                                onToggle = { viewModel.updateFpsBooster(fps.copy(fancyGraphics = it)) }
                            )
                        }
                    }
                }

                item { SectionHeader("Clouds") }

                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text("Cloud Quality", style = MaterialTheme.typography.bodyMedium)
                            Spacer(Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf("Off", "Fast", "Fancy").forEachIndexed { i, label ->
                                    FilterChip(
                                        selected = fps.cloudLevel == i,
                                        onClick  = { viewModel.updateFpsBooster(fps.copy(cloudLevel = i)) },
                                        label    = { Text(label) }
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF00CC66).copy(alpha = 0.1f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Info, null, tint = Color(0xFF00CC66), modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "These settings are applied as config values that Minecraft Bedrock reads from game settings on restart.",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GraphicsToggleRow(label: String, subtitle: String, checked: Boolean, boost: String, onToggle: (Boolean) -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
            Text(boost, style = MaterialTheme.typography.labelSmall, color = Color(0xFF00CC66))
        }
        Switch(checked = checked, onCheckedChange = onToggle)
    }
}
