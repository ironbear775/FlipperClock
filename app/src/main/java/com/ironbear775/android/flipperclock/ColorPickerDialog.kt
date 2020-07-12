package com.ironbear775.android.flipperclock

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import com.ironbear775.android.flipperclock.util.Preference
import kotlinx.android.synthetic.main.dialog_color_picker.*

/**
 * @Author: Xym
 * @Date: 2020/7/9 15:23
 */
class ColorPickerDialog(context: Context) : Dialog(context) {
    companion object {
        const val TYPE_CLOCK = 1
        const val TYPE_CLOCK_BG = 2
        const val TYPE_BG = 4
    }

    private var typeKey = TYPE_CLOCK

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_color_picker)

        saveBtn.setOnClickListener {
            when (typeKey) {
                TYPE_CLOCK -> Preference.saveClockColor(colorPickerView.selectedColor)
                TYPE_CLOCK_BG -> Preference.saveClockBgColor(colorPickerView.selectedColor)
                TYPE_BG -> Preference.saveBgColor(colorPickerView.selectedColor)
            }
            Toast.makeText(context, R.string.save_success, Toast.LENGTH_SHORT).show()
            dismiss()
        }
        colorPickerView.setAlphaSlider(alphaSlider)
        colorPickerView.setLightnessSlider(lightnessSlider)
        cancelBtn.setOnClickListener {
            dismiss()
        }

    }

    override fun onStart() {
        super.onStart()
        when (typeKey) {
            TYPE_CLOCK -> {
                colorPickerView.setColorPreview(previewView, Preference.getClockColor(context))
                colorPickerTitle.text = context.resources.getString(R.string.set_clock_color)
            }
            TYPE_CLOCK_BG -> {
                colorPickerView.setColorPreview(previewView, Preference.getClockBgColor(context))
                colorPickerView.selectedColor
                colorPickerTitle.text = context.resources.getString(R.string.set_clock_bg_color)
            }
            TYPE_BG -> {
                colorPickerView.setColorPreview(previewView, Preference.getBgColor(context))
                colorPickerTitle.text = context.resources.getString(R.string.set_bg_color)
            }
        }
    }

    fun showWithType(type: Int) {
        this.typeKey = type
        show()
    }
}