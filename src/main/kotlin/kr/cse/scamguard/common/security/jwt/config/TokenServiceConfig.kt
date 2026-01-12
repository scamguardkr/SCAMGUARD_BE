package kr.cse.scamguard.common.security.jwt.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import kr.cse.scamguard.common.security.jwt.forbidden.service.ForbiddenTokenService
import kr.cse.scamguard.common.security.jwt.forbidden.service.ForbiddenTokenServiceInMemoryImpl
import kr.cse.scamguard.common.security.jwt.forbidden.service.ForbiddenTokenServiceRedisImpl
import kr.cse.scamguard.common.security.jwt.refresh.service.RefreshTokenService
import kr.cse.scamguard.common.security.jwt.refresh.service.RefreshTokenServiceInMemoryImpl
import kr.cse.scamguard.common.security.jwt.refresh.service.RefreshTokenServiceRedisImpl

@Configuration
class TokenServiceConfig (
    private val inMemoryRefreshService: RefreshTokenServiceInMemoryImpl,
    private val inMemoryForbiddenService: ForbiddenTokenServiceInMemoryImpl,
    private val redisRefreshTokenService: RefreshTokenServiceRedisImpl,
    private val redisForbiddenTokenService: ForbiddenTokenServiceRedisImpl
) {
    @Bean
    fun refreshTokenService(): RefreshTokenService {
        val useInMemory = false
        return if (useInMemory) inMemoryRefreshService else redisRefreshTokenService
    }

    @Bean
    fun forbiddenTokenService(): ForbiddenTokenService {
        val useInMemory = false
        return if (useInMemory) inMemoryForbiddenService else redisForbiddenTokenService
    }
}
