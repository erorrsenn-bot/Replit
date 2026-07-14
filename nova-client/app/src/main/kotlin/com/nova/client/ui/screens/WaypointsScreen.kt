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
import com.nova.client.data.models.Waypoint
import com.nova.client.ui.components.*
import com.nova.client.viewmodel.MainViewModel

@Composable
fun WaypointsScreen(viewModel: MainViewModel, onBack: () -> Unit) {
    val waypoints by viewModel.waypoints.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { NovaTopBar("Waypoints", onBack = onBack) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add waypoint")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (waypoints.isEmpty()) {
                item {
                    Box(Modifier.fillMaxWidth().padding(top = 60.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.LocationOff, null, Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onBackground.copy(0.3f))
                            Spacer(Modifier.height(12.dp))
                            Text("No waypoints yet", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onBackground.copy(0.4f))
                            Text("Tap + to save your first location", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onBackground.copy(0.3f))
                        }
                    }
                }
            }

            items(waypoints, key = { it.id }) { wp ->
                WaypointCard(wp = wp, onDelete = { viewModel.deleteWaypoint(wp.id) })
            }
        }
    }

    if (showAddDialog) {
        AddWaypointDialog(
            onConfirm = { wp -> viewModel.addWaypoint(wp); showAddDialog = false },
            onDismiss = { showAddDialog = false }
        )
    }
}

@Composable
private fun WaypointCard(wp: Waypoint, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(wp.color), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.LocationOn, null, tint = Color.White, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(wp.name, fontWeight = FontWeight.SemiBold)
                Text(
                    "X: ${wp.x}  Y: ${wp.y}  Z: ${wp.z}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                )
                Text(
                    wp.dimension.replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
private fun AddWaypointDialog(onConfirm: (Waypoint) -> Unit, onDismiss: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var x by remember { mutableStateOf("0") }
    var y by remember { mutableStateOf("64") }
    var z by remember { mutableStateOf("0") }
    var dimension by remember { mutableStateOf("overworld") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Waypoint") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = x, onValueChange = { x = it }, label = { Text("X") }, singleLine = true, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = y, onValueChange = { y = it }, label = { Text("Y") }, singleLine = true, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = z, onValueChange = { z = it }, label = { Text("Z") }, singleLine = true, modifier = Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("overworld", "nether", "end").forEach { dim ->
                        FilterChip(
                            selected = dimension == dim,
                            onClick = { dimension = dim },
                            label = { Text(dim.replaceFirstChar { it.uppercase() }) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank()) {
                        onConfirm(Waypoint(
                            name = name.trim(),
                            x = x.toIntOrNull() ?: 0,
                            y = y.toIntOrNull() ?: 64,
                            z = z.toIntOrNull() ?: 0,
                            dimension = dimension
                        ))
                    }
                },
                enabled = name.isNotBlank()
            ) { Text("Add") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
