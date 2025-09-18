package com.rodionov.remote.interceptors

import com.google.gson.Gson
import com.rodionov.domain.models.Gender
import com.rodionov.domain.models.KindOfSport
import com.rodionov.domain.models.SportsCategory
import com.rodionov.remote.base.CommonModel
import com.rodionov.remote.response.auth.AuthResponse
import com.rodionov.remote.response.auth.TokenResponse
import com.rodionov.remote.response.user.QualificationResponse
import com.rodionov.remote.response.user.UserResponse
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody

class MockInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val path = request.url.encodedPath // например: "/api/v1/register"

        // Простая проверка: точное совпадение пути или заканчивается на matchPath
        return when {
            path.contains("user/register") -> registerResponse(request)
            path.contains("user/verify_code") -> verifyCodeResponse(request)
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

}
