package kr.cse.scamguard.common.security.jwt.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import kr.cse.scamguard.common.security.jwt.exception.JwtAuthenticationException
import kr.cse.scamguard.common.security.jwt.exception.JwtErrorCodeUtil

@Component
class JwtExceptionFilter(
    private val authenticationEntryPoint: AuthenticationEntryPoint
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            filterChain.doFilter(request, response)
        } catch (e: Exception) {
            val authException = when (e) {
                is JwtAuthenticationException -> e
                else -> {
                    val jwtException = JwtErrorCodeUtil.determineAuthErrorException(e)
                    JwtAuthenticationException(jwtException.errorCode)
                }
            }

            authenticationEntryPoint.commence(request, response, authException)
        }
    }
}
