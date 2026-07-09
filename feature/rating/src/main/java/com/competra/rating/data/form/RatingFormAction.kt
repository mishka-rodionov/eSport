package com.competra.rating.data.form

import com.competra.domain.models.Gender
import com.competra.ui.BaseAction

sealed class RatingFormAction : BaseAction {
    data class NameChanged(val name: String) : RatingFormAction()
    data object AddGroupClick : RatingFormAction()
    data class RemoveGroupClick(val localId: Int) : RatingFormAction()
    data class GroupTitleChanged(val localId: Int, val title: String) : RatingFormAction()
    data class GroupGenderChanged(val localId: Int, val gender: Gender?) : RatingFormAction()
    data class GroupMinAgeChanged(val localId: Int, val minAge: String) : RatingFormAction()
    data class GroupMaxAgeChanged(val localId: Int, val maxAge: String) : RatingFormAction()
    data object Save : RatingFormAction()
}
