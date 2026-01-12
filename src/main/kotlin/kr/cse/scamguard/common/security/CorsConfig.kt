package kr.cse.scamguard.common.security

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.http.HttpHeaders;

@Configuration
class CorsConfig (
    @Value("\${cors.allowed-origins}") private val allowedOrigins: List<String>
) {

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration().apply {
            this.allowedOrigins = this@CorsConfig.allowedOrigins
            allowedMethods = listOf("GET", "POST", "OPTIONS", "PUT", "PATCH", "DELETE")
            allowedHeaders = listOf("*")
            exposedHeaders = listOf(HttpHeaders.AUTHORIZATION, HttpHeaders.SET_COOKIE)
            maxAge = 3600L
            allowCredentials = true
        }

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }
}
