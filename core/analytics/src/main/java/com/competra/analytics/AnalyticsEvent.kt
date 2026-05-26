package com.competra.analytics

/**
 * Типобезопасный словарь продуктовых событий приложения.
 *
 * Каждый кейс — отдельное событие со своим именем и параметрами.
 * Имя события в AppMetrica совпадает с `eventName`, параметры передаются
 * картой `params` (snake_case-ключи).
 */
sealed class AnalyticsEvent(
    val eventName: String,
    val params: Map<String, Any?> = emptyMap(),
) {

    // region Auth / Registration

    /** Пользователь запросил отправку кода авторизации (param: домен email). */
    class AuthLoginRequested(emailDomain: String) :
        AnalyticsEvent("auth_login_requested", mapOf("email_domain" to emailDomain))

    /** Пользователь отправил полученный код на проверку. */
    data object AuthCodeSubmitted : AnalyticsEvent("auth_code_submitted")

    /** Успешная авторизация. */
    data object AuthLoginSuccess : AnalyticsEvent("auth_login_success")

    enum class AuthFailureReason { INVALID_CODE, NETWORK, SERVER, UNKNOWN }

    /** Авторизация завершилась ошибкой. */
    class AuthLoginFailed(reason: AuthFailureReason) :
        AnalyticsEvent("auth_login_failed", mapOf("reason" to reason.name.lowercase()))

    /** Пользователь отправил форму регистрации. */
    data object RegistrationSubmitted : AnalyticsEvent("registration_submitted")

    /** Регистрация завершилась успехом. */
    data object RegistrationSuccess : AnalyticsEvent("registration_success")

    /** Пользователь вышел из аккаунта. */
    data object Logout : AnalyticsEvent("logout")

    // endregion

    // region Events (просмотр / участие)

    enum class EventSource { LIST, PUSH, DEEPLINK }

    /** Открыта карточка соревнования. */
    class EventOpened(eventId: Long, source: EventSource) : AnalyticsEvent(
        "event_opened",
        mapOf("event_id" to eventId, "source" to source.name.lowercase()),
    )

    /** Применён фильтр в списке соревнований. */
    class EventFilterApplied(filters: Map<String, Any?>) :
        AnalyticsEvent("event_filter_applied", filters)

    /** Нажата кнопка «зарегистрироваться» в карточке соревнования. */
    class EventRegisterClicked(eventId: Long) :
        AnalyticsEvent("event_register_clicked", mapOf("event_id" to eventId))

    /** Открыт экран live-результатов. */
    class EventLiveResultsOpened(eventId: Long) :
        AnalyticsEvent("event_live_results_opened", mapOf("event_id" to eventId))

    // endregion

    // region Create competition (центр)

    /** Начато создание соревнования. */
    class CreateCompetitionStarted(kindOfSport: String) :
        AnalyticsEvent("create_competition_started", mapOf("kind_of_sport" to kindOfSport))

    enum class CreateCompetitionStep { COMMON, REGISTRATION, ORGANIZATOR, DISTANCE, GROUPS }

    /** Завершён очередной шаг мастера создания. */
    class CreateCompetitionStepCompleted(step: CreateCompetitionStep) : AnalyticsEvent(
        "create_competition_step_completed",
        mapOf("step" to step.name.lowercase()),
    )

    /** Мастер создания соревнования прошёл до конца. */
    class CreateCompetitionFinished(competitionId: Long, kindOfSport: String) : AnalyticsEvent(
        "create_competition_finished",
        mapOf("competition_id" to competitionId, "kind_of_sport" to kindOfSport),
    )

    // endregion

    // region NFC / чипы

    enum class NfcCardType { MIFARE_CLASSIC, MIFARE_ULTRALIGHT, PARTICIPANT, MASTER, UNKNOWN }

    /** Зафиксирована попытка чтения NFC-чипа. */
    class NfcChipReadAttempted(cardType: NfcCardType) : AnalyticsEvent(
        "nfc_chip_read_attempted",
        mapOf("card_type" to cardType.name.lowercase()),
    )

    /** Успешное чтение чипа. */
    data object NfcChipReadSuccess : AnalyticsEvent("nfc_chip_read_success")

    /** Чтение чипа завершилось ошибкой. */
    class NfcChipReadFailed(reason: String) :
        AnalyticsEvent("nfc_chip_read_failed", mapOf("reason" to reason))

    /** Чип привязан к участнику. */
    data object NfcChipAssigned : AnalyticsEvent("nfc_chip_assigned")

    enum class ParticipantAddMethod { MANUAL, NFC, IMPORT }

    /** Добавлен участник в соревнование. */
    class ParticipantAdded(method: ParticipantAddMethod) :
        AnalyticsEvent("participant_added", mapOf("method" to method.name.lowercase()))

    /** Выполнена жеребьёвка. */
    class ParticipantDrawn(participantsCount: Int) :
        AnalyticsEvent("participant_drawn", mapOf("participants_count" to participantsCount))

    // endregion

    // region Results

    /** Просмотр результатов соревнования. */
    class ResultsViewed(eventId: Long) :
        AnalyticsEvent("results_viewed", mapOf("event_id" to eventId))

    /** Время отметки на КП отредактировано вручную при считывании чипа. */
    data object SplitEditSaved : AnalyticsEvent("split_edit_saved")

    /** Отметка на КП удалена вручную при считывании чипа. */
    data object SplitDeleted : AnalyticsEvent("split_deleted")

    // endregion

    // region Profile

    /** Открыт редактор профиля. */
    data object ProfileEditStarted : AnalyticsEvent("profile_edit_started")

    /** Сохранены изменения в профиле. */
    data object ProfileEditSaved : AnalyticsEvent("profile_edit_saved")

    // endregion
}
