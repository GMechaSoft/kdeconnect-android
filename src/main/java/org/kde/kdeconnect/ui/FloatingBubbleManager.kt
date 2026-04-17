/*
 * SPDX-FileCopyrightText: 2026 Gerson Sanchez <gmechasoft@gmail.com>
 * SPDX-License-Identifier: GPL-2.0-only OR GPL-3.0-only OR LicenseRef-KDE-Accepted-GPL
 */

package org.kde.kdeconnect.ui

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.WindowManager.LayoutParams
import org.kde.kdeconnect.plugins.clipboard.ClipboardFloatingActivity
import org.kde.kdeconnect_tp.R

/**
 * Coordinates the floating bubble lifecycle and delegates logic to [BubbleStateController].
 */
class FloatingBubbleManager(private val context: Context) {
    companion object {
        private const val TAG = "FloatingBubbleManager"
        private const val INITIAL_X = 100
        private const val INITIAL_Y = 100
        private const val TARGET_Y_OFFSET = 200
        private const val CLICK_THRESHOLD = 10f
        private const val DISMISSAL_OVER_TARGET_RATIO = 0.5f
    }

    private val windowManager: WindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private val stateController = BubbleStateController(context)

    private var bubbleView: FloatingBubbleView? = null
    private var bubbleParams: LayoutParams? = null
    private var targetView: BubbleDismissTargetView? = null
    private var targetParams: LayoutParams? = null

    private var lastTouchX = 0f
    private var lastTouchY = 0f
    private var startTouchX = 0f
    private var startTouchY = 0f

    /**
     * Initializes and shows the floating bubble.
     */
    fun showBubble() {
        if (bubbleView != null) return

        bubbleView = FloatingBubbleView(context)

        val size = stateController.bubbleSize

        bubbleParams = LayoutParams().apply {
            width = size
            height = size
            x = INITIAL_X
            y = INITIAL_Y
            type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                LayoutParams.TYPE_PHONE
            }
            flags = LayoutParams.FLAG_NOT_FOCUSABLE or
                    LayoutParams.FLAG_NOT_TOUCH_MODAL or
                    LayoutParams.FLAG_LAYOUT_NO_LIMITS
            gravity = Gravity.TOP or Gravity.START
            format = PixelFormat.TRANSLUCENT
        }

        setupTouchListener()
        windowManager.addView(bubbleView, bubbleParams)
        Log.d(TAG, "Floating bubble shown with size $size")
    }

    /**
     * Hides and removes the floating bubble and target.
     */
    fun hideBubble() {
        bubbleView?.let {
            windowManager.removeView(it)
            bubbleView = null
            bubbleParams = null
            Log.d(TAG, "Floating bubble hidden")
        }
        hideTargetBubble()
    }

    private fun disableBubbleInPrefs() {
        val prefs = android.preference.PreferenceManager.getDefaultSharedPreferences(context)
        prefs.edit().putBoolean("kdeconnect_bubble_enabled", false).apply()
        Log.d(TAG, "Bubble disabled in shared preferences")
    }

    private fun showTargetBubble() {
        if (targetView != null) return

        targetView = BubbleDismissTargetView(context)

        val screenWidth = context.resources.displayMetrics.widthPixels
        val screenHeight = context.resources.displayMetrics.heightPixels
        val size = stateController.bubbleSize

        targetParams = LayoutParams().apply {
            width = size
            height = size
            x = (screenWidth - size) / 2
            y = screenHeight - TARGET_Y_OFFSET
            type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                LayoutParams.TYPE_PHONE
            }
            flags = LayoutParams.FLAG_NOT_FOCUSABLE or
                    LayoutParams.FLAG_NOT_TOUCH_MODAL or
                    LayoutParams.FLAG_LAYOUT_NO_LIMITS
            gravity = Gravity.TOP or Gravity.START
            format = PixelFormat.TRANSLUCENT
        }

        windowManager.addView(targetView, targetParams)
        Log.d(TAG, "Target bubble shown with size $size")
    }

    private fun hideTargetBubble() {
        targetView?.let {
            windowManager.removeView(it)
            targetView = null
            targetParams = null
            Log.d(TAG, "Target bubble hidden")
        }
    }

    private fun setupTouchListener() {
        bubbleView?.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startTouchX = event.rawX
                    startTouchY = event.rawY
                    lastTouchX = event.rawX
                    lastTouchY = event.rawY
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    val dx = event.rawX - lastTouchX
                    val dy = event.rawY - lastTouchY

                    bubbleParams?.let { p ->
                        val (newX, newY) = stateController.calculatePosition(
                            p.x, p.y, dx, dy, stateController.bubbleSize
                        )
                        p.x = newX
                        p.y = newY

                        showTargetBubble()

                        // Visual feedback only if over target
                        val isOver = targetParams?.let { tp ->
                            stateController.isOverTarget(p.x, p.y, tp.x, tp.y)
                        } ?: false

                        bubbleView?.setDismissalProgress(if (isOver) DISMISSAL_OVER_TARGET_RATIO else 0f)

                        windowManager.updateViewLayout(v, p)
                    }

                    lastTouchX = event.rawX
                    lastTouchY = event.rawY
                    true
                }
                MotionEvent.ACTION_UP -> {
                    val isOver = bubbleParams?.let { p ->
                        targetParams?.let { tp ->
                            stateController.isOverTarget(p.x, p.y, tp.x, tp.y)
                        }
                    } ?: false

                    if (isOver) {
                        disableBubbleInPrefs()
                        hideBubble()
                    } else {
                        hideTargetBubble()
                        bubbleView?.setDismissalProgress(0f)

                        val deltaX = Math.abs(event.rawX - startTouchX)
                        val deltaY = Math.abs(event.rawY - startTouchY)

                        if (deltaX < CLICK_THRESHOLD && deltaY < CLICK_THRESHOLD) {
                            bubbleView?.animateClick()
                            triggerClipboardSync()
                        }
                    }
                    true
                }
                else -> false
            }
        }
    }

    private fun triggerClipboardSync() {
        Log.d(TAG, "Bubble clicked: triggering clipboard sync")
        val intent = ClipboardFloatingActivity.getIntent(context, true)
        context.startActivity(intent)
    }
}

