package com.competra.domain.models

/** Страница списка: элементы текущей страницы + признак наличия следующей. */
data class PagedResult<T>(
    val items: List<T>,
    val hasMore: Boolean
)
