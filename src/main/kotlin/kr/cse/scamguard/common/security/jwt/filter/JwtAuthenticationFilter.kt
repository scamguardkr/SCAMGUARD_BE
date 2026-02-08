package kr.cse.scamguard.common.security.jwt.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter
import kr.cse.scamguard.common.security.jwt.access.model.AccessTokenClaimKeys
import kr.cse.scamguard.common.security.jwt.common.JwtProvider
import kr.cse.scamguard.common.security.jwt.exception.JwtAuthenticationException
import kr.cse.scamguard.common.security.jwt.exception.JwtErrorCode
import kr.cse.scamguard.common.security.jwt.exception.JwtErrorException
import kr.cse.scamguard.common.security.jwt.forbidden.service.ForbiddenTokenService
import kr.cse.scamguard.common.security.jwt.forbidden.service.ForbiddenTokenServiceRedisImpl

@Component
class JwtAuthenticationFilter(
    private val userDetailsService: UserDetailsService,
    private val forbiddenTokenService: ForbiddenTokenService,
    private val accessTokenProvider: JwtProvider
) : OncePerRequestFilter() {

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        if (isAnonymousRequest(request)) {
            filterChain.doFilter(request, response)
            return
        }

        try {
            val accessToken = resolveAccessToken(request)
            val userDetails = getUserDetails(accessToken)
            authenticateUser(userDetails, request)
            filterChain.doFilter(request, response)
        } catch (e: JwtAuthenticationException) {
            log.info(e.message)
            throw e
        } catch (e: JwtErrorException) {
            log.info(e.message)
            throw JwtAuthenticationException(e.errorCode)
        } catch (e: Exception) {
            log.info(e.message)
            throw JwtAuthenticationException(JwtErrorCode.UNEXPECTED_ERROR)
        }
    }

    private fun resolveAccessToken(request: HttpServletRequest): String {
        val authHeader = request.getHeader(HttpHeaders.AUTHORIZATION)

        val token = try {
            accessTokenProvider.resolveToken(authHeader).also {
                log.debug("요청 토큰 : {}", it)
            }
        } catch (e: Exception) {
            log.error("토큰 파싱 중 오류 발생: {}", e.message)
            throw JwtAuthenticationException(JwtErrorCode.INVALID_TOKEN_FORMAT)
        }

        if (!StringUtils.hasText(token)) {
            throw JwtAuthenticationException(JwtErrorCode.EMPTY_ACCESS_TOKEN)
        }

        if (forbiddenTokenService.checkForbidden(token)) {
            throw JwtAuthenticationException(JwtErrorCode.FORBIDDEN_ACCESS_TOKEN)
        }

        if (accessTokenProvider.isTokenExpired(token)) {
            throw JwtAuthenticationException(JwtErrorCode.EXPIRED_TOKEN)
        }

        return token
    }

    private fun isAnonymousRequest(request: HttpServletRequest): Boolean {
        val accessToken = request.getHeader(HttpHeaders.AUTHORIZATION)
        return !StringUtils.hasText(accessToken)
    }

    private fun getUserDetails(accessToken: String): UserDetails {
        val claims = accessTokenProvider.getJwtClaimsFromToken(accessToken)
        val userId = claims.getClaims()[AccessTokenClaimKeys.USER_ID.value] as String
        log.debug("User ID: {}", userId)

        return userDetailsService.loadUserByUsername(userId)
    }

    private fun authenticateUser(userDetails: UserDetails, request: HttpServletRequest) {
        val authentication = org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
            userDetails, null, userDetails.authorities
        )

        authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
        SecurityContextHolder.getContext().authentication = authentication
        log.info("Authenticated user: {}", userDetails.username)
    }
}
