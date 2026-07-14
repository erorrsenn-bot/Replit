package com.nova.client.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.nova.client.data.models.HudElement
import com.nova.client.data.models.HudElementType
import com.nova.client.ui.components.*
import com.nova.client.viewmodel.MainViewModel

@Composable
fun HudEditorScreen(viewModel: MainViewModel, onBack: () -> Unit) {
    val hudElements by viewModel.hudElements.collectAsState()
    var previewMode by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            NovaTopBar(
                title = "HUD Editor",
                onBack = onBack,
                actions = {
                    IconButton(onClick = { previewMode = !previewMode }) {
                        Icon(
                            if (previewMode) Icons.Default.Edit else Icons.Default.Visibility,
                            contentDescription = "Toggle preview"
                        )
                    }
                }
            )
        }
    ) { padding ->
        if (previewMode) {
            // Interactive drag preview
            HudDragPreview(
                elements = hudElements,
                modifier = Modifier.fillMaxSize().padding(padding),
                onPositionChange = { type, x, y -> viewModel.updateHudPosition(type, x, y) }
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Info, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Toggle elements below. Tap Preview (▶) to drag them on screen.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                item { SectionHeader("HUD Elements") }

                items(hudElements, key = { it.type.name }) { element ->
                    HudElementRow(
                        element = element,
                        onToggle = { viewModel.toggleHudElement(element.type, it) },
                        onScaleChange = { viewModel.updateHudScale(element.type, it) }
                    )
                }
            }
        }
    }
}

@Composable
private fun HudElementRow(
    element: HudElement,
    onToggle: (Boolean) -> Unit,
    onScaleChange: (Float) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(element.type.label, fontWeight = FontWeight.SemiBold)
                    Text(
                        if (element.enabled) "Enabled · Scale ${String.format("%.1f", element.scale)}x" else "Disabled",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Switch(
                    checked = element.enabled,
                    onCheckedChange = onToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.primary
                    )
                )
            }

            if (expanded) {
                Spacer(Modifier.height(8.dp))
                LabeledSlider(
                    label = "Scale",
                    value = element.scale,
                    valueRange = 0.5f..3.0f,
                    onValueChange = onScaleChange,
                    valueText = "${String.format("%.1f", element.scale)}x"
                )
            }
        }
    }
}

@Composable
private fun HudDragPreview(
    elements: List<HudElement>,
    modifier: Modifier,
    onPositionChange: (HudElementType, Float, Float) -> Unit
) {
    var canvasWidth by remember { mutableStateOf(0f) }
    var canvasHeight by remember { mutableStateOf(0f) }

    Box(
        modifier = modifier
            .background(Color.Black.copy(alpha = 0.85f))
            .onSizeChanged {
                canvasWidth = it.width.toFloat()
                canvasHeight = it.height.toFloat()
            }
    ) {
        // Grid lines
        Text(
            "Drag elements to reposition. Tap the back arrow when done.",
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.4f),
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 8.dp)
        )

        elements.filter { it.enabled }.forEach { element ->
            var offsetX by remember(element.type) { mutableStateOf(element.position.x * canvasWidth) }
            var offsetY by remember(element.type) { mutableStateOf(element.position.y * canvasHeight) }

            Box(
                modifier = Modifier
                    .offset { IntOffset(offsetX.toInt(), offsetY.toInt()) }
                    .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(6.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                    .padding(6.dp)
                    .pointerInput(element.type) {
                        detectDragGestures(
                            onDragEnd = {
                                if (canvasWidth > 0 && canvasHeight > 0) {
                                    onPositionChange(
                                        element.type,
                                        (offsetX / canvasWidth).coerceIn(0f, 1f),
                                        (offsetY / canvasHeight).coerceIn(0f, 1f)
                                    )
                                }
                            }
                        ) { _, dragAmount ->
                            offsetX = (offsetX + dragAmount.x).coerceIn(0f, canvasWidth - 80f)
                            offsetY = (offsetY + dragAmount.y).coerceIn(0f, canvasHeight - 40f)
                        }
                    }
            ) {
                Text(
                    element.type.label,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
