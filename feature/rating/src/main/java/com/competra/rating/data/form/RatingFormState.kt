package com.competra.rating.data.form

import com.competra.domain.models.Gender
import com.competra.ui.BaseState

data class RatingGroupDraft(
    val localId: Int,
    val title: String = "",
    val gender: Gender? = null,
    val minAge: String = "",
    val maxAge: String = ""
)

data class RatingFormState(
    val clubId: String = "",
    val name: String = "",
    val nameError: Boolean = false,
    val groups: List<RatingGroupDraft> = emptyList(),
    val groupsError: Boolean = false,
    val isSaving: Boolean = false
) : BaseState
