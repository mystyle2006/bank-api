package org.inno.domains.auth.service

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import okhttp3.FormBody
import org.inno.common.exception.CustomException
import org.inno.common.exception.ErrCode
import org.inno.common.httpClient.CallClient
import org.inno.common.json.JsonUtil
import org.inno.config.OAuth2Config
import org.inno.interfaces.OAuth2TokenResponse
import org.inno.interfaces.OAuth2UserResponse
import org.inno.interfaces.OAuthServiceInterface
import org.springframework.stereotype.Service

private const val key = "google"

@Service(key)
class GoogleAuthService(
    private val config: OAuth2Config,
    private val httpClient: CallClient
): OAuthServiceInterface {
    private val provider = config.providers[key] ?: throw CustomException(ErrCode.AUTH_CONFIG_NOT_FOUND, key)
    private val tokenURL = "https://oauth2.googleapis.com/token"
    private val userInfoURL = "https://www.googleapis.com/oauth2/v2/userinfo"

    override val providerName: String = key

    override fun getToken(code: String): OAuth2TokenResponse {
        val body = FormBody.Builder()
            .add("code", code)
            .add("client_id", provider.clientId)
            .add("client_secret", provider.clientSecret)
            .add("redirect_uri", provider.redirectUri)
            .add("grant_type", "authorization_code")
            .build()

        val headers = mapOf("Accept" to "application/json")
        val jsonString = httpClient.POST(tokenURL, headers, body)

        val response : GoogleTokenResponse = JsonUtil.decodeFromJson(jsonString, GoogleTokenResponse.serializer())

        return response
    }

    override fun getUserInfo(accessToken: String): OAuth2UserResponse {
        val headers = mapOf(
            "Content-Type"  to "application/json",
            "Authorization" to "Bearer $accessToken"
        )

        val jsonString = httpClient.GET(userInfoURL, headers)
        val response : GoogleUserResponse = JsonUtil.decodeFromJson(jsonString, GoogleUserResponse.serializer())
        return response
    }
}

@Serializable
data class GoogleTokenResponse(
    @SerialName("access_token") override val accessToken: String,
) : OAuth2TokenResponse

@Serializable
data class GoogleUserResponse(
    override val id: String,
    override val email: String,
    override val name: String,
) : OAuth2UserResponse

