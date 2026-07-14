package com.nova.client.data.repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.nova.client.data.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import java.io.File

class ConfigRepository(private val context: Context) {

    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()
    private val configFile: File = File(context.filesDir, "nova_config.json")

    private val _config = MutableStateFlow(loadConfig())
    val config: StateFlow<NovaConfig> = _config

    // ── Active profile helpers ────────────────────────────────────────────────
    val activeProfile: NovaProfile
        get() {
            val cfg = _config.value
            return cfg.profiles.find { it.id == cfg.activeProfileId }
                ?: cfg.profiles.firstOrNull()
                ?: NovaProfile()
        }

    // ── Persistence ───────────────────────────────────────────────────────────
    private fun loadConfig(): NovaConfig {
        return try {
            if (configFile.exists()) {
                gson.fromJson(configFile.readText(), NovaConfig::class.java) ?: NovaConfig()
            } else {
                NovaConfig()
            }
        } catch (e: Exception) {
            NovaConfig()
        }
    }

    suspend fun saveConfig(config: NovaConfig) = withContext(Dispatchers.IO) {
        _config.value = config
        configFile.writeText(gson.toJson(config))
    }

    // ── Profile ops ───────────────────────────────────────────────────────────
    suspend fun updateProfile(profile: NovaProfile) {
        val cfg = _config.value
        val updated = cfg.copy(
            profiles = cfg.profiles.map { if (it.id == profile.id) profile else it }
        )
        saveConfig(updated)
    }

    suspend fun addProfile(profile: NovaProfile) {
        val cfg = _config.value
        saveConfig(cfg.copy(profiles = cfg.profiles + profile))
    }

    suspend fun deleteProfile(profileId: String) {
        val cfg = _config.value
        if (cfg.profiles.size <= 1) return
        val updated = cfg.copy(
            profiles = cfg.profiles.filter { it.id != profileId },
            activeProfileId = if (cfg.activeProfileId == profileId)
                cfg.profiles.first { it.id != profileId }.id
            else cfg.activeProfileId
        )
        saveConfig(updated)
    }

    suspend fun switchProfile(profileId: String) {
        saveConfig(_config.value.copy(activeProfileId = profileId))
    }

    // ── HUD ops ───────────────────────────────────────────────────────────────
    suspend fun updateHudElement(element: HudElement) {
        val profile = activeProfile
        val updatedElements = profile.hudElements.map {
            if (it.type == element.type) element else it
        }
        updateProfile(profile.copy(hudElements = updatedElements))
    }

    // ── Crosshair ops ─────────────────────────────────────────────────────────
    suspend fun updateCrosshair(crosshair: CrosshairConfig) {
        updateProfile(activeProfile.copy(crosshair = crosshair))
    }

    // ── Waypoint ops ──────────────────────────────────────────────────────────
    suspend fun addWaypoint(wp: Waypoint) {
        val p = activeProfile
        updateProfile(p.copy(waypoints = p.waypoints + wp))
    }

    suspend fun deleteWaypoint(id: String) {
        val p = activeProfile
        updateProfile(p.copy(waypoints = p.waypoints.filter { it.id != id }))
    }

    // ── Macro ops ─────────────────────────────────────────────────────────────
    suspend fun addMacro(macro: MacroButton) {
        val p = activeProfile
        updateProfile(p.copy(macros = p.macros + macro))
    }

    suspend fun deleteMacro(id: String) {
        val p = activeProfile
        updateProfile(p.copy(macros = p.macros.filter { it.id != id }))
    }

    // ── FPS Booster ops ───────────────────────────────────────────────────────
    suspend fun updateFpsBooster(fps: FpsBoosterConfig) {
        saveConfig(_config.value.copy(fpsBooster = fps))
    }

    // ── Screenshot mode ───────────────────────────────────────────────────────
    suspend fun setScreenshotMode(enabled: Boolean) {
        saveConfig(_config.value.copy(screenshotMode = enabled))
    }
}
