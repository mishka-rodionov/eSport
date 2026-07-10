package com.competra.rating.data.search

import com.competra.ui.BaseAction

sealed class RatingsSearchAction : BaseAction {
    data object BackClick : RatingsSearchAction()
    data class QueryChanged(val query: String) : RatingsSearchAction()
    data object Search : RatingsSearchAction()
    data object LoadMore : RatingsSearchAction()
    data class RatingClick(val ratingId: String) : RatingsSearchAction()
}
