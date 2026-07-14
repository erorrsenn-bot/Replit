package com.nova.client.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.nova.client.data.models.ResourcePack
import com.nova.client.ui.components.NovaTopBar

@Composable
fun ResourcePacksScreen(onBack: () -> Unit) {
    // Demo packs; in a real integration these are loaded from
    // Android/data/com.mojang.minecraftpe/files/games/com.mojang/resource_packs/
    val packs = remember {
        mutableStateListOf(
            ResourcePack("1", "Faithful 32x", "1.0", "Faithful textures in 32x resolution", enabled = true),
            ResourcePack("2", "Vanilla Tweaks", "2.3", "Quality-of-life vanilla improvements", enabled = true),
            ResourcePack("3", "BetterGrass",   "1.1", "Improved grass and foliage textures", enabled = false),
            ResourcePack("4", "PvP Textures",  "3.0", "Optimized low-fire, short swords", enabled = false),
            ResourcePack("5", "Dark UI",       "1.4", "Dark themed inventory and HUD", enabled = false),
        )
    }

    Scaffold(topBar = { NovaTopBar("Resource Pack Manager", onBack = onBack) }) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(0.1f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Manage resource packs for Minecraft Bedrock. Toggle packs here and restart Minecraft to apply changes.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Installed Packs", fontWeight = FontWeight.Bold)
                    Text("${packs.count { it.enabled }} active", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                }
            }

            items(packs, key = { it.id }) { pack ->
                val index = packs.indexOf(pack)
                ResourcePackCard(
                    pack = pack,
                    onToggle = { if (index != -1) packs[index] = pack.copy(enabled = it) },
                    onMoveUp = { if (index > 0) { packs.add(index - 1, packs.removeAt(index)) } },
                    onMoveDown = { if (index < packs.size - 1) { packs.add(index + 1, packs.removeAt(index)) } }
                )
            }
        }
    }
}

@Composable
private fun ResourcePackCard(
    pack: ResourcePack,
    onToggle: (Boolean) -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(MaterialTheme.colorScheme.primary.copy(0.15f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Archive, null, tint = MaterialTheme.colorScheme.primary)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(pack.name, fontWeight = FontWeight.SemiBold)
                Text("v${pack.version}  ·  ${pack.description}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(0.55f))
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(onClick = onMoveUp, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.KeyboardArrowUp, null, modifier = Modifier.size(18.dp))
                }
                IconButton(onClick = onMoveDown, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.KeyboardArrowDown, null, modifier = Modifier.size(18.dp))
                }
            }
            Switch(checked = pack.enabled, onCheckedChange = onToggle)
        }
    }
}
