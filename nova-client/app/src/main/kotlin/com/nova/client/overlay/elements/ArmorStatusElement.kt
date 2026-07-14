package com.nova.client.overlay.elements

import android.graphics.*

/**
 * Displays armor durability bars for helmet, chestplate, leggings, and boots.
 * Call updateArmor() from the protocol layer when an InventorySlotPacket
 * containing armor items is received.
 */
class ArmorStatusElement {

    data class ArmorPiece(val name: String, var durability: Float = 1.0f, var equipped: Boolean = true)

    private val armor = listOf(
        ArmorPiece("Helm"),
        ArmorPiece("Chest"),
        ArmorPiece("Legs"),
        ArmorPiece("Boots")
    )

    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xAA000000.toInt()
        style = Paint.Style.FILL
    }
    private val barBgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFF333333.toInt()
        style = Paint.Style.FILL
    }
    private val barFillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 20f
        typeface = Typeface.MONOSPACE
    }

    fun updateArmor(slot: Int, durabilityFraction: Float, equipped: Boolean) {
        if (slot in armor.indices) {
            armor[slot].durability = durabilityFraction.coerceIn(0f, 1f)
            armor[slot].equipped = equipped
        }
    }

    fun draw(canvas: Canvas) {
        val equippedPieces = armor.filter { it.equipped }
        if (equippedPieces.isEmpty()) return

        val lineH = 26f
        val barW  = 80f
        val barH  = 8f
        val padX  = 8f
        val padY  = 8f
        val labelW = 44f
        val totalW = padX * 2 + labelW + barW + 10f
        val totalH = equippedPieces.size * lineH + padY * 2

        canvas.drawRoundRect(0f, 0f, totalW, totalH, 10f, 10f, bgPaint)

        equippedPieces.forEachIndexed { i, piece ->
            val cy = padY + i * lineH + lineH / 2f
            canvas.drawText(piece.name, padX, cy + 7f, textPaint)

            val barX = padX + labelW + 4f
            val barY = cy - barH / 2f
            // Background track
            canvas.drawRoundRect(barX, barY, barX + barW, barY + barH, 4f, 4f, barBgPaint)
            // Fill
            val dur = piece.durability
            barFillPaint.color = when {
                dur > 0.6f -> 0xFF00CC66.toInt()
                dur > 0.3f -> 0xFFFFAA00.toInt()
                else       -> 0xFFFF4444.toInt()
            }
            canvas.drawRoundRect(barX, barY, barX + barW * dur, barY + barH, 4f, 4f, barFillPaint)
        }
    }
}
