package com.rodionov.remote.base

import com.google.gson.annotations.SerializedName

open class BaseModel(
    @SerializedName("status")
    val status: Int? = null,

    @SerializedName("errors")
    val errors: List<BaseError>? = null
) {
    fun getFirstErrorMessage() = errors?.firstOrNull()
}