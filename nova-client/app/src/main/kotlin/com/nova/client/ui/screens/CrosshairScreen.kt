package com.nova.client.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.nova.client.data.models.CrosshairConfig
import com.nova.client.data.models.CrosshairStyle
import com.nova.client.ui.components.*
import com.nova.client.viewmodel.MainViewModel
import kotlin.math.*

@Composable
fun CrosshairScreen(viewModel: MainViewModel, onBack: () -> Unit) {
    val crosshair by viewModel.crosshairConfig.collectAsState()

    Scaffold(topBar = { NovaTopBar("Crosshair Customizer", onBack = onBack) }) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Live preview
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(Color(0xFF111111), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    CrosshairPreviewCanvas(crosshair)
                    Text(
                        "Preview",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.3f),
                        modifier = Modifier.align(Alignment.TopStart).padding(10.dp)
                    )
                }
            }

            // Style picker
            item {
                SectionHeader("Style")
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(CrosshairStyle.entries) { style ->
                        FilterChip(
                            selected = crosshair.style == style,
                            onClick  = { viewModel.updateCrosshair(crosshair.copy(style = style)) },
                            label    = { Text(style.label, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }
            }

            // Size
            item {
                SectionHeader("Size & Thickness")
                LabeledSlider(
                    label = "Size",
                    value = crosshair.size,
                    valueRange = 0.5f..3.0f,
                    onValueChange = { viewModel.updateCrosshair(crosshair.copy(size = it)) }
                )
                LabeledSlider(
                    label = "Thickness",
                    value = crosshair.thickness,
                    valueRange = 1f..8f,
                    onValueChange = { viewModel.updateCrosshair(crosshair.copy(thickness = it)) },
                    valueText = "%.0fpx".format(crosshair.thickness)
                )
                LabeledSlider(
                    label = "Gap Size",
                    value = crosshair.gapSize,
                    valueRange = 0f..20f,
                    onValueChange = { viewModel.updateCrosshair(crosshair.copy(gapSize = it)) },
                    valueText = "%.0fpx".format(crosshair.gapSize)
                )
                LabeledSlider(
                    label = "Opacity",
                    value = crosshair.opacity,
                    valueRange = 0.1f..1f,
                    onValueChange = { viewModel.updateCrosshair(crosshair.copy(opacity = it)) },
                    valueText = "%.0f%%".format(crosshair.opacity * 100)
                )
            }

            // Colors
            item {
                SectionHeader("Colors")
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ColorPickerChip(
                        label = "Crosshair",
                        selectedColor = crosshair.color,
                        onColorSelected = { viewModel.updateCrosshair(crosshair.copy(color = it)) }
                    )
                    ColorPickerChip(
                        label = "Outline",
                        selectedColor = crosshair.outlineColor,
                        onColorSelected = { viewModel.updateCrosshair(crosshair.copy(outlineColor = it)) }
                    )
                }
            }

            // Options
            item {
                SectionHeader("Options")
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    ToggleRow("Show Outline", crosshair.showOutline) {
                        viewModel.updateCrosshair(crosshair.copy(showOutline = it))
                    }
                    ToggleRow("Show Dot", crosshair.showDot) {
                        viewModel.updateCrosshair(crosshair.copy(showDot = it))
                    }
                }
            }
        }
    }
}

@Composable
private fun CrosshairPreviewCanvas(cfg: CrosshairConfig) {
    Canvas(modifier = Modifier.size(180.dp, 160.dp)) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val s  = cfg.size * 18f
        val g  = cfg.gapSize
        val strokeW = cfg.thickness
        val outW = strokeW + cfg.outlineThickness * 2f

        val mainColor    = Color(cfg.color).copy(alpha = cfg.opacity)
        val outlineColor = Color(cfg.outlineColor).copy(alpha = cfg.opacity)

        fun stroke(color: Color, width: Float) = androidx.compose.ui.graphics.drawscope.Stroke(width = width)

        when (cfg.style) {
            CrosshairStyle.DOT -> {
                if (cfg.showOutline) drawCircle(outlineColor, cfg.dotSize * 5f + outW, Offset(cx, cy))
                drawCircle(mainColor, cfg.dotSize * 5f, Offset(cx, cy))
            }
            CrosshairStyle.CIRCLE, CrosshairStyle.CIRCLE_DOT -> {
                if (cfg.showOutline) drawCircle(outlineColor, s, Offset(cx, cy), style = stroke(outlineColor, outW))
                drawCircle(mainColor, s, Offset(cx, cy), style = stroke(mainColor, strokeW))
                if (cfg.style == CrosshairStyle.CIRCLE_DOT) drawCircle(mainColor, cfg.dotSize * 3f, Offset(cx, cy))
            }
            else -> {
                // Draw cross
                listOf(
                    Offset(cx - s - g, cy) to Offset(cx - g, cy),
                    Offset(cx + g, cy)     to Offset(cx + s + g, cy),
                    Offset(cx, cy - s - g) to Offset(cx, cy - g),
                    Offset(cx, cy + g)     to Offset(cx, cy + s + g)
                ).forEach { (a, b) ->
                    if (cfg.showOutline) drawLine(outlineColor, a, b, outW, cap = StrokeCap.Round)
                    drawLine(mainColor, a, b, strokeW, cap = StrokeCap.Round)
                }
                if (cfg.showDot || cfg.style == CrosshairStyle.CROSS_DOT)
                    drawCircle(mainColor, cfg.dotSize * 3f, Offset(cx, cy))
            }
        }
    }
}

@Composable
private fun ToggleRow(label: String, checked: Boolean, onToggle: (Boolean) -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Switch(checked = checked, onCheckedChange = onToggle)
    }
}

private val presetColors = listOf(
    0xFFFFFFFF, 0xFF00FF00, 0xFFFF0000, 0xFF00D4FF, 0xFFFF00CC,
    0xFFFFAA00, 0xFF6C63FF, 0xFF00FF88, 0xFFFF6584, 0xFF000000
)

@Composable
private fun ColorPickerChip(label: String, selectedColor: Long, onColorSelected: (Long) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
        Spacer(Modifier.height(4.dp))
        ColorChip(color = selectedColor, label = "#%06X".format(selectedColor and 0xFFFFFF), onClick = { expanded = true })

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            LazyRow(
                modifier = Modifier.padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(presetColors) { color ->
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(color), RoundedCornerShape(50))
                            .border(
                                2.dp,
                                if (selectedColor == color) MaterialTheme.colorScheme.primary else Color.Transparent,
                                RoundedCornerShape(50)
                            )
                            .clickable { onColorSelected(color); expanded = false }
                    )
                }
            }
        }
    }
}
