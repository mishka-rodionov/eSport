package com.competra.remote.base

import com.google.gson.annotations.SerializedName

/** Страница списка с бэкенда: элементы + признак наличия следующей страницы. */
data class PagedResponse<T>(
    @SerializedName("items")
    val items: List<T>,
    @SerializedName("hasMore")
    val hasMore: Boolean
)
