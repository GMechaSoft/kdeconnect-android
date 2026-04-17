/*
 * SPDX-FileCopyrightText: 2026 Gerson Sanchez <gmechasoft@gmail.com>
 * SPDX-License-Identifier: GPL-2.0-only OR GPL-3.0-only OR LicenseRef-KDE-Accepted-GPL
 */

package org.kde.kdeconnect.ui

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import org.kde.kdeconnect_tp.R

/**
 * Circular view representing the floating bubble.
 * Handles its own visual state and provides a click target for synchronization.
 */
class FloatingBubbleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    initialIconRes: Int = R.drawable.ic_baseline_content_paste_24
) : FrameLayout(context, attrs, defStyleAttr) {

    companion object {
        private const val BACKGROUND_COLOR = 0x80808080.toInt() // 50% transparent grey
        private const val BORDER_WIDTH = 2
        private const val ICON_PADDING = 16
        private const val ANIMATION_DURATION = 100L
        private const val ANIMATION_SCALE = 0.8f
        private const val DISMISSAL_SCALE_FACTOR = 0.5f
    }

    private val iconView: ImageView

    init {
        // Set circular background: semi-transparent grey
        val shape = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(BACKGROUND_COLOR)
            setStroke(BORDER_WIDTH, Color.WHITE)
        }
        background = shape

        // Create icon
        iconView = ImageView(context).apply {
            setImageResource(initialIconRes)
            layoutParams = LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            setPadding(ICON_PADDING, ICON_PADDING, ICON_PADDING, ICON_PADDING)
            setColorFilter(Color.WHITE)
            scaleType = ImageView.ScaleType.FIT_CENTER
        }

        addView(iconView)
    }

    fun setIcon(resId: Int) {
        iconView.setImageResource(resId)
    }

    fun animateClick() {
        animate().scaleX(ANIMATION_SCALE).scaleY(ANIMATION_SCALE).setDuration(ANIMATION_DURATION).withEndAction {
            animate().scaleX(1.0f).scaleY(1.0f).setDuration(ANIMATION_DURATION).start()
        }.start()
    }

    fun setDismissalProgress(progress: Float) {
        val scale = 1.0f - (progress * DISMISSAL_SCALE_FACTOR)
        val alpha = 1.0f - progress
        scaleX = scale
        scaleY = scale
        this.alpha = alpha
    }
}
