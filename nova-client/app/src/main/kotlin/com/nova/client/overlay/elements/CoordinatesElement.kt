package com.nova.client.overlay.elements

import android.graphics.*

/**
 * Shows X/Y/Z coordinates and cardinal direction.
 *
 * In a real Bedrock integration the coordinates would come from
 * the game's location API. Here they are simulated for HUD demo purposes.
 * To integrate with an actual Bedrock session, call updatePosition()
 * from your protocol handler whenever a MovePlayerPacket is received.
 */
class CoordinatesElement {

    private var x: Double = 128.0
    private var y: Double = 64.0
    private var z: Double = -256.0
    private var facing: String = "N"

    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xAA000000.toInt()
        style = Paint.Style.FILL
    }
    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFF00D4FF.toInt()
        textSize = 24f
        typeface = Typeface.MONOSPACE
    }
    private val valuePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 24f
        typeface = Typeface.MONOSPACE
    }

    fun updatePosition(x: Double, y: Double, z: Double, yaw: Float = 0f) {
        this.x = x
        this.y = y
        this.z = z
        this.facing = yawToCardinal(yaw)
    }

    fun draw(canvas: Canvas) {
        val lines = listOf(
            "X" to "%.1f".format(x),
            "Y" to "%.1f".format(y),
            "Z" to "%.1f".format(z),
            "→" to facing
        )
        val lineH = 28f
        val padX = 10f
        val padY = 8f
        val w = 175f
        val h = lines.size * lineH + padY * 2

        canvas.drawRoundRect(0f, 0f, w, h, 10f, 10f, bgPaint)

        lines.forEachIndexed { i, (label, value) ->
            val y = padY + (i + 1) * lineH - 4f
            canvas.drawText(label, padX, y, labelPaint)
            canvas.drawText(value, w - valuePaint.measureText(value) - padX, y, valuePaint)
        }
    }

    private fun yawToCardinal(yaw: Float): String {
        val norm = ((yaw % 360) + 360) % 360
        return when {
            norm < 22.5 || norm >= 337.5 -> "S"
            norm < 67.5  -> "SW"
            norm < 112.5 -> "W"
            norm < 157.5 -> "NW"
            norm < 202.5 -> "N"
            norm < 247.5 -> "NE"
            norm < 292.5 -> "E"
            else         -> "SE"
        }
    }
}
