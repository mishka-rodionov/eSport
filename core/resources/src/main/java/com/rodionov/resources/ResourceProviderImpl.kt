package com.rodionov.resources

import android.content.Context

class ResourceProviderImpl(private val context: Context) : ResourceProvider {

    override fun getString(id: Int) = context.getString(id)

    override fun getString(id: Int, vararg args: Any) = context.getString(id, args)
}