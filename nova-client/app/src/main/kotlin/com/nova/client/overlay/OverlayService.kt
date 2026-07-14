package com.nova.client.overlay

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager
import androidx.core.app.NotificationCompat
import com.nova.client.MainActivity
import com.nova.client.NovaApplication
import com.nova.client.R
import com.nova.client.data.models.NovaConfig

/**
 * Foreground service that draws the Nova HUD as a system-level overlay
 * on top of Minecraft (or any app). Requires SYSTEM_ALERT_WINDOW permission.
 *
 * Architecture:
 *   OverlayService  ─── manages lifecycle & WindowManager
 *   HudOverlayView  ─── single Canvas-based view containing all HUD elements
 */
class OverlayService : Service() {

    private lateinit var windowManager: WindowManager
    private var hudView: HudOverlayView? = null

    // Current config snapshot pushed from outside
    private var config: NovaConfig = NovaConfig()
    private var screenshotMode: Boolean = false

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        startForeground(NOTIFICATION_ID, buildNotification())
        attachHudView()
        instance = this
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Re-attach if killed
        if (hudView == null) attachHudView()
        return START_STICKY
    }

    override fun onDestroy() {
        instance = null
        detachHudView()
        super.onDestroy()
    }

    // ── HUD View lifecycle ────────────────────────────────────────────────────
    private fun attachHudView() {
        if (hudView != null) return

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                    or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
        }

        val view = HudOverlayView(this)
        view.applyConfig(
            NovaApplication.instance.configRepository.config.value,
            screenshotMode
        )
        windowManager.addView(view, params)
        hudView = view
    }

    private fun detachHudView() {
        hudView?.let {
            try { windowManager.removeView(it) } catch (_: Exception) {}
        }
        hudView = null
    }

    // ── Config updates ────────────────────────────────────────────────────────
    fun applyNewConfig(newConfig: NovaConfig) {
        config = newConfig
        hudView?.applyConfig(newConfig, screenshotMode)
    }

    fun applyScreenshotMode(enabled: Boolean) {
        screenshotMode = enabled
        hudView?.applyConfig(config, enabled)
    }

    // ── Notification ──────────────────────────────────────────────────────────
    private fun buildNotification(): Notification {
        val openIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )
        val stopIntent = PendingIntent.getService(
            this, 1,
            Intent(this, OverlayService::class.java).apply { action = ACTION_STOP },
            PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, NovaApplication.OVERLAY_CHANNEL_ID)
            .setContentTitle("Nova HUD Active")
            .setContentText("Tap to open Nova Client")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentIntent(openIntent)
            .addAction(android.R.drawable.ic_delete, "Stop", stopIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()
    }

    // ── Companion / static helpers ────────────────────────────────────────────
    companion object {
        const val ACTION_STOP = "com.nova.client.STOP_OVERLAY"
        const val NOTIFICATION_ID = 1001

        @Volatile
        private var instance: OverlayService? = null

        fun updateConfig(config: NovaConfig) {
            instance?.applyNewConfig(config)
        }

        fun setScreenshotMode(enabled: Boolean) {
            instance?.applyScreenshotMode(enabled)
        }

        fun isRunning(): Boolean = instance != null
    }
}
