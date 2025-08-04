package com.rodionov.center.data

data class OrienteeringCreatorErrors(
    val isEmptyAddress: Boolean = false,
    val isEmptyGroup: Boolean = false,
    val isGroupTitleError: Boolean = false,
    val isGroupDistanceError: Boolean = false,
    val isCountOfControlsError: Boolean = false,
    val isMaxTimeError: Boolean = false
) {

    fun checkErrors() : Boolean {
        return !isEmptyAddress && !isEmptyGroup && !isGroupTitleError && !isGroupDistanceError && !isCountOfControlsError && !isMaxTimeError
    }

}
