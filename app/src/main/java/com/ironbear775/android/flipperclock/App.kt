package com.ironbear775.android.flipperclock

import android.app.Application
import com.ironbear775.android.flipperclock.util.Preference

/**
 * @Author: Xym
 * @Date: 2020/7/8 19:49
 */
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Preference.init(this)
    }
}