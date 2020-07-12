package com.ironbear775.android.flipperclock

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.ironbear775.android.flipperclock.util.Preference
import kotlinx.android.synthetic.main.activity_setting.*


/**
 * @Author: Xym
 * @Date: 2020/7/8 17:12
 */
class SettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        toolbar.setNavigationOnClickListener {
            finish()
        }

        changeClockColorBtn.setOnClickListener {
            ColorPickerDialog(this).showWithType(ColorPickerDialog.TYPE_CLOCK)
        }

        changeClockBgColorBtn.setOnClickListener {
            ColorPickerDialog(this).showWithType(ColorPickerDialog.TYPE_CLOCK_BG)
        }

        changeBgColorBtn.setOnClickListener {
            ColorPickerDialog(this).showWithType(ColorPickerDialog.TYPE_BG)
        }

        clockColorSetBtn.setOnClickListener {
            Preference.saveClockColor(ContextCompat.getColor(this, R.color.flipperTextColor))
            Toast.makeText(this, R.string.save_success, Toast.LENGTH_SHORT).show()
        }

        clockBgColorSetBtn.setOnClickListener {
            Preference.saveClockBgColor(ContextCompat.getColor(this, R.color.flipperBgColor))
            Toast.makeText(this, R.string.save_success, Toast.LENGTH_SHORT).show()
        }

        bgColorSetBtn.setOnClickListener {
            Preference.saveBgColor(ContextCompat.getColor(this, R.color.backgroundColor))
            Toast.makeText(this, R.string.save_success, Toast.LENGTH_SHORT).show()
        }

        use12HourSwitch.isChecked = Preference.getUse12Hour()
        use12HourSwitch.setOnCheckedChangeListener { _, isChecked ->
            Preference.setUse12Hour(isChecked)
        }
        showDateSwitch.isChecked = Preference.getShowDate()
        showDateSwitch.setOnCheckedChangeListener { _, isChecked ->
            Preference.setShowDate(isChecked)
        }
        showSecondSwitch.isChecked = Preference.getShowSecond()
        showSecondSwitch.setOnCheckedChangeListener { _, isChecked ->
            Preference.setShowSecond(isChecked)
        }
    }
}