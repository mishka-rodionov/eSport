package com.rodionov.remote.interceptors

import com.google.gson.Gson
import com.rodionov.domain.models.Competition
import com.rodionov.domain.models.Coordinates
import com.rodionov.domain.models.Gender
import com.rodionov.domain.models.KindOfSport
import com.rodionov.domain.models.SportsCategory
import com.rodionov.domain.models.orienteering.OrienteeringCompetition
import com.rodionov.domain.models.orienteering.OrienteeringDirection
import com.rodionov.domain.models.orienteering.PunchingSystem
import com.rodionov.remote.base.CommonModel
import com.rodionov.remote.request.orienteering.OrienteeringCompetitionRequest
import com.rodionov.remote.request.orienteering.ParticipantGroupRequest
import com.rodionov.remote.response.auth.AuthResponse
import com.rodionov.remote.response.auth.TokenResponse
import com.rodionov.remote.response.competition.CompetitionResponse
import com.rodionov.remote.response.competition.CoordinatesResponse
import com.rodionov.remote.response.orienteering.OrienteeringCompetitionResponse
import com.rodionov.remote.response.orienteering.ParticipantGroupResponse
import com.rodionov.remote.response.user.QualificationResponse
import com.rodionov.remote.response.user.UserResponse
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import java.nio.Buffer
import java.time.LocalDate

class MockInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val path = request.url.encodedPath // например: "/api/v1/register"

        // Простая проверка: точное совпадение пути или заканчивается на matchPath
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

    fun registerResponse(request: Request): Response {
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
            .code(200) // HTTP 200 OK — можно заменить на любой код
            .message("OK (mocked by RegisterInterceptor)")
            .body(mockJson)
            .addHeader("Content-Type", "application/json; charset=utf-8")
            .build()
    }

    fun verifyCodeResponse(request: Request): Response {
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val mockAuthResponse = AuthResponse(
            user = UserResponse(
                id = "12345",
                firstName = "Иван",
                lastName = "Иванов",
                middleName = "Иванович",
                birthDate = "1990-05-15",
                gender = Gender.MALE,
                photo = "https://example.com/photos/ivan.jpg",
                phoneNumber = "+79998887766",
                email = "ivan.ivanov@example.com",
                qualification = listOf(
                    /*QualificationResponse(
                        kindOfSport = KindOfSport.Orienteering,
                        sportsCategory = SportsCategory.FIRST
                    ),
                    QualificationResponse(
                        kindOfSport = KindOfSport.TrailRunning,
                        sportsCategory = SportsCategory.CANDIDATE
                    )*/
                )
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
            .code(200) // HTTP 200 OK — можно заменить на любой код
            .message("OK (mocked by RegisterInterceptor)")
            .body(mockJson)
            .addHeader("Content-Type", "application/json; charset=utf-8")
            .build()
    }

    fun mockEvents(request: Request): Response {
        val mockOrienteeringResponse = listOf(
            OrienteeringCompetitionResponse(
                competitionId = 1,
                competition = CompetitionResponse(
                    title = "Городские соревнования",
                    date = LocalDate.parse("2025-08-25").toEpochDay(),
                    kindOfSport = KindOfSport.Orienteering.name,
                    description = "Новые соревнования по ориентированию. Новые соревнования по ориентированию. Новые соревнования по ориентированию. Новые соревнования по ориентированию. Новые соревнования по ориентированию. Новые соревнования по ориентированию. Новые соревнования по ориентированию.",
                    address = "Саратов",
                    mainOrganizer = "12345",
                    coordinates = CoordinatesResponse(
                        latitude = 51.3200,
                        longitude = 46.0000
                    )
                ),
                direction = OrienteeringDirection.FORWARD.name,
                punchingSystem = PunchingSystem.SPORTIDUINO
            ),
            OrienteeringCompetitionResponse(
                competitionId = 2,
                direction = OrienteeringDirection.FORWARD.name,
                punchingSystem = PunchingSystem.SPORTIDUINO,
                competition = CompetitionResponse(
                    title = "Городские соревнования #2",
                    date = LocalDate.parse("2025-08-27").toEpochDay(),
                    kindOfSport = KindOfSport.Orienteering.name,
                    description = "Новые соревнования по ориентированию. Новые соревнования по ориентированию. Новые соревнования по ориентированию. Новые соревнования по ориентированию. Новые соревнования по ориентированию. Новые соревнования по ориентированию. Новые соревнования по ориентированию.",
                    address = "Балашов",
                    mainOrganizer = "12345",
                    coordinates = CoordinatesResponse(
                        latitude = 51.3200,
                        longitude = 46.0000
                    )
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
            .code(200) // HTTP 200 OK — можно заменить на любой код
            .message("OK (mocked by MockInterceptor)")
            .body(mockJson)
            .addHeader("Content-Type", "application/json; charset=utf-8")
            .build()
    }

    fun getOrienteeringCompetitionsResponse(request: Request): Response {
        val mediaType = "application/json; charset=utf-8".toMediaType()

        // Достаём тело запроса (competition), чтобы "вернуть его"
        val bodyString = request.body?.let { body ->
            val buffer = okio.Buffer()
            body.writeTo(buffer)
            buffer.readUtf8()
        }

        // Парсим входящий JSON → OrienteeringCompetitionRequest
        val gson = Gson()
        val competitionRequest = try {
            gson.fromJson(bodyString, OrienteeringCompetitionRequest::class.java)
        } catch (e: Exception) {
            null
        }

        // Формируем моковый ответ — сервер обычно возвращает competitionId + competition data
        val responseBody = OrienteeringCompetitionResponse(
            competitionId = (1000..9999).random().toLong(), // моковый id
            direction = competitionRequest?.direction ?: OrienteeringDirection.FORWARD.name,
            punchingSystem = /*competitionRequest?.punchingSystem ?:*/ PunchingSystem.SPORTIDUINO,
            competition = CompetitionResponse(
                title = competitionRequest?.competition?.title ?: "Mocked Competition",
                date = competitionRequest?.competition?.date ?: LocalDate.now().toEpochDay(),
                kindOfSport = competitionRequest?.competition?.kindOfSport
                    ?: KindOfSport.Orienteering.name,
                description = competitionRequest?.competition?.description ?: "Mocked Description",
                address = competitionRequest?.competition?.address ?: "Mocked Address",
                mainOrganizer = /*competitionRequest?.competition?.mainOrganizer ?:*/ "12345",
                coordinates = CoordinatesResponse(
                    latitude = competitionRequest?.competition?.coordinates?.latitude ?: 51.0,
                    longitude = competitionRequest?.competition?.coordinates?.longitude ?: 45.0
                )
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

    fun getParticipantGroupResponse(request: Request): Response {

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val gson = Gson()

        // Тут можешь сделать парсинг body, если нужен
        // Достаём тело запроса (competition), чтобы "вернуть его"
        val bodyString = request.body?.let { body ->
            val buffer = okio.Buffer()
            body.writeTo(buffer)
            buffer.readUtf8()
        }

        val participantGroups =
            gson.fromJson(bodyString, Array<ParticipantGroupRequest>::class.java)
        val mockGroupsResponse = mutableListOf<ParticipantGroupResponse>()
        participantGroups.forEach {
            mockGroupsResponse.add(
                ParticipantGroupResponse(
                    groupId = (1000..9999).random().toLong(),
                    competitionId = it.competitionId,
                    title = it.title,
                    distance = it.distance,
                    countOfControls = it.countOfControls,
                    maxTimeInMinute = it.maxTimeInMinute
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
