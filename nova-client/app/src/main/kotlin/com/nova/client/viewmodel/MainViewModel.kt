package com.nova.client.viewmodel

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nova.client.NovaApplication
import com.nova.client.data.models.*
import com.nova.client.network.BedrockPing
import com.nova.client.overlay.OverlayService
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = NovaApplication.instance.configRepository

    val config: StateFlow<NovaConfig> = repo.config
    val activeProfile: NovaProfile get() = repo.activeProfile

    val currentTheme: StateFlow<NovaTheme> = config.map { cfg ->
        cfg.profiles.find { it.id == cfg.activeProfileId }?.theme
            ?: cfg.profiles.firstOrNull()?.theme
            ?: NovaTheme.DARK
    }.stateIn(viewModelScope, SharingStarted.Eagerly, NovaTheme.DARK)

    // ── Overlay state ────────────────────────────────────────────────────────
    private val _overlayActive = MutableStateFlow(false)
    val overlayActive: StateFlow<Boolean> = _overlayActive

    // ── Server ping ───────────────────────────────────────────────────────────
    private val _serverStatus = MutableStateFlow<ServerStatus?>(null)
    val serverStatus: StateFlow<ServerStatus?> = _serverStatus

    private val _pingLoading = MutableStateFlow(false)
    val pingLoading: StateFlow<Boolean> = _pingLoading

    // ── HUD elements ──────────────────────────────────────────────────────────
    val hudElements: StateFlow<List<HudElement>> = config.map {
        repo.activeProfile.hudElements
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // ── Crosshair ─────────────────────────────────────────────────────────────
    val crosshairConfig: StateFlow<CrosshairConfig> = config.map {
        repo.activeProfile.crosshair
    }.stateIn(viewModelScope, SharingStarted.Eagerly, CrosshairConfig())

    // ── Waypoints ─────────────────────────────────────────────────────────────
    val waypoints: StateFlow<List<Waypoint>> = config.map {
        repo.activeProfile.waypoints
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // ── Macros ────────────────────────────────────────────────────────────────
    val macros: StateFlow<List<MacroButton>> = config.map {
        repo.activeProfile.macros
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // ── FPS Booster ───────────────────────────────────────────────────────────
    val fpsBooster: StateFlow<FpsBoosterConfig> = config.map { it.fpsBooster }
        .stateIn(viewModelScope, SharingStarted.Eagerly, FpsBoosterConfig())

    // ── Overlay control ───────────────────────────────────────────────────────
    fun startOverlay() {
        val ctx = getApplication<Application>()
        if (!Settings.canDrawOverlays(ctx)) return
        ctx.startForegroundService(Intent(ctx, OverlayService::class.java))
        _overlayActive.value = true
    }

    fun stopOverlay() {
        val ctx = getApplication<Application>()
        ctx.stopService(Intent(ctx, OverlayService::class.java))
        _overlayActive.value = false
    }

    fun hasOverlayPermission(): Boolean =
        Settings.canDrawOverlays(getApplication())

    // ── Profile ops ───────────────────────────────────────────────────────────
    fun switchProfile(id: String) = viewModelScope.launch {
        repo.switchProfile(id)
    }

    fun addProfile(name: String) = viewModelScope.launch {
        val profile = NovaProfile(name = name)
        repo.addProfile(profile)
    }

    fun deleteProfile(id: String) = viewModelScope.launch {
        repo.deleteProfile(id)
    }

    // ── HUD ops ───────────────────────────────────────────────────────────────
    fun toggleHudElement(type: HudElementType, enabled: Boolean) = viewModelScope.launch {
        val current = repo.activeProfile.hudElements.find { it.type == type }
            ?: HudElement(type)
        repo.updateHudElement(current.copy(enabled = enabled))
        OverlayService.updateConfig(repo.config.value)
    }

    fun updateHudPosition(type: HudElementType, x: Float, y: Float) = viewModelScope.launch {
        val el = repo.activeProfile.hudElements.find { it.type == type }
            ?: HudElement(type)
        repo.updateHudElement(el.copy(position = androidx.compose.ui.geometry.Offset(x, y)))
        OverlayService.updateConfig(repo.config.value)
    }

    fun updateHudScale(type: HudElementType, scale: Float) = viewModelScope.launch {
        val el = repo.activeProfile.hudElements.find { it.type == type }
            ?: HudElement(type)
        repo.updateHudElement(el.copy(scale = scale.coerceIn(0.5f, 3.0f)))
    }

    // ── Crosshair ops ─────────────────────────────────────────────────────────
    fun updateCrosshair(cfg: CrosshairConfig) = viewModelScope.launch {
        repo.updateCrosshair(cfg)
        OverlayService.updateConfig(repo.config.value)
    }

    // ── Waypoints ─────────────────────────────────────────────────────────────
    fun addWaypoint(wp: Waypoint) = viewModelScope.launch { repo.addWaypoint(wp) }
    fun deleteWaypoint(id: String) = viewModelScope.launch { repo.deleteWaypoint(id) }

    // ── Macros ────────────────────────────────────────────────────────────────
    fun addMacro(macro: MacroButton) = viewModelScope.launch { repo.addMacro(macro) }
    fun deleteMacro(id: String) = viewModelScope.launch { repo.deleteMacro(id) }

    // ── FPS Booster ───────────────────────────────────────────────────────────
    fun updateFpsBooster(fps: FpsBoosterConfig) = viewModelScope.launch {
        repo.updateFpsBooster(fps)
    }

    // ── Screenshot mode ───────────────────────────────────────────────────────
    fun toggleScreenshotMode(enabled: Boolean) = viewModelScope.launch {
        repo.setScreenshotMode(enabled)
        OverlayService.setScreenshotMode(enabled)
    }

    // ── Theme ops ─────────────────────────────────────────────────────────────
    fun setTheme(theme: NovaTheme) = viewModelScope.launch {
        val profile = repo.activeProfile
        repo.updateProfile(profile.copy(theme = theme))
    }

    // ── Server ping ───────────────────────────────────────────────────────────
    fun pingServer(address: String, port: Int = 19132) = viewModelScope.launch {
        _pingLoading.value = true
        _serverStatus.value = BedrockPing.ping(address, port)
        _pingLoading.value = false
    }
}
