package com.rodionov.remote.interceptors

import com.google.gson.Gson
import com.rodionov.domain.models.Gender
import com.rodionov.domain.models.KindOfSport
import com.rodionov.domain.models.orienteering.ControlPointRole
import com.rodionov.domain.models.orienteering.OrienteeringDirection
import com.rodionov.domain.models.orienteering.PunchingSystem
import com.rodionov.domain.models.orienteering.StartTimeMode
import com.rodionov.remote.request.orienteering.OrienteeringCompetitionRequest
import com.rodionov.remote.request.orienteering.ParticipantGroupRequest
import com.rodionov.remote.response.auth.AuthResponse
import com.rodionov.remote.response.auth.TokenResponse
import com.rodionov.remote.response.competition.CompetitionResponse
import com.rodionov.remote.response.competition.CoordinatesResponse
import com.rodionov.remote.response.orienteering.ControlPointResponse
import com.rodionov.remote.response.orienteering.OrienteeringCompetitionResponse
import com.rodionov.remote.response.orienteering.ParticipantGroupResponse
import com.rodionov.remote.response.user.UserResponse
import com.rodionov.utils.DateTimeFormat
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import java.time.LocalDate
import java.time.ZoneId

/**
 * Интерцептор для имитации ответов сервера.
 */
class MockInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val path = request.url.encodedPath

        return when {
            path.contains("user/register") -> registerResponse(request)
            path.contains("user/verify_code") -> verifyCodeResponse(request)
            path.contains("event/orienteering/competitions") -> mockEvents(request)
            path.contains("event/orienteering/save/competitions") -> getOrienteeringCompetitionsResponse(
                request
            )
            path.contains("event/orienteering/save/participantGroup") -> getParticipantGroupResponse(
                request
            )
            else -> chain.proceed(request)
        }
    }

    private fun registerResponse(request: Request): Response {
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val mockJson = """
            {
              "status": 1,
              "result": {}
            }
            """.trimIndent().toResponseBody(mediaType)
        return Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(200)
            .message("OK (mocked by RegisterInterceptor)")
            .body(mockJson)
            .addHeader("Content-Type", "application/json; charset=utf-8")
            .build()
    }

    private fun verifyCodeResponse(request: Request): Response {
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val mockAuthResponse = AuthResponse(
            user = UserResponse(
                id = "12345",
                firstName = "Иван",
                lastName = "Иванов",
                middleName = "Иванович",
                // Используем Long для даты рождения в моковом ответе
                birthDate = DateTimeFormat.transformApiDateToLong("15.05.1990"),
                gender = Gender.MALE,
                photo = "https://example.com/photos/ivan.jpg",
                phoneNumber = "+79998887766",
                email = "ivan.ivanov@example.com",
                qualification = emptyList()
            ),
            token = TokenResponse(
                accessToken = "fake-access-token-123456",
                refreshToken = "fake-refresh-token-abcdef"
            )
        )
        val gson = Gson()
        val mockJson = """
            {
              "status": 1,
              "result": ${gson.toJson(mockAuthResponse)}
            }
            """.trimIndent().toResponseBody(mediaType)

        return Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(200)
            .message("OK (mocked by RegisterInterceptor)")
            .body(mockJson)
            .addHeader("Content-Type", "application/json; charset=utf-8")
            .build()
    }

    private fun mockEvents(request: Request): Response {
        val zoneId = ZoneId.systemDefault()
        val mockOrienteeringResponse = listOf(
            OrienteeringCompetitionResponse(
                competitionId = 1,
                competition = CompetitionResponse(
                    remoteId = 1,
                    title = "Городские соревнования",
                    startDate = LocalDate.parse("2025-08-25").atStartOfDay(zoneId).toInstant().toEpochMilli(),
                    endDate = LocalDate.parse("2025-08-26").atStartOfDay(zoneId).toInstant().toEpochMilli(),
                    kindOfSport = KindOfSport.Orienteering.name,
                    description = "Новые соревнования по ориентированию.",
                    address = "Саратов",
                    mainOrganizerId = 12345L,
                    coordinates = CoordinatesResponse(
                        latitude = 51.3200,
                        longitude = 46.0000
                    ),
                    status = "REGISTRATION",
                    registrationStart = LocalDate.parse("2025-07-01").atStartOfDay(zoneId).toInstant().toEpochMilli(),
                    registrationEnd = LocalDate.parse("2025-08-20").atStartOfDay(zoneId).toInstant().toEpochMilli(),
                    maxParticipants = 300,
                    feeAmount = 500.0,
                    feeCurrency = "RUB",
                    regulationUrl = null,
                    mapUrl = null,
                    contactPhone = "+78452000000",
                    contactEmail = "info@mock.ru",
                    website = null,
                    resultsStatus = "NOT_PUBLISHED"
                ),
                direction = OrienteeringDirection.FORWARD.name,
                punchingSystem = PunchingSystem.SPORTIDUINO,
                startTimeMode = StartTimeMode.STRICT
            ),
            OrienteeringCompetitionResponse(
                competitionId = 2,
                direction = OrienteeringDirection.FORWARD.name,
                punchingSystem = PunchingSystem.SPORTIDUINO,
                startTimeMode = StartTimeMode.STRICT,
                competition = CompetitionResponse(
                    remoteId = 2,
                    title = "Городские соревнования #2",
                    startDate = LocalDate.parse("2025-08-27").atStartOfDay(zoneId).toInstant().toEpochMilli(),
                    endDate = LocalDate.parse("2025-08-28").atStartOfDay(zoneId).toInstant().toEpochMilli(),
                    kindOfSport = KindOfSport.Orienteering.name,
                    description = "Новые соревнования по ориентированию.",
                    address = "Балашов",
                    mainOrganizerId = 12345L,
                    coordinates = CoordinatesResponse(
                        latitude = 51.3200,
                        longitude = 46.0000
                    ),
                    status = "CREATED",
                    registrationStart = null,
                    registrationEnd = null,
                    maxParticipants = null,
                    feeAmount = null,
                    feeCurrency = null,
                    regulationUrl = null,
                    mapUrl = null,
                    contactPhone = null,
                    contactEmail = null,
                    website = null,
                    resultsStatus = "NOT_PUBLISHED"
                )
            )
        )

        val gson = Gson()
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val mockJson = """
            {
              "status": 1,
              "result": ${gson.toJson(mockOrienteeringResponse)}
            }
            """.trimIndent().toResponseBody(mediaType)
        return Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(200)
            .message("OK (mocked by MockInterceptor)")
            .body(mockJson)
            .addHeader("Content-Type", "application/json; charset=utf-8")
            .build()
    }

    private fun getOrienteeringCompetitionsResponse(request: Request): Response {
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val bodyString = request.body?.let { body ->
            val buffer = okio.Buffer()
            body.writeTo(buffer)
            buffer.readUtf8()
        }

        val gson = Gson()
        val competitionRequest = try {
            gson.fromJson(bodyString, OrienteeringCompetitionRequest::class.java)
        } catch (e: Exception) {
            null
        }

        val responseBody = OrienteeringCompetitionResponse(
            competitionId = (1000..9999).random().toLong(),
            direction = competitionRequest?.direction ?: OrienteeringDirection.FORWARD.name,
            punchingSystem = PunchingSystem.SPORTIDUINO,
            startTimeMode = StartTimeMode.valueOf(competitionRequest?.startTimeMode ?: StartTimeMode.STRICT.name),
            competition = CompetitionResponse(
                remoteId = (1..99).random().toLong(),
                title = competitionRequest?.competition?.title ?: "Mocked Competition",
                startDate = competitionRequest?.competition?.startDate ?: System.currentTimeMillis(),
                endDate = competitionRequest?.competition?.endDate,
                kindOfSport = competitionRequest?.competition?.kindOfSport
                    ?: KindOfSport.Orienteering.name,
                description = competitionRequest?.competition?.description ?: "Mocked Description",
                address = competitionRequest?.competition?.address ?: "Mocked Address",
                mainOrganizerId = 12345L,
                coordinates = CoordinatesResponse(
                    latitude = competitionRequest?.competition?.coordinates?.latitude ?: 51.0,
                    longitude = competitionRequest?.competition?.coordinates?.longitude ?: 45.0
                ),
                status = competitionRequest?.competition?.status ?: "CREATED",
                registrationStart = competitionRequest?.competition?.registrationStart,
                registrationEnd = competitionRequest?.competition?.registrationEnd,
                maxParticipants = competitionRequest?.competition?.maxParticipants,
                feeAmount = competitionRequest?.competition?.feeAmount,
                feeCurrency = competitionRequest?.competition?.feeCurrency,
                regulationUrl = competitionRequest?.competition?.regulationUrl,
                mapUrl = competitionRequest?.competition?.mapUrl,
                contactPhone = competitionRequest?.competition?.contactPhone,
                contactEmail = competitionRequest?.competition?.contactEmail,
                website = competitionRequest?.competition?.website,
                resultsStatus = competitionRequest?.competition?.resultsStatus ?: "NOT_PUBLISHED"
            )
        )

        val mockJson = """
        {
          "status": 1,
          "result": ${gson.toJson(responseBody)}
        }
    """.trimIndent().toResponseBody(mediaType)

        return Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(200)
            .message("OK (mocked createOrienteeringCompetition)")
            .body(mockJson)
            .addHeader("Content-Type", "application/json; charset=utf-8")
            .build()
    }

    private fun getParticipantGroupResponse(request: Request): Response {
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val gson = Gson()
        val bodyString = request.body?.let { body ->
            val buffer = okio.Buffer()
            body.writeTo(buffer)
            buffer.readUtf8()
        }

        val participantGroups = gson.fromJson(bodyString, Array<ParticipantGroupRequest>::class.java)
        val mockGroupsResponse = participantGroups.map {
            ParticipantGroupResponse(
                groupId = (1000..9999).random().toLong(),
                competitionId = it.competitionId,
                title = it.title,
                distance = it.distance,
                countOfControls = it.countOfControls,
                maxTimeInMinute = it.maxTimeInMinute,
                controlPoints = listOf(
                    ControlPointResponse(49, ControlPointRole.ORDINARY, 0),
                    ControlPointResponse(52, ControlPointRole.ORDINARY, 0),
                    ControlPointResponse(53, ControlPointRole.ORDINARY, 0),
                    ControlPointResponse(54, ControlPointRole.ORDINARY, 0)
                )
            )
        }

        val mockJson = """
        {
          "status": 1,
          "result": ${gson.toJson(mockGroupsResponse)}
        }
    """.trimIndent().toResponseBody(mediaType)

        return Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(200)
            .message("OK (mocked createOrienteeringCompetition)")
            .body(mockJson)
            .addHeader("Content-Type", "application/json; charset=utf-8")
            .build()
    }
}
