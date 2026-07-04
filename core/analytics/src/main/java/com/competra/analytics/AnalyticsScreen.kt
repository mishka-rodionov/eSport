package com.competra.analytics

/**
 * Каталог экранов приложения для трекинга `screen_view`.
 *
 * Используем enum, а не строковые литералы в местах вызова, чтобы изменения
 * словаря шли через единый файл и компилятор проверял корректность.
 */
enum class AnalyticsScreen(val screenName: String) {
    // Events
    EventsList("events_list"),
    EventDetails("event_details"),
    EventParticipantGroup("event_participant_group"),
    EventResults("event_results"),
    EventLiveResults("event_live_results"),
    EventGroupSplitsTable("event_group_splits_table"),

    // Center
    CenterHome("center_home"),
    CenterKindOfSport("center_kind_of_sport"),
    CreateCompetitionCommon("create_competition_common"),
    CreateCompetitionRegistration("create_competition_registration"),
    CreateCompetitionOrganizator("create_competition_organizator"),
    CreateCompetitionDistance("create_competition_distance"),
    CreateCompetitionGroups("create_competition_groups"),
    MapPicker("map_picker"),
    EventControl("event_control"),
    NfcReadCard("nfc_read_card"),
    ParticipantList("participant_list"),
    ParticipantDraw("participant_draw"),
    StartGrid("start_grid"),
    CenterResults("center_results"),
    ParticipantSplits("participant_splits"),
    GroupSplitsTable("group_splits_table"),
    GetChip("get_chip"),

    // Profile
    ProfileHome("profile_home"),
    ProfileEditor("profile_editor"),
    AboutApp("about_app"),
    Auth("auth"),
    AuthCode("auth_code"),
    Registration("registration"),
    UserRegistrations("user_registrations");

    companion object {
        /**
         * Сопоставляет route Compose Navigation (fully-qualified имя класса
         * sealed-route) с экраном из словаря.
         *
         * Compose Navigation 2.8 для type-safe маршрутов в `destination.route`
         * подставляет имя класса с параметрами, поэтому используем `contains`.
         */
        fun fromRoute(route: String?): AnalyticsScreen? {
            if (route.isNullOrBlank()) return null
            return when {
                route.contains("EventsRoute") -> EventsList
                route.contains("EventDetailsRoute") -> EventDetails
                route.contains("EventParticipantGroupRoute") -> EventParticipantGroup
                route.contains("EventResultsRoute") -> EventResults
                route.contains("LiveResultsRoute") -> EventLiveResults
                route.contains("EventSplitsTableRoute") -> EventGroupSplitsTable

                route.contains("CenterRoute") -> CenterHome
                route.contains("KindOfSportRoute") -> CenterKindOfSport
                route.contains("CommonCompetitionFieldRoute") -> CreateCompetitionCommon
                route.contains("RegistrationCompetitionFieldRoute") -> CreateCompetitionRegistration
                route.contains("OrganizatorCompetitionFieldRoute") -> CreateCompetitionOrganizator
                route.contains("CreateDistanceRoute") -> CreateCompetitionDistance
                route.contains("CreateParticipantGroupRoute") -> CreateCompetitionGroups
                route.contains("MapPickerRoute") -> MapPicker
                route.contains("OrienteeringEventControlRoute") -> EventControl
                route.contains("OrientReadCardRoute") -> NfcReadCard
                route.contains("ParticipantList") -> ParticipantList
                route.contains("StartGridRoute") -> StartGrid
                route.contains("DrawParticipants") -> ParticipantDraw
                route.contains("ParticipantResults") -> CenterResults
                route.contains("ParticipantSplitsRoute") -> ParticipantSplits
                route.contains("GroupSplitsTableRoute") -> GroupSplitsTable
                route.contains("GetOrienteeringChipRoute") -> GetChip

                route.contains("MainProfileRoute") -> ProfileHome
                route.contains("ProfileEditorRoute") -> ProfileEditor
                route.contains("AboutAppRoute") -> AboutApp
                route.contains("AuthCodeRoute") -> AuthCode
                route.contains("AuthRoute") -> Auth
                route.contains("RegistrationRoute") -> Registration
                route.contains("UserRegistrationsRoute") -> UserRegistrations

                else -> null
            }
        }
    }
}
