package com.nova.client.overlay.elements

import android.graphics.*

/** Shows the current render FPS (frames per second of the overlay view). */
class FpsDisplayElement {

    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xAA000000.toInt()
        style = Paint.Style.FILL
    }
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 32f
        typeface = Typeface.MONOSPACE
        isFakeBoldText = true
    }
    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFF00D4FF.toInt()
        textSize = 20f
    }

    fun draw(canvas: Canvas, fps: Int) {
        textPaint.color = when {
            fps >= 50 -> 0xFF00FF88.toInt()
            fps >= 30 -> 0xFFFFAA00.toInt()
            else      -> 0xFFFF4444.toInt()
        }
        val fpsStr = fps.toString()
        val label  = "FPS"
        val w = maxOf(textPaint.measureText(fpsStr), labelPaint.measureText(label)) + 20f
        val h = 72f

        canvas.drawRoundRect(0f, 0f, w, h, 10f, 10f, bgPaint)
        canvas.drawText(fpsStr, w / 2f - textPaint.measureText(fpsStr) / 2f, 44f, textPaint)
        canvas.drawText(label,  w / 2f - labelPaint.measureText(label) / 2f, 64f, labelPaint)
    }
}
