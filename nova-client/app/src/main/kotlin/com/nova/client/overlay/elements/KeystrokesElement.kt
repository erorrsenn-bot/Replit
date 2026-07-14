package com.nova.client.overlay.elements

import android.graphics.*

/**
 * Shows WASD / Jump / Sneak keystrokes.
 * In a real integration, call setKeyPressed() from the touch intercept layer
 * when the player taps a direction or action button.
 */
class KeystrokesElement {

    enum class Key(val label: String) {
        W("W"), A("A"), S("S"), D("D"),
        JUMP("↑"), SNEAK("↓"), SPRINT("▶")
    }

    private val pressed = mutableSetOf<Key>()

    private val bgPaint    = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style  = Paint.Style.STROKE
        strokeWidth = 2f
        color  = 0xFF444444.toInt()
    }
    private val textPaint  = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 24f
        typeface = Typeface.MONOSPACE
        isFakeBoldText = true
        textAlign = Paint.Align.CENTER
    }

    fun setKeyPressed(key: Key, isDown: Boolean) {
        if (isDown) pressed.add(key) else pressed.remove(key)
    }

    fun draw(canvas: Canvas) {
        val size = 48f
        val gap  =  6f
        val r    =  8f

        // Layout: top row = W, middle = A S D, bottom = JUMP SNEAK
        val layout = mapOf(
            Key.W     to Pair(size + gap, 0f),
            Key.A     to Pair(0f,         size + gap),
            Key.S     to Pair(size + gap, size + gap),
            Key.D     to Pair((size + gap) * 2, size + gap),
            Key.JUMP  to Pair(size + gap, (size + gap) * 2),
            Key.SNEAK to Pair((size + gap) * 2, (size + gap) * 2)
        )

        layout.forEach { (key, pos) ->
            val x = pos.first
            val y = pos.second
            val isDown = key in pressed

            bgPaint.color = if (isDown) 0xCC6C63FF.toInt() else 0xAA1A1A1A.toInt()
            canvas.drawRoundRect(x, y, x + size, y + size, r, r, bgPaint)
            canvas.drawRoundRect(x, y, x + size, y + size, r, r, borderPaint)

            textPaint.color = if (isDown) Color.WHITE else 0xFFCCCCCC.toInt()
            canvas.drawText(key.label, x + size / 2f, y + size / 2f + 9f, textPaint)
        }
    }
}
