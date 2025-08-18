package com.rodionov.events.presentation.eventDetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.rodionov.data.navigation.Navigation
import com.rodionov.data.navigation.getArguments
import com.rodionov.domain.models.Competition
import com.rodionov.events.presentation.main.DetailsInfo

class EventDetailsViewModel(val savedStateHandle: SavedStateHandle,
    private val navigation: Navigation): ViewModel() {

    val detailInfo: Competition? = navigation.getArguments<Competition>("temp")

}