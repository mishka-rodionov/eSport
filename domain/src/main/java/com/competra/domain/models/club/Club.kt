package com.competra.domain.models.club

/**
 * Клуб — постоянное сообщество, объединяющее участников. Всегда публичен (виден в поиске/списке всем).
 *
 * @property foundedAt Заполняется автоматически датой создания клуба, руками не задаётся.
 * @property allowJoinRequests Разрешён ли приём заявок на вступление извне.
 */
data class Club(
    val id: String,
    val name: String,
    val description: String?,
    val allowJoinRequests: Boolean,
    val foundedAt: Long,
    val membersCount: Int
)
