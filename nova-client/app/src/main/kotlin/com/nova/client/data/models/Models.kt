package com.nova.client.data.models

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize

// ── Themes ────────────────────────────────────────────────────────────────────
enum class NovaTheme(val label: String) {
    DARK("Dark"),
    NEON("Neon"),
    AMOLED("AMOLED"),
    MINIMAL("Minimal")
}

// ── HUD Elements ─────────────────────────────────────────────────────────────
enum class HudElementType(val label: String, val defaultEnabled: Boolean) {
    CPS_COUNTER("CPS Counter", true),
    COORDINATES("Coordinates", true),
    ARMOR_STATUS("Armor Status", true),
    POTION_EFFECTS("Potion Effects", false),
    KEYSTROKES("Keystrokes", true),
    CROSSHAIR("Crosshair", true),
    FPS_DISPLAY("FPS Display", true),
    CHAT_TIMESTAMPS("Chat Timestamps", false)
}

data class HudElement(
    val type: HudElementType,
    var enabled: Boolean = type.defaultEnabled,
    var position: Offset = Offset.Zero,        // fraction of screen 0..1
    var scale: Float = 1.0f,
    var opacity: Float = 1.0f
)

// ── Crosshair ─────────────────────────────────────────────────────────────────
enum class CrosshairStyle(val label: String) {
    DEFAULT("Default"),
    DOT("Dot"),
    CIRCLE("Circle"),
    CIRCLE_DOT("Circle + Dot"),
    CROSS("Cross"),
    CROSS_DOT("Cross + Dot"),
    SQUARE("Square"),
    TRIANGLE("Triangle"),
    STAR("Star"),
    ARROW("Arrow"),
    HEART("Heart"),
    CUSTOM("Custom")
}

data class CrosshairConfig(
    val style: CrosshairStyle = CrosshairStyle.DEFAULT,
    val color: Long = 0xFFFFFFFF,             // ARGB packed
    val outlineColor: Long = 0xFF000000,
    val size: Float = 1.0f,
    val thickness: Float = 2.0f,
    val outlineThickness: Float = 1.0f,
    val showOutline: Boolean = true,
    val showDot: Boolean = false,
    val dotSize: Float = 2.0f,
    val gapSize: Float = 4.0f,
    val opacity: Float = 1.0f
)

// ── Waypoints ────────────────────────────────────────────────────────────────
data class Waypoint(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val x: Int,
    val y: Int,
    val z: Int,
    val dimension: String = "overworld",
    val color: Long = 0xFFFF5500,
    val enabled: Boolean = true
)

// ── Macro Buttons ─────────────────────────────────────────────────────────────
data class MacroButton(
    val id: String = java.util.UUID.randomUUID().toString(),
    val label: String,
    val command: String,          // e.g. "/home", "/spawn"
    val color: Long = 0xFF6C63FF,
    val enabled: Boolean = true
)

// ── Resource Packs ────────────────────────────────────────────────────────────
data class ResourcePack(
    val id: String,
    val name: String,
    val version: String,
    val description: String,
    val enabled: Boolean = false,
    val filePath: String = ""
)

// ── Profile ───────────────────────────────────────────────────────────────────
data class NovaProfile(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String = "Default",
    val theme: NovaTheme = NovaTheme.DARK,
    val hudElements: List<HudElement> = HudElementType.entries.map { HudElement(it) },
    val crosshair: CrosshairConfig = CrosshairConfig(),
    val waypoints: List<Waypoint> = emptyList(),
    val macros: List<MacroButton> = emptyList()
)

// ── FPS Booster ───────────────────────────────────────────────────────────────
data class FpsBoosterConfig(
    val enabled: Boolean = false,
    val renderDistance: Int = 4,              // 4-16 chunks
    val particleReduction: Boolean = true,
    val smoothLighting: Boolean = false,
    val fancyGraphics: Boolean = false,
    val cloudLevel: Int = 0                  // 0=off, 1=fast, 2=fancy
)

// ── Server Ping Result ────────────────────────────────────────────────────────
data class ServerStatus(
    val address: String,
    val port: Int = 19132,
    val online: Boolean = false,
    val motd: String = "",
    val playerCount: Int = 0,
    val maxPlayers: Int = 0,
    val pingMs: Long = -1,
    val gameMode: String = "",
    val protocolVersion: Int = 0
)

// ── Chat Message ──────────────────────────────────────────────────────────────
data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val type: ChatType = ChatType.CHAT
)

enum class ChatType { CHAT, SYSTEM, WHISPER, COMMAND }

// ── Config (persisted to JSON) ────────────────────────────────────────────────
data class NovaConfig(
    val version: Int = 1,
    val activeProfileId: String = "",
    val profiles: List<NovaProfile> = listOf(NovaProfile()),
    val fpsBooster: FpsBoosterConfig = FpsBoosterConfig(),
    val chatTimestamps: Boolean = true,
    val chatFilter: String = "",
    val quickReplies: List<String> = listOf("/home", "/spawn", "/warp hub", "/tpa"),
    val screenshotMode: Boolean = false,
    val replayEnabled: Boolean = false
)
