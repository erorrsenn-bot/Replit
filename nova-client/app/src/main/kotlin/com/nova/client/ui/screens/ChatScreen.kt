package com.nova.client.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import com.nova.client.data.models.ChatMessage
import com.nova.client.data.models.ChatType
import com.nova.client.ui.components.NovaTopBar
import com.nova.client.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ChatScreen(viewModel: MainViewModel, onBack: () -> Unit) {
    val config by viewModel.config.collectAsState()
    var filterText  by remember { mutableStateOf("") }
    var quickInput  by remember { mutableStateOf("") }
    var showFilters by remember { mutableStateOf(false) }

    // Demo messages to show the chat improvements UI
    val demoMessages = remember {
        listOf(
            ChatMessage(sender = "System",    message = "Welcome to the server!",              type = ChatType.SYSTEM),
            ChatMessage(sender = "Steve",     message = "hey anyone wanna pvp?",               type = ChatType.CHAT),
            ChatMessage(sender = "Alex",      message = "nah im busy farming",                 type = ChatType.CHAT),
            ChatMessage(sender = "Notch",     message = "GG on that kill",                    type = ChatType.CHAT),
            ChatMessage(sender = "[Whisper]", message = "Can I have some diamonds?",           type = ChatType.WHISPER),
            ChatMessage(sender = "System",    message = "Player Herobrine has joined",         type = ChatType.SYSTEM),
            ChatMessage(sender = "Herobrine", message = "Hello everyone",                      type = ChatType.CHAT),
            ChatMessage(sender = "You",       message = "/home",                               type = ChatType.COMMAND),
            ChatMessage(sender = "System",    message = "Teleporting to home in 3 seconds...", type = ChatType.SYSTEM),
        )
    }

    val filteredMessages = demoMessages.filter {
        filterText.isEmpty() || it.message.contains(filterText, ignoreCase = true) || it.sender.contains(filterText, ignoreCase = true)
    }

    val listState = rememberLazyListState()
    val quickReplies = config.quickReplies

    Scaffold(
        topBar = {
            NovaTopBar(
                title = "Chat",
                onBack = onBack,
                actions = {
                    IconButton(onClick = { showFilters = !showFilters }) {
                        Icon(Icons.Default.FilterList, "Filter")
                    }
                }
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            // Filter bar
            if (showFilters) {
                OutlinedTextField(
                    value = filterText,
                    onValueChange = { filterText = it },
                    placeholder = { Text("Filter messages…") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    trailingIcon = if (filterText.isNotEmpty()) ({
                        IconButton(onClick = { filterText = "" }) { Icon(Icons.Default.Clear, null) }
                    }) else null
                )
            }

            // Chat messages
            LazyColumn(
                modifier = Modifier.weight(1f),
                state = listState,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(filteredMessages) { msg ->
                    ChatMessageRow(msg, showTimestamp = config.chatTimestamps)
                }
            }

            // Quick replies
            if (quickReplies.isNotEmpty()) {
                androidx.compose.foundation.lazy.LazyRow(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(quickReplies.size) { i ->
                        SuggestionChip(
                            onClick = { quickInput = quickReplies[i] },
                            label = { Text(quickReplies[i], style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }
            }

            // Input row
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = quickInput,
                    onValueChange = { quickInput = it },
                    placeholder = { Text("Type a message…") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp)
                )
                IconButton(
                    onClick = { /* Send over overlay/protocol */ quickInput = "" },
                    enabled = quickInput.isNotBlank()
                ) {
                    Icon(Icons.Default.Send, "Send", tint = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

@Composable
private fun ChatMessageRow(msg: ChatMessage, showTimestamp: Boolean) {
    val fmt = remember { SimpleDateFormat("HH:mm:ss", Locale.getDefault()) }

    val bgColor = when (msg.type) {
        ChatType.SYSTEM  -> MaterialTheme.colorScheme.primary.copy(0.08f)
        ChatType.WHISPER -> Color(0xFFFF6584).copy(0.12f)
        ChatType.COMMAND -> Color(0xFF00D4FF).copy(0.08f)
        else             -> Color.Transparent
    }
    val textColor = when (msg.type) {
        ChatType.SYSTEM  -> MaterialTheme.colorScheme.primary
        ChatType.WHISPER -> Color(0xFFFF6584)
        ChatType.COMMAND -> Color(0xFF00D4FF)
        else             -> MaterialTheme.colorScheme.onBackground
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        if (showTimestamp) {
            Text(
                fmt.format(Date(msg.timestamp)),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onBackground.copy(0.3f),
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(end = 8.dp, top = 2.dp)
            )
        }
        Column {
            if (msg.sender.isNotEmpty() && msg.type != ChatType.SYSTEM) {
                Text(
                    msg.sender,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
            }
            Text(msg.message, style = MaterialTheme.typography.bodySmall, color = textColor)
        }
    }
}
