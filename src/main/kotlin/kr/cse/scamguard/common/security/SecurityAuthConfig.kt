package kr.cse.scamguard.common.security

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.access.AccessDeniedHandler
import kr.cse.scamguard.common.security.jwt.handler.JwtAccessDeniedHandler
import kr.cse.scamguard.common.security.jwt.handler.JwtAuthenticationEntryPoint

@Configuration
class SecurityAuthConfig (
    private val userDetailService: UserDetailsService,
    private val objectMapper: ObjectMapper
) {

    @Bean
    fun daoAuthenticationProvider(): DaoAuthenticationProvider {
        return DaoAuthenticationProvider(userDetailService)
    }

    @Bean
    fun bCryptPasswordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun authenticationEntryPoint(): AuthenticationEntryPoint {
        return JwtAuthenticationEntryPoint(objectMapper)
    }

    @Bean
    fun accessDeniedHandler(): AccessDeniedHandler {
        return JwtAccessDeniedHandler(objectMapper)
    }

    @Bean
    fun authenticationManager(config: AuthenticationConfiguration): AuthenticationManager {
        return config.authenticationManager
    }

}
