package com.competra.rating.data.add_competition

import com.competra.ui.BaseAction

sealed class AddCompetitionAction : BaseAction {
    data object BackClick : AddCompetitionAction()
    data class QueryChanged(val query: String) : AddCompetitionAction()
    data object Search : AddCompetitionAction()
    data class CompetitionClick(val competitionId: String) : AddCompetitionAction()
}
