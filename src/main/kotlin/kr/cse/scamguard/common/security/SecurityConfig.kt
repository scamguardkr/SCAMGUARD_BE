package kr.cse.scamguard.common.security

import org.springframework.boot.autoconfigure.security.servlet.PathRequest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.security.web.util.matcher.RequestMatcher
import org.springframework.web.cors.CorsConfigurationSource
import kr.cse.scamguard.common.security.jwt.config.JwtSecurityAdapterConfig

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
class SecurityConfig(
    private val securityAdapterConfig: JwtSecurityAdapterConfig,
    private val corsConfigurationSource: CorsConfigurationSource,
    private val accessDeniedHandler: AccessDeniedHandler,
    private val authenticationEntryPoint: AuthenticationEntryPoint
) {

    @Bean
    fun filterChain(httpSecurity: HttpSecurity): SecurityFilterChain {
        return httpSecurity
            .httpBasic { it.disable() }
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .cors { it.configurationSource(corsConfigurationSource) }
            .formLogin { it.disable() }
            .logout { it.disable() }
            .with(securityAdapterConfig) {}
            .exceptionHandling {
                it.accessDeniedHandler(accessDeniedHandler)
                it.authenticationEntryPoint(authenticationEntryPoint)
            }
            .authorizeHttpRequests {
                it.requestMatchers(*WebSecurityUrls.SWAGGER_ENDPOINTS).permitAll()
                    .requestMatchers(RequestMatcher { PathRequest.toStaticResources().atCommonLocations().matches(it) }).permitAll()
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    .requestMatchers(HttpMethod.GET, *WebSecurityUrls.READ_ONLY_PUBLIC_ENDPOINTS).permitAll()
                    .anyRequest().permitAll()
            }
            .build()
    }
}
