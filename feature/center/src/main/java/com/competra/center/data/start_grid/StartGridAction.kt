package com.competra.center.data.start_grid

import com.competra.ui.BaseAction

/**
 * Действия на экране «Стартовая решётка».
 */
sealed class StartGridAction : BaseAction {

    /** Перезагрузить данные соревнования и участников. */
    data object Reload : StartGridAction()

    /** Изменён текст поиска по стартовому номеру. */
    data class SearchChanged(val value: String) : StartGridAction()
}
