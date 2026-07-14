package com.nova.client.overlay

import android.content.Context
import android.graphics.*
import android.os.SystemClock
import android.view.MotionEvent
import android.view.View
import com.nova.client.data.models.*
import com.nova.client.overlay.elements.*
import kotlin.math.roundToInt

/**
 * A full-screen transparent Canvas view that draws all HUD elements.
 * Runs on the main thread; redraws via Choreographer at display refresh rate.
 */
class HudOverlayView(context: Context) : View(context) {

    private var config: NovaConfig = NovaConfig()
    private var screenshotMode: Boolean = false

    // Subsystem renderers
    private val cpsCounter    = CpsCounterElement()
    private val coordsOverlay = CoordinatesElement()
    private val armorStatus   = ArmorStatusElement()
    private val potionEffects = PotionEffectsElement()
    private val keystrokes    = KeystrokesElement()
    private val crosshair     = CrosshairElement()
    private val fpsDisplay    = FpsDisplayElement()

    // FPS tracking
    private var frameCount = 0
    private var fpsLastTime = SystemClock.elapsedRealtime()
    private var currentFps = 0

    init {
        setLayerType(LAYER_TYPE_HARDWARE, null)
        // Trigger continuous redraws
        postInvalidateOnAnimation()
    }

    fun applyConfig(newConfig: NovaConfig, screenshot: Boolean) {
        config = newConfig
        screenshotMode = screenshot
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Update FPS
        frameCount++
        val now = SystemClock.elapsedRealtime()
        if (now - fpsLastTime >= 1000L) {
            currentFps = frameCount
            frameCount = 0
            fpsLastTime = now
        }

        if (screenshotMode) {
            postInvalidateOnAnimation()
            return   // hide everything for clean screenshots
        }

        val w = width.toFloat()
        val h = height.toFloat()
        val profile = config.profiles.find { it.id == config.activeProfileId }
            ?: config.profiles.firstOrNull()
            ?: return

        profile.hudElements.filter { it.enabled }.forEach { element ->
            val px = element.position.x * w
            val py = element.position.y * h
            canvas.save()
            canvas.translate(px, py)
            canvas.scale(element.scale, element.scale)

            when (element.type) {
                HudElementType.CPS_COUNTER    -> cpsCounter.draw(canvas)
                HudElementType.COORDINATES    -> coordsOverlay.draw(canvas)
                HudElementType.ARMOR_STATUS   -> armorStatus.draw(canvas)
                HudElementType.POTION_EFFECTS -> potionEffects.draw(canvas)
                HudElementType.KEYSTROKES     -> keystrokes.draw(canvas)
                HudElementType.CROSSHAIR      -> crosshair.draw(canvas, profile.crosshair, w / element.scale, h / element.scale)
                HudElementType.FPS_DISPLAY    -> fpsDisplay.draw(canvas, currentFps)
                HudElementType.CHAT_TIMESTAMPS -> {} // handled by chat layer
            }

            canvas.restore()
        }

        postInvalidateOnAnimation()
    }

    // ── Touch pass-through: CPS counter registers taps ────────────────────────
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            cpsCounter.registerTap()
        }
        return false // always pass through
    }
}
