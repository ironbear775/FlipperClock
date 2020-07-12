package com.ironbear775.android.flipperclock.view

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
 * @Date: 2020/7/10 14:19
 */
class BatteryView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var defaultWidth = 20.dp
    private var defaultHeight = 12.dp
    private var batteryCorner = 1.dpF
    private var bgCorner = 2.5f.dpF
    private var gap = 1.5f.dpF
    private val bgPath = Path()
    private val batteryPath = Path()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val batteryPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val defaultColor = ContextCompat.getColor(context, R.color.flipperTextColor)
    private var remain = 80

    init {
        paint.color = defaultColor
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1.dpF
        batteryPaint.color = defaultColor
        batteryPaint.style = Paint.Style.FILL
    }

    fun setColor(color: Int) {
        paint.color = color
        batteryPaint.color = color
        invalidate()
    }

    fun setBatteryRemain(remain: Int) {
        this.remain = remain
        invalidate()
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
        setRoundBgPath(realWidth, realHeight)
    }

    private fun setRoundBgPath(w: Int, h: Int) {
        val round = bgCorner
        bgPath.reset()
        bgPath.addRoundRect(
            RectF(
                paddingLeft.toFloat(),
                paddingTop.toFloat(),
                (w - paddingRight).toFloat(),
                (h - paddingBottom).toFloat()
            ), round, round, Path.Direction.CW
        )
    }

    private fun setBatteryPath(w: Int, h: Int) {
        val round = batteryCorner
        batteryPath.reset()
        batteryPath.addRoundRect(
            RectF(
                paddingLeft + gap,
                paddingTop + gap,
                w * remain / 100f - paddingRight - gap,
                h - paddingBottom - gap
            ), round, round, Path.Direction.CW
        )
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        setBatteryPath(width, height)
        canvas?.let {
            it.drawPath(bgPath, paint)
            it.drawPath(batteryPath, batteryPaint)
        }
    }
}