package com.nova.client.overlay.elements

import android.graphics.*
import com.nova.client.data.models.CrosshairConfig
import com.nova.client.data.models.CrosshairStyle
import kotlin.math.*

/**
 * Renders all crosshair styles at the center of the screen.
 * The CrosshairElement is drawn at the center of the HUD layer,
 * not at its HudElement.position (that offset is zeroed out in HudOverlayView for crosshair).
 */
class CrosshairElement {

    private val mainPaint    = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE }
    private val fillPaint    = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val outlinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE }
    private val dotPaint     = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }

    /** screenW and screenH are passed in so we can center the crosshair. */
    fun draw(canvas: Canvas, cfg: CrosshairConfig, screenW: Float, screenH: Float) {
        val cx = screenW / 2f
        val cy = screenH / 2f

        mainPaint.color    = (cfg.color and 0xFFFFFFFF).toInt()
        mainPaint.alpha    = (cfg.opacity * 255).toInt()
        mainPaint.strokeWidth = cfg.thickness

        outlinePaint.color = (cfg.outlineColor and 0xFFFFFFFF).toInt()
        outlinePaint.alpha = (cfg.opacity * 255).toInt()
        outlinePaint.strokeWidth = cfg.thickness + cfg.outlineThickness * 2f

        fillPaint.color    = (cfg.color and 0xFFFFFFFF).toInt()
        fillPaint.alpha    = (cfg.opacity * 255).toInt()

        dotPaint.color     = (cfg.color and 0xFFFFFFFF).toInt()
        dotPaint.alpha     = (cfg.opacity * 255).toInt()

        val s = cfg.size * 20f   // base size
        val g = cfg.gapSize

        when (cfg.style) {
            CrosshairStyle.DEFAULT, CrosshairStyle.CROSS -> {
                drawCross(canvas, cx, cy, s, g, cfg)
            }
            CrosshairStyle.DOT -> {
                drawDot(canvas, cx, cy, cfg.dotSize * 4f, cfg)
            }
            CrosshairStyle.CIRCLE -> {
                drawCircle(canvas, cx, cy, s, cfg)
            }
            CrosshairStyle.CIRCLE_DOT -> {
                drawCircle(canvas, cx, cy, s, cfg)
                drawDot(canvas, cx, cy, cfg.dotSize * 3f, cfg)
            }
            CrosshairStyle.CROSS_DOT -> {
                drawCross(canvas, cx, cy, s, g, cfg)
                drawDot(canvas, cx, cy, cfg.dotSize * 3f, cfg)
            }
            CrosshairStyle.SQUARE -> {
                val half = s * 0.5f
                if (cfg.showOutline) canvas.drawRect(cx - half - outlinePaint.strokeWidth / 2f, cy - half - outlinePaint.strokeWidth / 2f, cx + half + outlinePaint.strokeWidth / 2f, cy + half + outlinePaint.strokeWidth / 2f, outlinePaint)
                canvas.drawRect(cx - half, cy - half, cx + half, cy + half, mainPaint)
            }
            CrosshairStyle.TRIANGLE -> {
                val path = Path().apply {
                    moveTo(cx, cy - s)
                    lineTo(cx + s * 0.866f, cy + s * 0.5f)
                    lineTo(cx - s * 0.866f, cy + s * 0.5f)
                    close()
                }
                if (cfg.showOutline) { outlinePaint.style = Paint.Style.STROKE; canvas.drawPath(path, outlinePaint) }
                canvas.drawPath(path, mainPaint)
            }
            CrosshairStyle.STAR -> {
                for (i in 0 until 5) {
                    val angle = Math.toRadians(i * 72.0 - 90.0)
                    val ox = (cx + cos(angle) * s).toFloat()
                    val oy = (cy + sin(angle) * s).toFloat()
                    if (cfg.showOutline) canvas.drawLine(cx, cy, ox, oy, outlinePaint)
                    canvas.drawLine(cx, cy, ox, oy, mainPaint)
                }
            }
            CrosshairStyle.ARROW -> {
                val path = Path().apply {
                    moveTo(cx, cy - s)
                    lineTo(cx + s * 0.4f, cy)
                    lineTo(cx + s * 0.15f, cy)
                    lineTo(cx + s * 0.15f, cy + s)
                    lineTo(cx - s * 0.15f, cy + s)
                    lineTo(cx - s * 0.15f, cy)
                    lineTo(cx - s * 0.4f, cy)
                    close()
                }
                if (cfg.showOutline) canvas.drawPath(path, outlinePaint)
                fillPaint.style = Paint.Style.FILL
                canvas.drawPath(path, fillPaint)
            }
            CrosshairStyle.HEART -> {
                // Simple heart approximation via bezier
                val path = Path().apply {
                    val hs = s * 0.6f
                    moveTo(cx, cy + hs * 0.8f)
                    cubicTo(cx - hs * 1.5f, cy - hs * 0.2f, cx - hs * 2f, cy - hs * 1.5f, cx, cy - hs * 0.5f)
                    cubicTo(cx + hs * 2f, cy - hs * 1.5f, cx + hs * 1.5f, cy - hs * 0.2f, cx, cy + hs * 0.8f)
                    close()
                }
                fillPaint.style = Paint.Style.FILL
                canvas.drawPath(path, fillPaint)
            }
            CrosshairStyle.CUSTOM -> {
                drawCross(canvas, cx, cy, s, g, cfg) // fallback
            }
        }
    }

    private fun drawCross(canvas: Canvas, cx: Float, cy: Float, s: Float, g: Float, cfg: CrosshairConfig) {
        // Horizontal lines
        if (cfg.showOutline) {
            canvas.drawLine(cx - s - g, cy, cx - g, cy, outlinePaint)
            canvas.drawLine(cx + g, cy, cx + s + g, cy, outlinePaint)
            canvas.drawLine(cx, cy - s - g, cx, cy - g, outlinePaint)
            canvas.drawLine(cx, cy + g, cx, cy + s + g, outlinePaint)
        }
        canvas.drawLine(cx - s - g, cy, cx - g, cy, mainPaint)
        canvas.drawLine(cx + g, cy, cx + s + g, cy, mainPaint)
        canvas.drawLine(cx, cy - s - g, cx, cy - g, mainPaint)
        canvas.drawLine(cx, cy + g, cx, cy + s + g, mainPaint)

        if (cfg.showDot) drawDot(canvas, cx, cy, cfg.dotSize * 3f, cfg)
    }

    private fun drawCircle(canvas: Canvas, cx: Float, cy: Float, r: Float, cfg: CrosshairConfig) {
        if (cfg.showOutline) canvas.drawCircle(cx, cy, r, outlinePaint)
        canvas.drawCircle(cx, cy, r, mainPaint)
    }

    private fun drawDot(canvas: Canvas, cx: Float, cy: Float, radius: Float, cfg: CrosshairConfig) {
        canvas.drawCircle(cx, cy, radius, dotPaint)
    }
}
