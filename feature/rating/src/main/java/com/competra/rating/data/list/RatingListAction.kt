package com.competra.rating.data.list

import com.competra.ui.BaseAction

sealed class RatingListAction : BaseAction {
    data object BackClick : RatingListAction()
    data object CreateRatingClick : RatingListAction()
    data class RatingClick(val ratingId: String) : RatingListAction()
}
