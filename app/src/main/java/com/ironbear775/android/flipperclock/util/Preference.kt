package com.ironbear775.android.flipperclock.util

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.ContextCompat
import com.ironbear775.android.flipperclock.R

/**
 * @Author: Xym
 * @Date: 2020/7/8 19:41
 */
object Preference {
    private var sharedPreferences: SharedPreferences? = null
    private const val key = "flip timer"
    private const val clockColorKey = "flip timer text color"
    private const val clockBgColorKey = "flip timer bg color"
    private const val bgColorKey = "flip bg color"
    private const val use12 = "flip use 12"
    private const val showDate = "flip show date"
    private const val showSecond = "flip show second"

    fun init(appContext: Context) {
        sharedPreferences =
            appContext.applicationContext.getSharedPreferences(key, Context.MODE_PRIVATE)
    }

    fun saveClockColor(colorInt: Int) {
        sharedPreferences?.edit()?.putInt(clockColorKey, colorInt)?.apply()
    }

    fun getClockColor(context: Context): Int {
        val defaultColor = ContextCompat.getColor(context, R.color.flipperTextColor)
        return sharedPreferences?.getInt(clockColorKey, defaultColor) ?: defaultColor
    }

    fun saveClockBgColor(colorInt: Int) {
        sharedPreferences?.edit()?.putInt(clockBgColorKey, colorInt)?.apply()
    }

    fun getClockBgColor(context: Context): Int {
        val defaultColor = ContextCompat.getColor(context, R.color.flipperBgColor)
        return sharedPreferences?.getInt(clockBgColorKey, defaultColor) ?: defaultColor
    }

    fun saveBgColor(colorInt: Int) {
        sharedPreferences?.edit()?.putInt(bgColorKey, colorInt)?.apply()
    }

    fun getBgColor(context: Context): Int {
        val defaultColor = ContextCompat.getColor(context, R.color.backgroundColor)
        return sharedPreferences?.getInt(bgColorKey, defaultColor) ?: defaultColor
    }

    fun getUse12Hour() = sharedPreferences?.getBoolean(use12, false) ?: false

    fun setUse12Hour(use: Boolean) = sharedPreferences?.edit()?.putBoolean(use12, use)?.apply()

    fun getShowDate() = sharedPreferences?.getBoolean(showDate, true) ?: true

    fun setShowDate(use: Boolean) = sharedPreferences?.edit()?.putBoolean(showDate, use)?.apply()

    fun getShowSecond() = sharedPreferences?.getBoolean(showSecond, false) ?: true

    fun setShowSecond(use: Boolean) =
        sharedPreferences?.edit()?.putBoolean(showSecond, use)?.apply()
}