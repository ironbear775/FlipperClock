package com.ironbear775.android.flipperclock.view

import android.content.Context
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.updateLayoutParams
import com.ironbear775.android.flipperclock.util.Preference
import com.ironbear775.android.flipperclock.util.dp
import java.util.*

/**
 * @Author: Xym
 * @Date: 2020/7/7 20:45
 */
class TimerLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), Handler.Callback {
    companion object {
        private const val MSG_WHAT = 1078
    }

    private val heightHourFlipper = TimerFlipperView(context)
    private val lowHourFlipper = TimerFlipperView(context)
    private val hourMinuteDot = TimerDotView(context)
    private val heightMinuteFlipper = TimerFlipperView(context)
    private val lowMinuteFlipper = TimerFlipperView(context)
    private val minuteSecondDot = TimerDotView(context)
    private val heightSecondFlipper = TimerFlipperView(context)
    private val lowSecondFlipper = TimerFlipperView(context)

    private val timerHandler = Handler(this)
    var showSecond = false
        set(value) {
            field = value
            shouldShowSecond()
        }

    override fun handleMessage(msg: Message): Boolean {
        if (msg.what == MSG_WHAT) {
            val calendar = Calendar.getInstance()
            val hour =
                if (use12) calendar.get(Calendar.HOUR) else calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)
            val second = calendar.get(Calendar.SECOND)
            val heightHour = hour / 10
            val lowHour = hour % 10
            val heightMinute = minute / 10
            val lowMinute = minute % 10
            val heightSecond = second / 10
            val lowSecond = second % 10
            heightHourFlipper.setTime(heightHour)
            lowHourFlipper.setTime(lowHour,heightHour)
            heightMinuteFlipper.setTime(heightMinute)
            lowMinuteFlipper.setTime(lowMinute)
            if (showSecond) {
                heightSecondFlipper.setTime(heightSecond)
                lowSecondFlipper.setTime(lowSecond)
            }
            timerHandler.sendEmptyMessageDelayed(MSG_WHAT, 1000)
        }
        return true
    }

    init {
        val layoutParams = LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        gravity = Gravity.CENTER
        addView(heightHourFlipper, layoutParams)
        addView(lowHourFlipper, layoutParams)
        addView(hourMinuteDot, layoutParams)
        val margin = 2.5f.dp
        hourMinuteDot.updateLayoutParams<MarginLayoutParams> {
            this.marginStart = margin
            this.marginEnd = margin
        }
        addView(heightMinuteFlipper, layoutParams)
        addView(lowMinuteFlipper, layoutParams)
        addView(minuteSecondDot, layoutParams)
        addView(heightSecondFlipper, layoutParams)
        addView(lowSecondFlipper, layoutParams)
        heightHourFlipper.setMaxTime(3)
        lowHourFlipper.setMaxTime(4)
        heightMinuteFlipper.setMaxTime(6)
        lowMinuteFlipper.setMaxTime(10)
        heightSecondFlipper.setMaxTime(6)
        lowSecondFlipper.setMaxTime(10)
        shouldShowSecond()
    }

    private fun shouldShowSecond() {
        if (showSecond) {
            heightSecondFlipper.visibility = View.VISIBLE
            lowSecondFlipper.visibility = View.VISIBLE
            minuteSecondDot.visibility = View.VISIBLE
            minuteSecondDot.startGlint()
        } else {
            minuteSecondDot.visibility = View.GONE
            heightSecondFlipper.visibility = View.GONE
            lowSecondFlipper.visibility = View.GONE
        }

        hourMinuteDot.startGlint()
        heightHourFlipper.useCompactUI(showSecond)
        lowHourFlipper.useCompactUI(showSecond)
        hourMinuteDot.useCompactUI(showSecond)
        heightMinuteFlipper.useCompactUI(showSecond)
        lowMinuteFlipper.useCompactUI(showSecond)
        minuteSecondDot.useCompactUI(showSecond)
        heightSecondFlipper.useCompactUI(showSecond)
        lowSecondFlipper.useCompactUI(showSecond)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        timerHandler.sendEmptyMessage(MSG_WHAT)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        timerHandler.removeCallbacksAndMessages(null)
    }

    fun updateColor() {
        val textColorInt = Preference.getClockColor(context)
        val bgColorInt = Preference.getClockBgColor(context)
        heightHourFlipper.updateColor(textColorInt, bgColorInt)
        lowHourFlipper.updateColor(textColorInt, bgColorInt)
        hourMinuteDot.setTextColor(textColorInt)
        heightMinuteFlipper.updateColor(textColorInt, bgColorInt)
        lowMinuteFlipper.updateColor(textColorInt, bgColorInt)
        minuteSecondDot.setTextColor(textColorInt)
        heightSecondFlipper.updateColor(textColorInt, bgColorInt)
        lowSecondFlipper.updateColor(textColorInt, bgColorInt)
    }

    private var use12 = false
    fun use12Hour(use: Boolean) {
        use12 = use
        if (use) {
            heightHourFlipper.setMaxTime(2)
            lowHourFlipper.setMaxTime(3)
        } else {
            heightHourFlipper.setMaxTime(3)
            lowHourFlipper.setMaxTime(10)
        }
        heightHourFlipper.reset()
        lowHourFlipper.reset()
    }

}