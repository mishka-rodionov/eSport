package com.rodionov.resources

import android.content.Context

class ResourceProviderImpl(private val context: Context) : ResourceProvider {

    override fun getString(id: Int) = context.getString(id)

    override fun getString(id: Int, vararg args: Any) = context.getString(id, *args)

    override fun coloredHtmlString(s: String?, color: String): String {
        return String.format("<font color=\"%s\">%s</font>", color, s)
    }

    override fun colorToHexCode(color: Int): String {
        return java.lang.String.format("#%06x", context.getColor(color) and 0x00ffffff)
    }
}