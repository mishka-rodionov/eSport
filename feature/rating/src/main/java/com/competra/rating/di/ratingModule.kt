package com.competra.rating.di

import com.competra.rating.presentation.add_competition.AddCompetitionViewModel
import com.competra.rating.presentation.detail.RatingDetailViewModel
import com.competra.rating.presentation.form.RatingFormViewModel
import com.competra.rating.presentation.group_mapping.GroupMappingViewModel
import com.competra.rating.presentation.list.RatingListViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val ratingModule = module {
    viewModelOf(::RatingListViewModel)
    viewModelOf(::RatingFormViewModel)
    viewModelOf(::RatingDetailViewModel)
    viewModelOf(::AddCompetitionViewModel)
    viewModelOf(::GroupMappingViewModel)
}
