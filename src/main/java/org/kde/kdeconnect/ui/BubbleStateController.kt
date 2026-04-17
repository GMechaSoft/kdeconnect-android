/*
 * SPDX-FileCopyrightText: 2026 Gerson Sanchez <gmechasoft@gmail.com>
 * SPDX-License-Identifier: GPL-2.0-only OR GPL-3.0-only OR LicenseRef-KDE-Accepted-GPL
 */

package org.kde.kdeconnect.ui

import kotlin.math.pow
import kotlin.math.sqrt
import android.content.Context
import android.view.WindowManager.LayoutParams

/**
 * Handles the business logic for the floating bubble's state, positioning and collision.
 */
class BubbleStateController(private val context: Context) {
    companion object {
        private const val TABLET_MIN_WIDTH_DP = 600
        private const val BUBBLE_SIZE_TABLET = 100
        private const val BUBBLE_SIZE_PHONE = 150
        private const val MAGNETIC_THRESHOLD_RATIO = 0.5f
        private const val EDGE_DAMPING = 0.2
        private const val EDGE_MIN_VISIBLE_RATIO = 0.8
        private const val BOTTOM_SAFETY_MARGIN = 100
        private const val COLLISION_THRESHOLD_RATIO = 0.5
    }

    private val screenWidth = context.resources.displayMetrics.widthPixels
    private val screenHeight = context.resources.displayMetrics.heightPixels

    val bubbleSize: Int = if (isTablet()) BUBBLE_SIZE_TABLET else BUBBLE_SIZE_PHONE

    private fun isTablet(): Boolean {
        val smallestScreenWidthDp = context.resources.configuration.smallestScreenWidthDp
        return smallestScreenWidthDp >= TABLET_MIN_WIDTH_DP
    }

    /**
     * Calculates the new position based on movement and applies magnetic edge anchoring.
     * Returns a Pair of (x, y).
     */
    fun calculatePosition(currentX: Int, currentY: Int, dx: Float, dy: Float, bubbleWidth: Int): Pair<Int, Int> {
        var newX = currentX + dx.toInt()
        var newY = currentY + dy.toInt()

        // Edge anchoring (Magnetic effect)
        // Left edge
        if (newX < 0) {
            val centerX = newX + bubbleWidth / 2
            val magneticThreshold = (bubbleWidth * MAGNETIC_THRESHOLD_RATIO).toInt()
            if (centerX < magneticThreshold) {
                val targetX = -bubbleWidth / 2
                val diff = targetX - newX
                newX += (diff * EDGE_DAMPING).toInt()
            }
            // Prevent disappearing completely
            if (newX < -bubbleWidth * EDGE_MIN_VISIBLE_RATIO) {
                newX = (-(bubbleWidth * EDGE_MIN_VISIBLE_RATIO)).toInt()
            }
        }
        // Right edge
        else if (newX > screenWidth - bubbleWidth) {
            val centerX = newX + bubbleWidth / 2
            val magneticThreshold = (bubbleWidth * MAGNETIC_THRESHOLD_RATIO).toInt()
            if (centerX > screenWidth - magneticThreshold) {
                val targetX = screenWidth - bubbleWidth / 2
                val diff = targetX - newX
                newX += (diff * EDGE_DAMPING).toInt()
            }
            // Prevent disappearing completely
            if (newX > screenWidth) {
                newX = (screenWidth - (bubbleWidth * (1.0 - EDGE_MIN_VISIBLE_RATIO))).toInt()
            }
        }

        // Bottom boundary: Prevent bubble from overlapping navigation bar
        val bottomLimit = screenHeight - bubbleWidth - BOTTOM_SAFETY_MARGIN
        if (newY > bottomLimit) {
            newY = bottomLimit
        }

        return Pair(newX, newY)
    }

    /**
     * Checks if the bubble center is overlapping with the target center.
     */
    fun isOverTarget(bubbleX: Int, bubbleY: Int, targetX: Int, targetY: Int): Boolean {
        val halfSize = bubbleSize / 2f
        val bubbleCenterX = bubbleX + halfSize
        val bubbleCenterY = bubbleY + halfSize
        val targetCenterX = targetX + halfSize
        val targetCenterY = targetY + halfSize

        val dist = sqrt(
            (bubbleCenterX - targetCenterX).toDouble().pow(2.0) +
            (bubbleCenterY - targetCenterY).toDouble().pow(2.0)
        )
        return dist < (bubbleSize * COLLISION_THRESHOLD_RATIO)
    }
}
