package com.nova.client.overlay.elements

import android.graphics.*

/**
 * Shows active potion effects with remaining time and amplifier.
 * Call addEffect() / removeEffect() from the protocol layer when
 * a MobEffectPacket is received.
 */
class PotionEffectsElement {

    data class PotionEffect(
        val id: Int,
        val name: String,
        val amplifier: Int,       // 0 = I, 1 = II, etc.
        var remainingMs: Long,
        val color: Int
    )

    private val effects = mutableMapOf<Int, PotionEffect>()
    private var lastTick = System.currentTimeMillis()

    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xAA000000.toInt()
        style = Paint.Style.FILL
    }
    private val namePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 22f
        typeface = Typeface.DEFAULT_BOLD
    }
    private val timePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFFAAAAAA.toInt()
        textSize = 19f
        typeface = Typeface.MONOSPACE
    }
    private val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    fun addEffect(id: Int, name: String, amplifier: Int, durationTicks: Int, color: Int) {
        effects[id] = PotionEffect(id, name, amplifier, durationTicks * 50L, color)
    }

    fun removeEffect(id: Int) {
        effects.remove(id)
    }

    // Demo effects so the HUD shows something
    fun loadDemoEffects() {
        addEffect(1, "Speed",      1, 1200, 0xFF88AAFF.toInt())
        addEffect(5, "Resistance", 0, 600,  0xFF884444.toInt())
    }

    fun draw(canvas: Canvas) {
        // Tick down remaining time
        val now = System.currentTimeMillis()
        val delta = now - lastTick
        lastTick = now
        effects.values.forEach { it.remainingMs -= delta }
        effects.entries.removeIf { it.value.remainingMs <= 0 }

        if (effects.isEmpty()) return

        val sorted = effects.values.sortedByDescending { it.remainingMs }
        val lineH = 30f
        val padX = 10f
        val padY = 8f
        val w = 200f
        val h = sorted.size * lineH + padY * 2

        canvas.drawRoundRect(0f, 0f, w, h, 10f, 10f, bgPaint)

        sorted.forEachIndexed { i, eff ->
            val cy = padY + i * lineH + 20f
            dotPaint.color = eff.color
            canvas.drawCircle(padX + 6f, cy - 6f, 6f, dotPaint)

            val amp = if (eff.amplifier > 0) " ${romanNumeral(eff.amplifier + 1)}" else ""
            canvas.drawText("${eff.name}$amp", padX + 18f, cy, namePaint)

            val secs = (eff.remainingMs / 1000).coerceAtLeast(0)
            val timeStr = "%d:%02d".format(secs / 60, secs % 60)
            canvas.drawText(timeStr, w - timePaint.measureText(timeStr) - padX, cy, timePaint)
        }
    }

    private fun romanNumeral(n: Int) = listOf("", "I","II","III","IV","V","VI","VII","VIII","IX","X").getOrElse(n) { n.toString() }
}
