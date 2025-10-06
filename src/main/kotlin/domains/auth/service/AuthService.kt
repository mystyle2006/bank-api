package org.inno.domains.auth.service

import org.inno.common.exception.CustomException
import org.inno.common.exception.ErrCode
import org.inno.common.jwt.JwtProvider
import org.inno.common.logging.Logging
import org.inno.interfaces.OAuthServiceInterface
import org.slf4j.Logger
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val oAuthServices: Map<String, OAuthServiceInterface>,
    private val jwtProvider: JwtProvider,
    private val logger : Logger = Logging.getLogger(AuthService::class.java),
) {
    fun handleAuth(state: String, code: String): String = Logging.logFor(logger) { log ->
        val provider = state.lowercase()
        log["provider"] = provider

        val callService = oAuthServices[provider] ?: throw CustomException(ErrCode.PROVIDER_NOT_FOUND, provider)

        val accessToken = callService.getToken(code).accessToken
        val userInfo = callService.getUserInfo(accessToken)
        val token = jwtProvider.createToken(provider, userInfo.email, userInfo.name, userInfo.id)

        return@logFor ""
    }
}