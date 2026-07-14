package com.nova.client.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.nova.client.data.models.ServerStatus
import com.nova.client.ui.components.*
import com.nova.client.viewmodel.MainViewModel

@Composable
fun ServerPingScreen(viewModel: MainViewModel, onBack: () -> Unit) {
    val serverStatus by viewModel.serverStatus.collectAsState()
    val pingLoading  by viewModel.pingLoading.collectAsState()
    var address by remember { mutableStateOf("") }
    var port    by remember { mutableStateOf("19132") }

    val quickServers = listOf(
        "play.nethergames.org",
        "play.hivemc.com",
        "mco.mineplex.com",
        "play.cubecraft.net",
        "sg.mineheroes.net"
    )

    Scaffold(topBar = { NovaTopBar("Server Ping", onBack = onBack) }) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Ping a Bedrock Server", fontWeight = FontWeight.Bold)

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = address,
                                onValueChange = { address = it },
                                label = { Text("Server address") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = port,
                                onValueChange = { port = it },
                                label = { Text("Port") },
                                singleLine = true,
                                modifier = Modifier.width(88.dp)
                            )
                        }

                        Button(
                            onClick = {
                                if (address.isNotBlank()) {
                                    viewModel.pingServer(address.trim(), port.toIntOrNull() ?: 19132)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = address.isNotBlank() && !pingLoading,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            if (pingLoading) {
                                CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp, color = Color.White)
                                Spacer(Modifier.width(8.dp))
                            } else {
                                Icon(Icons.Default.Wifi, null, Modifier.size(18.dp))
                                Spacer(Modifier.width(8.dp))
                            }
                            Text(if (pingLoading) "Pinging…" else "Ping Server")
                        }
                    }
                }
            }

            // Quick server chips
            item {
                SectionHeader("Popular Bedrock Servers")
                androidx.compose.foundation.lazy.LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(quickServers.size) { i ->
                        SuggestionChip(
                            onClick = {
                                address = quickServers[i]
                                viewModel.pingServer(quickServers[i])
                            },
                            label = { Text(quickServers[i], style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }
            }

            // Result card
            serverStatus?.let { status ->
                item { ServerStatusCard(status) }
            }
        }
    }
}

@Composable
private fun ServerStatusCard(status: ServerStatus) {
    val online = status.online
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (online) Color(0xFF00CC66).copy(0.1f) else MaterialTheme.colorScheme.errorContainer.copy(0.4f)
        )
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    if (online) Icons.Default.CheckCircle else Icons.Default.Cancel,
                    contentDescription = null,
                    tint = if (online) Color(0xFF00CC66) else MaterialTheme.colorScheme.error
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "${status.address}:${status.port}",
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(0.2f))

            if (online) {
                if (status.motd.isNotEmpty()) {
                    Text(status.motd, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                }
                InfoRow("Status",   "Online", Color(0xFF00CC66))
                InfoRow("Players",  "${status.playerCount} / ${status.maxPlayers}")
                InfoRow("Ping",     "${status.pingMs} ms",
                    when {
                        status.pingMs < 80  -> Color(0xFF00CC66)
                        status.pingMs < 150 -> Color(0xFFFFAA00)
                        else                -> Color(0xFFFF4444)
                    }
                )
                if (status.gameMode.isNotEmpty()) InfoRow("Mode", status.gameMode)
                if (status.protocolVersion > 0) InfoRow("Protocol", status.protocolVersion.toString())
            } else {
                InfoRow("Status", "Offline / Unreachable", MaterialTheme.colorScheme.error)
            }
        }
    }
}
