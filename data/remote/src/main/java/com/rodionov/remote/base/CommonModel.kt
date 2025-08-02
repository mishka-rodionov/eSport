package com.rodionov.remote.base

import com.google.gson.annotations.SerializedName

class CommonModel<T>(
    @SerializedName("result")
    val result: T,
    @SerializedName("status")
    val status: Int,
    @SerializedName("error")
    val error: BaseError
)