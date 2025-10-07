package org.inno.domains.auth.service

import com.github.f4b6a3.ulid.UlidCreator
import org.inno.common.exception.CustomException
import org.inno.common.exception.ErrCode
import org.inno.common.jwt.JwtProvider
import org.inno.common.logging.Logging
import org.inno.common.transaction.Transactional
import org.inno.domains.auth.repository.domains.auth.repository.AuthUserRepository
import org.inno.interfaces.OAuthServiceInterface
import org.inno.types.entity.User
import org.slf4j.Logger
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val oAuthServices: Map<String, OAuthServiceInterface>,
    private val jwtProvider: JwtProvider,
    private val logger: Logger = Logging.getLogger(AuthService::class.java),
    private val transaction: Transactional,
    private val authUserRepository: AuthUserRepository
) {
    fun handleAuth(state: String, code: String): String = Logging.logFor(logger) { log ->
        val provider = state.lowercase()
        log["provider"] = provider

        val callService = oAuthServices[provider] ?: throw CustomException(ErrCode.PROVIDER_NOT_FOUND, provider)

        val accessToken = callService.getToken(code).accessToken
        val userInfo = callService.getUserInfo(accessToken)
        val token = jwtProvider.createToken(provider, userInfo.email, userInfo.name, userInfo.id)

        val username = (userInfo.name ?: userInfo.email).toString()

        transaction.run {
            val exist = authUserRepository.existsByUsername(username)

            if (exist) {
                authUserRepository.updateAccessTokenByUsername(username, token)
            } else {
                val ulid = UlidCreator.getUlid().toString()

                val user = User(ulid, username, token)

                authUserRepository.save(user)
            }
        }

        return@logFor token
    }

    fun verifyToken(authorization : String) {
        jwtProvider.verifyToken(authorization.removePrefix("Bearer "))
    }
}