package com.nova.client.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nova.client.data.models.NovaTheme
import com.nova.client.ui.components.NovaTopBar
import com.nova.client.viewmodel.MainViewModel

private data class ThemePreview(
    val theme: NovaTheme,
    val bg: Color,
    val surface: Color,
    val primary: Color,
    val accent: Color,
    val description: String
)

@Composable
fun ThemeScreen(viewModel: MainViewModel, onBack: () -> Unit) {
    val currentTheme by viewModel.currentTheme.collectAsState()

    val themes = listOf(
        ThemePreview(NovaTheme.DARK,    Color(0xFF0D0D0D), Color(0xFF1A1A1A), Color(0xFF6C63FF), Color(0xFF00D4FF), "Classic dark with purple accent"),
        ThemePreview(NovaTheme.NEON,    Color(0xFF050510), Color(0xFF0A0A20), Color(0xFF00FF88), Color(0xFFFF00CC), "Cyberpunk neon glow"),
        ThemePreview(NovaTheme.AMOLED,  Color(0xFF000000), Color(0xFF0A0A0A), Color(0xFFFFFFFF), Color(0xFF888888), "True black for OLED screens"),
        ThemePreview(NovaTheme.MINIMAL, Color(0xFFF5F5F5), Color(0xFFFFFFFF), Color(0xFF1A1A2E), Color(0xFF0F3460), "Clean light minimalist")
    )

    Scaffold(topBar = { NovaTopBar("Theme Engine", onBack = onBack) }) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    "Choose your client theme. Applied instantly across all screens.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }

            items(themes) { preview ->
                ThemeCard(
                    preview  = preview,
                    selected = currentTheme == preview.theme,
                    onClick  = { viewModel.setTheme(preview.theme) }
                )
            }
        }
    }
}

@Composable
private fun ThemeCard(preview: ThemePreview, selected: Boolean, onClick: () -> Unit) {
    val borderColor = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .border(2.dp, borderColor, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = if (selected) 4.dp else 0.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            // Mini preview
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(preview.bg)
                    .padding(12.dp)
            ) {
                // Fake UI elements in theme colors
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        Modifier
                            .fillMaxWidth(0.6f)
                            .height(14.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(preview.primary)
                    )
                    Box(
                        Modifier
                            .fillMaxWidth(0.9f)
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(preview.surface)
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(
                            Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(preview.primary)
                        )
                        Box(
                            Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(preview.accent)
                        )
                        Box(
                            Modifier
                                .weight(1f)
                                .height(28.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(preview.surface)
                        )
                    }
                }

                if (selected) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(28.dp)
                            .background(preview.primary, RoundedCornerShape(50))
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Check, null, tint = Color.Black, modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(preview.theme.label, fontWeight = FontWeight.Bold)
                    Text(
                        preview.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(preview.bg, preview.surface, preview.primary, preview.accent).forEach { c ->
                        Box(
                            Modifier
                                .size(16.dp)
                                .background(c, RoundedCornerShape(50))
                                .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(50))
                        )
                    }
                }
            }
        }
    }
}
