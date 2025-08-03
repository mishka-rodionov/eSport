package com.rodionov.remote.base

import com.google.gson.annotations.SerializedName

class CommonModel<T> : BaseModel() {
    @SerializedName("result")
    val result: T? = null
}