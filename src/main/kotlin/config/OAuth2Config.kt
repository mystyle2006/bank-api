package org.inno.config

import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import jakarta.annotation.PostConstruct

@Configuration
@ConfigurationProperties(prefix = "oauth2")
class OAuth2Config {
    private val logger = LoggerFactory.getLogger(this::class.java)
    val providers: MutableMap<String, OAuth2ProviderValues> = mutableMapOf()

    @PostConstruct
    fun logConfig() {
        logger.info("=== OAuth2 Setting Info ===")
        providers.forEach { (provider, values) ->
            logger.info("$provider 설정:")
            logger.info("  - Client ID: ${values.clientId}")
            logger.info("  - Redirect URI: ${values.redirectUri}")
        }
    }
}

data class OAuth2ProviderValues(
    var clientId: String = "",
    var clientSecret: String = "",
    var redirectUri: String = ""
)
