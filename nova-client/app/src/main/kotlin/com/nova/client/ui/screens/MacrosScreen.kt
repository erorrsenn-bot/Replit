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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.nova.client.data.models.MacroButton
import com.nova.client.ui.components.*
import com.nova.client.viewmodel.MainViewModel

@Composable
fun MacrosScreen(viewModel: MainViewModel, onBack: () -> Unit) {
    val macros by viewModel.macros.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { NovaTopBar("Macro Buttons", onBack = onBack) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add macro")
            }
        }
    ) { padding ->
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
                            "Macro buttons send chat commands when tapped. They appear as a floating panel over Minecraft when the HUD overlay is active.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            if (macros.isEmpty()) {
                item {
                    Box(Modifier.fillMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Gamepad, null, Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onBackground.copy(0.3f))
                            Spacer(Modifier.height(12.dp))
                            Text("No macros yet", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onBackground.copy(0.4f))
                            Text("Add quick commands like /home, /spawn", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onBackground.copy(0.3f))
                        }
                    }
                }
            }

            items(macros, key = { it.id }) { macro ->
                MacroCard(macro = macro, onDelete = { viewModel.deleteMacro(macro.id) })
            }
        }
    }

    if (showAddDialog) {
        AddMacroDialog(
            onConfirm = { macro -> viewModel.addMacro(macro); showAddDialog = false },
            onDismiss = { showAddDialog = false }
        )
    }
}

@Composable
private fun MacroCard(macro: MacroButton, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(Color(macro.color), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.PlayArrow, null, tint = Color.White)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(macro.label, fontWeight = FontWeight.Bold)
                Text(
                    macro.command,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontFamily = FontFamily.Monospace
                )
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
private fun AddMacroDialog(onConfirm: (MacroButton) -> Unit, onDismiss: () -> Unit) {
    var label by remember { mutableStateOf("") }
    var command by remember { mutableStateOf("/") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Macro Button") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = label,
                    onValueChange = { label = it },
                    label = { Text("Button label (e.g. Home)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = command,
                    onValueChange = { command = it },
                    label = { Text("Command (e.g. /home)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    "Common: /home /spawn /warp hub /tpa /back",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(0.5f)
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (label.isNotBlank() && command.isNotBlank()) {
                        onConfirm(MacroButton(label = label.trim(), command = command.trim()))
                    }
                },
                enabled = label.isNotBlank() && command.isNotBlank()
            ) { Text("Add") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
