package com.rodionov.center.presentation.read_card

import androidx.lifecycle.viewModelScope
import com.rodionov.center.data.interactors.OrienteeringCompetitionInteractor
import com.rodionov.center.data.read_card.OrientReadCardState
import com.rodionov.data.navigation.Navigation
import com.rodionov.data.navigation.getArguments
import com.rodionov.domain.models.orienteering.ReadChipData
import com.rodionov.nfchelper.SportiduinoHelper
import com.rodionov.ui.BaseAction
import com.rodionov.ui.viewmodel.BaseViewModel
import com.rodionov.utils.constants.EventsConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OrientReadCardViewModel(
    private val sportiduinoHelper: SportiduinoHelper,
    private val orienteeringCompetitionInteractor: OrienteeringCompetitionInteractor,
    navigation: Navigation
) : BaseViewModel<OrientReadCardState>(OrientReadCardState()) {

    val competitionId: Long? = navigation.getArguments<Long>(EventsConstants.EVENT_ID.name)

    override fun onAction(action: BaseAction) {

    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            sportiduinoHelper.subscribeToReadCard { chipData ->
                handleChipData(chipData)
//                updateState { copy(text = text) }
            }
        }
    }

    fun handleChipData(chipData: ReadChipData) {
        when (chipData) {
            is ReadChipData.RawResult -> {
                competitionId?.let {
                    viewModelScope.launch(Dispatchers.IO) {
                        val participant = orienteeringCompetitionInteractor.getParticipantByChipNumber(
                            competitionId = competitionId,
                            chipNumber = chipData.chipNumber
                        )
                    }
                }
            }

            is ReadChipData.MasterChipData -> {}
        }
    }

}