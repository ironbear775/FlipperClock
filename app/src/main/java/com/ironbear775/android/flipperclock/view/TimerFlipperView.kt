package com.ironbear775.android.flipperclock.view

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.content.ContextCompat
import com.ironbear775.android.flipperclock.R
import com.ironbear775.android.flipperclock.util.*


/**
 * @Author: Xym
 * @Date: 2020/7/7 11:18
 */
class TimerFlipperView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
    private val camera = Camera()
    private var bgColor = ContextCompat.getColor(context, R.color.flipperBgColor)
    private var textColor = ContextCompat.getColor(context, R.color.flipperTextColor)
    private var textSize = 180f.spF
    private var bgCorner = 5f.dpF
    private var defaultWidth = 150.dp
    private var defaultHeight = 200.dp
    private val gapHeight = 1f.dpF
    private var bgTopPath = Path()
    private var bgBottomPath = Path()
    private var currentTime = -1
    private var preTime = -1
    private var flipping = false
    private var maxTime = 10
    private var degree = 0f
    private var flipPadding = 4.dpF

    init {
        bgPaint.color = bgColor
        bgPaint.style = Paint.Style.FILL_AND_STROKE
        bgPaint.strokeCap = Paint.Cap.ROUND
        textPaint.color = textColor
        textPaint.typeface = FontCache.getTypeface(TYPEFACE, context)
        textPaint.style = Paint.Style.FILL_AND_STROKE
        textPaint.textSize = textSize
        textPaint.textAlign = Paint.Align.CENTER
        val displayMetrics = resources.displayMetrics
        val newZ = -displayMetrics.density * 18
        camera.setLocation(0f, 0f, newZ)
    }

    private fun setRoundBgPath(w: Int, h: Int) {
        val round = bgCorner
        bgTopPath.reset()
        bgBottomPath.reset()
        bgTopPath.addRoundRect(
            RectF(
                paddingLeft + flipPadding,
                paddingTop + flipPadding,
                w - flipPadding,
                h / 2 - gapHeight
            ), round, round, Path.Direction.CW
        )
        bgBottomPath.addRoundRect(
            RectF(
                paddingLeft + flipPadding,
                h / 2 + gapHeight,
                w - paddingRight - flipPadding,
                h - paddingBottom - flipPadding
            ), round, round, Path.Direction.CW
        )
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

    fun setDegree(degree: Float) {
        this.degree = degree
        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val centerX = measuredWidth / 2
        val topCenterY = (measuredHeight / 2).toFloat() - gapHeight
        val bottomCenterY = topCenterY + gapHeight * 2
        canvas?.let {
            if (flipping) {
                //绘制一下时间的上半部分
                canvas.save()
                canvas.clipRect(0, 0, width, topCenterY.toInt())
                canvas.drawPath(bgTopPath, bgPaint)
                drawText(canvas)
                canvas.restore()

                // 绘制不动的下半部分
                canvas.save();
                canvas.clipRect(0, bottomCenterY.toInt(), width, height)
                canvas.drawPath(bgBottomPath, bgPaint)
                drawText(canvas, true)
                canvas.restore();

                if (degree < 90) {
                    // 绘制翻转的上半部分
                    canvas.save()
                    canvas.clipRect(0, 0, width, topCenterY.toInt())

                    camera.save();
                    canvas.translate(centerX.toFloat(), topCenterY)
                    camera.rotateX(-degree);
                    camera.applyToCanvas(canvas);
                    canvas.translate(-centerX.toFloat(), -topCenterY)
                    camera.restore()

                    canvas.drawPath(bgTopPath, bgPaint)
                    drawText(canvas, true)
                    canvas.restore()
                } else {
                    // 绘制翻转的下半部分
                    canvas.save()
                    canvas.clipRect(0, topCenterY.toInt(), width, height)

                    camera.save()
                    canvas.translate(centerX.toFloat(), topCenterY)
                    val bottomDegree = 180 - degree
                    camera.rotateX(bottomDegree)
                    camera.applyToCanvas(canvas)
                    canvas.translate(-centerX.toFloat(), -topCenterY)
                    camera.restore()

                    canvas.drawPath(bgBottomPath, bgPaint)
                    drawText(canvas)
                    canvas.restore()
                }
            } else {
                drawBg(canvas)
                drawText(canvas)
            }
        }
    }

    private fun drawBg(canvas: Canvas) {
        canvas.drawPath(bgTopPath, bgPaint)
        canvas.drawPath(bgBottomPath, bgPaint)
    }

    private fun drawText(canvas: Canvas, drawPre: Boolean = false) {
        if (currentTime == -1) return
        val text = if (drawPre) preTime.toString() else currentTime.toString()
        canvas.drawText(
            text,
            (width / 2).toFloat(),
            (height / 2 + gapHeight / 2 - ((textPaint.descent() + textPaint.ascent()) / 2)),
            textPaint
        )
    }

    private fun startFlip() {
        val animator = ObjectAnimator.ofFloat(this, "degree", 0f, 180f)
        animator.duration = 500
        animator.interpolator = LinearInterpolator()
        animator.doOnEnd {
            flipping = false
            invalidate()
        }
        animator.doOnStart {
            preTime = currentTime
            if (preTime == maxTime - 1 || (!use12 && highHourTime == 2 && preTime == 3)) {
                currentTime = 0
            } else {
                currentTime++
            }
            flipping = true
        }
        animator.start()
    }

    private var use12 = false
    fun setMaxTime(max: Int, use12: Boolean = false) {
        maxTime = max
        this.use12 = use12
    }

    private var highHourTime = -1

    fun setTime(time: Int, hourTime: Int? = null) {
        if (!use12 && hourTime == 2) {
            highHourTime = hourTime
        } else {
            highHourTime = -1
        }

        if (time == currentTime) return
        if (currentTime == -1) {
            currentTime = time
            preTime = time - 1
            invalidate()
        } else {
            startFlip()
        }
    }

    fun useCompactUI(use: Boolean) {
        if (use) {
            textSize = 120f.spF
            defaultWidth = 105.dp
            defaultHeight = 140.dp
        } else {
            textSize = 240f.spF
            defaultWidth = 165.dp
            defaultHeight = 220.dp
        }
        textPaint.textSize = textSize
        requestLayout()
    }

    fun updateColor(colorInt: Int, bgColorInt: Int) {
        textPaint.color = colorInt
        bgPaint.color = bgColorInt
        invalidate()
    }

    fun reset(){
        preTime = -1
        currentTime = -1
    }
}