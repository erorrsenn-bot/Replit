package com.nova.client.overlay.elements

import android.graphics.*

/**
 * Draws a CPS (clicks/taps per second) counter.
 * Tap timestamps are recorded and expired after 1 second.
 */
class CpsCounterElement {

    private val tapTimes = ArrayDeque<Long>()

    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xAA000000.toInt()
        style = Paint.Style.FILL
    }
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 36f
        typeface = Typeface.MONOSPACE
        isFakeBoldText = true
    }
    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFF00D4FF.toInt()
        textSize = 22f
        typeface = Typeface.DEFAULT
    }

    fun registerTap() {
        tapTimes.addLast(System.currentTimeMillis())
    }

    fun draw(canvas: Canvas) {
        val now = System.currentTimeMillis()
        // Remove taps older than 1 second
        while (tapTimes.isNotEmpty() && now - tapTimes.first() > 1000L) {
            tapTimes.removeFirst()
        }
        val cps = tapTimes.size

        // Color gradient: green <5, yellow 5-9, red 10+
        textPaint.color = when {
            cps >= 10 -> 0xFFFF4444.toInt()
            cps >= 5  -> 0xFFFFAA00.toInt()
            else      -> 0xFF00FF88.toInt()
        }

        val cpsText = cps.toString()
        val label = "CPS"
        val w = maxOf(textPaint.measureText(cpsText), labelPaint.measureText(label)) + 24f
        val h = 80f

        canvas.drawRoundRect(0f, 0f, w, h, 10f, 10f, bgPaint)
        canvas.drawText(cpsText, w / 2f - textPaint.measureText(cpsText) / 2f, 48f, textPaint)
        canvas.drawText(label,  w / 2f - labelPaint.measureText(label) / 2f, 72f, labelPaint)
    }
}
