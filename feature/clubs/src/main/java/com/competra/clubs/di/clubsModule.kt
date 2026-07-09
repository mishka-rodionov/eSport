package com.competra.clubs.di

import com.competra.clubs.presentation.detail.ClubDetailViewModel
import com.competra.clubs.presentation.form.ClubFormViewModel
import com.competra.clubs.presentation.join_requests.ClubJoinRequestsViewModel
import com.competra.clubs.presentation.list.ClubsListViewModel
import com.competra.clubs.presentation.my_requests.MyJoinRequestsViewModel
import com.competra.clubs.presentation.team_detail.TeamDetailViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val clubsModule = module {
    viewModelOf(::ClubsListViewModel)
    viewModelOf(::ClubFormViewModel)
    viewModelOf(::ClubDetailViewModel)
    viewModelOf(::ClubJoinRequestsViewModel)
    viewModelOf(::MyJoinRequestsViewModel)
    viewModelOf(::TeamDetailViewModel)
}
