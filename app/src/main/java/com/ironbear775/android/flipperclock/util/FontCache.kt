package com.ironbear775.android.flipperclock.util

import android.content.Context
import android.graphics.Typeface
import java.util.*

/**
 * @Author: Xym
 * @Date: 2020/7/8 19:20
 */
object FontCache {
    private val fontCache = HashMap<String, Typeface?>()

    /**
     * get Typeface From Asset
     */
    fun getTypeface(fontName: String, context: Context): Typeface? {
        var typeface = fontCache[fontName]
        if (typeface == null) {
            typeface = try {
                Typeface.createFromAsset(context.assets, "font/$fontName")
            } catch (e: Exception) {
                return null
            }
            fontCache[fontName] = typeface
        }
        return typeface
    }

    /**
     * get Typeface From File
     */
    fun getTypefaceFromFile(fontName: String, filePath: String?): Typeface? {
        var typeface = fontCache[fontName]
        if (typeface == null) {
            typeface = try {
                Typeface.createFromFile(filePath)
            } catch (e: Exception) {
                return null
            }
            fontCache[fontName] = typeface
        }
        return typeface
    }
}