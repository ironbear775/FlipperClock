package com.ironbear775.android.flipperclock.view

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.ironbear775.android.flipperclock.R
import com.ironbear775.android.flipperclock.util.dp
import com.ironbear775.android.flipperclock.util.dpF

/**
 * @Author: Xym
 * @Date: 2020/7/8 15:37
 */
class TimerDotView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val color = ContextCompat.getColor(context, R.color.flipperTextColor)
    private val path = Path()
    private var defaultWidth = 15.dp
    private var gapHeight = 10.dp
    private var defaultHeight = defaultWidth * 2 + gapHeight
    private var corner = 2f.dpF

    init {
        paint.color = color
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthSpec = MeasureSpec.getMode(widthMeasureSpec)
        val heightSpec = MeasureSpec.getMode(heightMeasureSpec)
        val realWidth: Int
        val realHeight: Int
        if (widthSpec == MeasureSpec.AT_MOST || heightSpec == MeasureSpec.AT_MOST ||
            widthSpec == MeasureSpec.UNSPECIFIED || heightSpec == MeasureSpec.UNSPECIFIED
        ) {
            realWidth = defaultWidth
            realHeight = defaultHeight
        } else {
            realWidth = MeasureSpec.getSize(widthMeasureSpec)
            realHeight = MeasureSpec.getSize(heightMeasureSpec)
        }
        setMeasuredDimension(realWidth, realHeight)
        setPath(realWidth.toFloat())
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawPath(path, paint)
    }

    private var show = true
    fun startGlint() {
        val animator = ObjectAnimator.ofFloat(this, "alpha", 1f,0f).setDuration(1000)
        animator.repeatMode = ValueAnimator.RESTART
        animator.repeatCount = ValueAnimator.INFINITE
        animator.start()
        show = !show
    }

    private fun setPath(w: Float) {
        path.reset()
        path.addRoundRect(
            RectF(
                0f + paddingLeft,
                0f + paddingTop,
                w - paddingRight,
                w - paddingBottom
            ),
            corner, corner, Path.Direction.CW
        )

        path.addRoundRect(
            RectF(
                0f + paddingLeft,
                w + gapHeight,
                w - paddingRight,
                w + w + gapHeight
            ), corner, corner, Path.Direction.CW
        )
    }

    fun useCompactUI(use: Boolean) {
        if (use) {
            defaultWidth = 10.dp
            gapHeight = 7f.dp
            corner = 1f.dpF
        } else {
            defaultWidth = 15.dp
            gapHeight = 10.dp
            corner = 2f.dpF
        }
        defaultHeight = defaultWidth * 2 + gapHeight
        requestLayout()
    }

    fun setTextColor(colorInt: Int) {
        paint.color = colorInt
        invalidate()
    }
}