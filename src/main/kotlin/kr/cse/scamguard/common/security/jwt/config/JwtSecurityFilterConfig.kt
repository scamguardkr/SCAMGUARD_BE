package kr.cse.scamguard.common.security.jwt.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.AuthenticationEntryPoint
import kr.cse.scamguard.common.security.jwt.common.JwtProvider
import kr.cse.scamguard.common.security.jwt.filter.JwtAuthenticationFilter
import kr.cse.scamguard.common.security.jwt.filter.JwtExceptionFilter
import kr.cse.scamguard.common.security.jwt.forbidden.service.ForbiddenTokenService
import kr.cse.scamguard.common.security.jwt.forbidden.service.ForbiddenTokenServiceRedisImpl

@Configuration
class JwtSecurityFilterConfig(
    private val userDetailServiceImpl: UserDetailsService,
    private val forbiddenTokenService: ForbiddenTokenService,
    private val accessTokenProvider: JwtProvider,
    private val jwtAuthenticationEntryPoint: AuthenticationEntryPoint
) {

    @Bean
    fun jwtExceptionFilter(): JwtExceptionFilter {
        return JwtExceptionFilter(jwtAuthenticationEntryPoint)
    }

    @Bean
    fun jwtAuthenticationFilter(): JwtAuthenticationFilter {
        return JwtAuthenticationFilter(userDetailServiceImpl, forbiddenTokenService, accessTokenProvider)
    }
}
