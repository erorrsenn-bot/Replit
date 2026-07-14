package com.nova.client.ui.screens

import android.provider.Settings
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.nova.client.data.models.NovaTheme
import com.nova.client.ui.components.*
import com.nova.client.ui.navigation.Routes
import com.nova.client.viewmodel.MainViewModel

private data class HomeFeature(
    val title: String,
    val icon: ImageVector,
    val route: String,
    val tint: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onNavigate: (String) -> Unit,
    onRequestOverlayPermission: () -> Unit
) {
    val context = LocalContext.current
    val overlayActive by viewModel.overlayActive.collectAsState()
    val config by viewModel.config.collectAsState()
    val profile = viewModel.activeProfile

    val features = listOf(
        HomeFeature("HUD Editor",       Icons.Default.Dashboard,     Routes.HUD_EDITOR,    Color(0xFF6C63FF)),
        HomeFeature("Theme Engine",     Icons.Default.Palette,       Routes.THEME,         Color(0xFFFF6584)),
        HomeFeature("Crosshair",        Icons.Default.GpsFixed,      Routes.CROSSHAIR,     Color(0xFF00D4FF)),
        HomeFeature("FPS Booster",      Icons.Default.Speed,         Routes.FPS_BOOSTER,   Color(0xFF00CC66)),
        HomeFeature("Waypoints",        Icons.Default.LocationOn,    Routes.WAYPOINTS,     Color(0xFFFFAA00)),
        HomeFeature("Macro Buttons",    Icons.Default.Gamepad,       Routes.MACROS,        Color(0xFFFF6584)),
        HomeFeature("Resource Packs",   Icons.Default.Archive,       Routes.RESOURCE_PACKS,Color(0xFF8BC34A)),
        HomeFeature("Chat",             Icons.Default.Chat,          Routes.CHAT,          Color(0xFF00BCD4)),
        HomeFeature("Server Ping",      Icons.Default.Wifi,          Routes.SERVER_PING,   Color(0xFF9C27B0)),
        HomeFeature("Settings",         Icons.Default.Settings,      Routes.SETTINGS,      Color(0xFF607D8B)),
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Nova Client", fontWeight = FontWeight.Black, fontSize = 22.sp)
                        Text(
                            "Profile: ${profile.name}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { onNavigate(Routes.SETTINGS) }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ── Overlay control card ──────────────────────────────────────────
            item {
                OverlayControlCard(
                    overlayActive = overlayActive,
                    hasPermission = viewModel.hasOverlayPermission(),
                    onStart = { viewModel.startOverlay() },
                    onStop  = { viewModel.stopOverlay() },
                    onRequestPermission = onRequestOverlayPermission
                )
            }

            // ── Profile switcher ──────────────────────────────────────────────
            item {
                SectionHeader("Profiles")
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(config.profiles) { p ->
                        FilterChip(
                            selected = p.id == config.activeProfileId,
                            onClick  = { viewModel.switchProfile(p.id) },
                            label    = { Text(p.name) },
                            leadingIcon = if (p.id == config.activeProfileId) ({
                                Icon(Icons.Default.Check, null, Modifier.size(16.dp))
                            }) else null
                        )
                    }
                    item {
                        var showDialog by remember { mutableStateOf(false) }
                        AssistChip(
                            onClick = { showDialog = true },
                            label   = { Text("+ New") },
                            leadingIcon = { Icon(Icons.Default.Add, null, Modifier.size(16.dp)) }
                        )
                        if (showDialog) {
                            NewProfileDialog(
                                onConfirm = { name -> viewModel.addProfile(name); showDialog = false },
                                onDismiss = { showDialog = false }
                            )
                        }
                    }
                }
            }

            // ── Feature grid ──────────────────────────────────────────────────
            item { SectionHeader("Features") }

            items(features) { feature ->
                FeatureCard(
                    icon     = feature.icon,
                    title    = feature.title,
                    subtitle = featureSubtitle(feature.title),
                    enabled  = true,
                    tint     = feature.tint,
                    onClick  = { onNavigate(feature.route) }
                )
            }

            // ── Footer ────────────────────────────────────────────────────────
            item {
                Text(
                    text = "Nova Client v1.0 · Bedrock Edition · Open-source",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
                    modifier = Modifier.padding(top = 8.dp, bottom = 16.dp).fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun OverlayControlCard(
    overlayActive: Boolean,
    hasPermission: Boolean,
    onStart: () -> Unit,
    onStop: () -> Unit,
    onRequestPermission: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (overlayActive)
                MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
            else
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    if (overlayActive) Icons.Default.LayersClear else Icons.Default.Layers,
                    contentDescription = null,
                    tint = if (overlayActive) Color(0xFF00CC66) else MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("HUD Overlay", fontWeight = FontWeight.Bold)
                    Text(
                        if (overlayActive) "Running over Minecraft" else "Tap to start overlay",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            if (!hasPermission) {
                OutlinedButton(
                    onClick = onRequestPermission,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.AdminPanelSettings, null, Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Grant Overlay Permission")
                }
            } else {
                OverlayStatusBadge(
                    active = overlayActive,
                    onClick = if (overlayActive) onStop else onStart
                )
            }
        }
    }
}

@Composable
private fun NewProfileDialog(onConfirm: (String) -> Unit, onDismiss: () -> Unit) {
    var name by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Profile") },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Profile name") },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(onClick = { if (name.isNotBlank()) onConfirm(name.trim()) }, enabled = name.isNotBlank()) {
                Text("Create")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

private fun featureSubtitle(title: String) = when (title) {
    "HUD Editor"     -> "Move, resize, and toggle HUD elements"
    "Theme Engine"   -> "Dark, Neon, AMOLED, Minimal"
    "Crosshair"      -> "Hundreds of styles, colors, sizes"
    "FPS Booster"    -> "Render distance & particle optimization"
    "Waypoints"      -> "Save and navigate to locations"
    "Macro Buttons"  -> "Quick chat commands"
    "Resource Packs" -> "Enable, disable, organize packs"
    "Chat"           -> "Timestamps, filters, quick replies"
    "Server Ping"    -> "Check Bedrock server status"
    "Settings"       -> "App configuration and profiles"
    else             -> ""
}
