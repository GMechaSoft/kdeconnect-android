/*
 * SPDX-FileCopyrightText: 2026 Gerson Sanchez <gmechasoft@gmail.com>
 * SPDX-License-Identifier: GPL-2.0-only OR GPL-3.0-only OR LicenseRef-KDE-Accepted-GPL
 */

package org.kde.kdeconnect.ui

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.ImageView
import org.kde.kdeconnect_tp.R

/**
 * Static target view for bubble dismissal.
 * Purely visual, no interaction logic.
 */
class BubbleDismissTargetView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    companion object {
        private const val BACKGROUND_COLOR = 0x80808080.toInt() // 50% transparent grey
        private const val BORDER_WIDTH = 2
        private const val ICON_PADDING = 16
    }

    init {
        // Set circular background: semi-transparent grey
        val shape = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(BACKGROUND_COLOR)
            setStroke(BORDER_WIDTH, Color.WHITE)
        }
        background = shape

        // Create 'X' icon
        val iconView = ImageView(context).apply {
            setImageResource(R.drawable.ic_delete)
            layoutParams = LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT
            )
            setPadding(ICON_PADDING, ICON_PADDING, ICON_PADDING, ICON_PADDING)
            setColorFilter(Color.WHITE)
            scaleType = ImageView.ScaleType.FIT_CENTER
        }

        addView(iconView)
    }
}
