package com.ironbear775.android.flipperclock

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.ironbear775.android.flipperclock.util.FontCache
import com.ironbear775.android.flipperclock.util.Preference
import com.ironbear775.android.flipperclock.util.TYPEFACE
import com.ironbear775.android.flipperclock.util.dpF
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private val receiver = TimeBroadCastReceiver()
    private val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        settingBtn.setOnClickListener {
            startActivity(Intent(this, SettingActivity::class.java))
        }
        dateView.typeface = FontCache.getTypeface(TYPEFACE, this)
        amPmView.typeface = FontCache.getTypeface(TYPEFACE, this)
        batteryTextView.typeface = FontCache.getTypeface(TYPEFACE, this)

        val calendarDate = Calendar.getInstance()
        val date = format.format(calendarDate.time)
        val hourOfDay = calendarDate.get(Calendar.HOUR_OF_DAY)
        checkBrightness(hourOfDay)
        dateView.text = date
        amPmView.text = resources.getString(if (hourOfDay < 13) R.string.am else R.string.pm)
        val battery =
            (getSystemService(BATTERY_SERVICE) as BatteryManager).getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        updateBattery(battery)

        receiver.register(this, {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val dateString = format.format(calendar.time)
            dateView.text = dateString
            checkBrightness(hour)
            shakeForScreenProtection()
        }, {
            updateBattery(it)
        })
    }

    private fun updateBattery(battery: Int) {
        batteryView.setBatteryRemain(battery)
        batteryTextView.text = resources.getString(R.string.battery, battery)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemUI()
    }

    private fun hideSystemUI() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

    private fun checkBrightness(hour: Int) {
        changeScreenBrightness(hour !in 7..22)
    }

    override fun onResume() {
        super.onResume()
        val clockColor = Preference.getClockColor(this)
        val bgColor = Preference.getBgColor(this)
        dateView.setTextColor(clockColor)
        amPmView.setTextColor(clockColor)
        batteryTextView.setTextColor(clockColor)
        batteryView.setColor(clockColor)
        timerLayout.updateColor()
        val use12 = Preference.getUse12Hour()
        timerLayout.use12Hour(use12)
        if (use12) {
            amPmView.visibility = View.VISIBLE
        } else {
            amPmView.visibility = View.GONE
        }
        if (Preference.getShowDate())
            dateView.visibility = View.VISIBLE
        else
            dateView.visibility = View.GONE
        timerLayout.showSecond = Preference.getShowSecond()
        window.decorView.setBackgroundColor(bgColor)
    }

    private fun shakeForScreenProtection() {
        if (rootLayout.translationX == 0f) {
            rootLayout.translationX = .5f.dpF
            rootLayout.translationY = .5f.dpF
        } else {
            rootLayout.translationX = 0f
            rootLayout.translationY = 0f
        }
    }

    private fun changeScreenBrightness(dark: Boolean) {
        val params = window.attributes
        params.screenBrightness = if (dark) 0f else Settings.System.getInt(
            contentResolver,
            Settings.System.SCREEN_BRIGHTNESS
        ).toFloat() / 100
        window.attributes = params
    }

    override fun onDestroy() {
        super.onDestroy()
        receiver.unregister(this)
    }

    inner class TimeBroadCastReceiver : BroadcastReceiver() {
        private var onOnTimeChange: (() -> Unit)? = null
        private var onBatteryChange: ((battery: Int) -> Unit)? = null
        fun register(
            context: Context,
            onOnTimeChange: (() -> Unit),
            onBatteryChange: ((battery: Int) -> Unit)
        ) {
            val intentFilter = IntentFilter()
            intentFilter.addAction(Intent.ACTION_TIME_TICK)
            intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED)
            intentFilter.addAction(Intent.ACTION_TIME_CHANGED)
            intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED)
            context.registerReceiver(this, intentFilter)
            this.onOnTimeChange = onOnTimeChange
            this.onBatteryChange = onBatteryChange
        }

        fun unregister(context: Context) {
            context.unregisterReceiver(this)
        }

        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                when (it.action) {
                    Intent.ACTION_TIME_CHANGED, Intent.ACTION_TIMEZONE_CHANGED, Intent.ACTION_TIME_TICK -> {
                        onOnTimeChange?.invoke()
                    }
                    Intent.ACTION_BATTERY_CHANGED -> {
                        onBatteryChange?.invoke(intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0))
                    }
                    else -> {

                    }
                }
            }
        }
    }
}