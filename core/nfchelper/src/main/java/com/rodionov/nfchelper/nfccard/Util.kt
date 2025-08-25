package com.rodionov.nfchelper.nfccard

import android.content.Context
import androidx.core.content.ContextCompat
import com.rodionov.nfchelper.nfccard.Constants.OPERATED_YEAR_MIN
import com.rodionov.resources.R

object Util {
    @JvmField
    var dformat: java.text.SimpleDateFormat = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    fun byteToUint(b: Byte): Int {
        return (b.toInt()) and 0xFF
    }

    @JvmStatic
    fun toUint32(bytes: ByteArray): Long {
        val buffer = java.nio.ByteBuffer.allocate(8).put(byteArrayOf(0, 0, 0, 0))
            .put(java.util.Arrays.copyOfRange(bytes, 0, 4))
        buffer.position(0)
        return buffer.getLong()
    }

    fun toUint16(b1: Byte, b2: Byte): Int {
        var ret = (b1.toInt() and 0xff) shl 8
        ret = ret or (b2.toInt() and 0xff)
        return ret
    }

    @JvmStatic
    fun fromUint32(value: Long): ByteArray {
        val bytes = ByteArray(8)
        java.nio.ByteBuffer.wrap(bytes).putLong(value)
        return java.util.Arrays.copyOfRange(bytes, 4, 8)
    }

    @JvmStatic
    fun fromUint16(value: Int): ByteArray {
        val bytes = ByteArray(4)
        java.nio.ByteBuffer.wrap(bytes).putInt(value)
        return java.util.Arrays.copyOfRange(bytes, 2, 4)
    }

    fun capitalize(s: String): String {
        return s.substring(0, 1).uppercase(java.util.Locale.getDefault()) + s.substring(1)
            .lowercase(java.util.Locale.getDefault())
    }

    fun colorString(s: String, bgColor: Int): android.text.SpannableString {
        val spannableString = android.text.SpannableString(s)
        spannableString.setSpan(
            android.text.style.BackgroundColorSpan(bgColor),
            0,
            s.length,
            android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return spannableString
    }

    fun error(s: String, context: Context): android.text.SpannableString {
        return colorString(s, context.getColor(R.color.error_text_bg))
    }

    fun error(s: String, view: android.view.View): android.text.SpannableString {
        val color: Int = ContextCompat.getColor(view.getContext(), R.color.error_text_bg)

        return colorString(s, color)
    }

    fun ok(s: String, context: Context): android.text.SpannableString {
        return colorString(s, context.getColor(R.color.ok_text_bg))
    }

    fun ok(s: String, view: android.view.View): android.text.SpannableString {
        val color: Int = ContextCompat.getColor(view.getContext(), R.color.ok_text_bg)

        return colorString(s, color)
    }

    fun coloredHtmlString(s: String?, color: String): String {
        return String.format("<font color=\"%s\">%s</font>", color, s)
    }

    fun colorToHexCode(color: Int, context: Context): String {
        return java.lang.String.format("#%06x", context.getColor(color) and 0x00ffffff)
    }

    fun checkOperatedYearMin(): Boolean {
        val calendar = java.util.Calendar.getInstance()

        return calendar.get(java.util.Calendar.YEAR) >= OPERATED_YEAR_MIN
    }

    interface Callback {
        fun call(str: CharSequence?)
    }
}

