package kr.cse.scamguard.common.security.jwt

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import kr.cse.scamguard.common.security.jwt.access.model.AccessTokenClaim
import kr.cse.scamguard.common.security.jwt.access.model.AccessTokenStrategy
import kr.cse.scamguard.common.security.jwt.common.JwtClaims
import kr.cse.scamguard.common.security.jwt.common.JwtClaimsParserUtil
import kr.cse.scamguard.common.security.jwt.common.JwtProvider
import kr.cse.scamguard.common.security.jwt.common.JwtTokens
import kr.cse.scamguard.common.security.jwt.exception.JwtErrorCode
import kr.cse.scamguard.common.security.jwt.exception.JwtErrorException
import kr.cse.scamguard.common.security.jwt.forbidden.service.ForbiddenTokenService
import kr.cse.scamguard.common.security.jwt.refresh.model.RefreshTokenClaim
import kr.cse.scamguard.common.security.jwt.refresh.model.RefreshTokenClaimKeys
import kr.cse.scamguard.common.security.jwt.refresh.model.RefreshTokenEntity
import kr.cse.scamguard.common.security.jwt.refresh.model.RefreshTokenStrategy
import kr.cse.scamguard.common.security.jwt.refresh.service.RefreshTokenService
import kr.cse.scamguard.domain.user.model.User

import java.time.Duration
import java.time.LocalDateTime

@Component
class JwtAuthManager(
    @AccessTokenStrategy private val accessTokenProvider: JwtProvider,
    @RefreshTokenStrategy private val refreshTokenProvider: JwtProvider,
    private val refreshTokenService: RefreshTokenService,
    private val forbiddenTokenService: ForbiddenTokenService
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    fun createToken(user: User): JwtTokens {
        val accessToken = accessTokenProvider.generateToken(AccessTokenClaim.of(user.id, user.type.name))
        val refreshToken = refreshTokenProvider.generateToken(RefreshTokenClaim.of(user.id, user.type.name))

        refreshTokenService.save(RefreshTokenEntity.of(user.id, refreshToken, toSeconds(refreshTokenProvider.getExpiryDate(refreshToken))))
        return JwtTokens.of(accessToken, refreshToken, user.id)
    }

    fun refresh(refreshToken: String): JwtTokens {
        val claims = refreshTokenProvider.getJwtClaimsFromToken(refreshToken)
        val userId = JwtClaimsParserUtil.getClaimsValue(claims, RefreshTokenClaimKeys.USER_ID.value) { it.toLong() }
            ?: throw JwtErrorException(JwtErrorCode.UNEXPECTED_ERROR)
        val role = JwtClaimsParserUtil.getClaimsValue(claims, RefreshTokenClaimKeys.ROLE.value) { it }
            ?: throw JwtErrorException(JwtErrorCode.UNEXPECTED_ERROR)

        val newRefreshToken = try {
            refreshTokenService.refresh(userId, refreshToken, refreshTokenProvider.generateToken(RefreshTokenClaim.of(userId, role)))
        } catch (e: IllegalArgumentException) {
            throw JwtErrorException(JwtErrorCode.EXPIRED_TOKEN)
        } catch (e: IllegalStateException) {
            throw JwtErrorException(JwtErrorCode.TAKEN_AWAY_TOKEN)
        }

        val newAccessToken = accessTokenProvider.generateToken(AccessTokenClaim.of(userId, role))
        return JwtTokens.of(newAccessToken, newRefreshToken.token, userId)
    }

    fun removeAccessTokenAndRefreshToken(userId: Long, accessToken: String?, refreshToken: String?) {
        var jwtClaims: JwtClaims? = null
        if (refreshToken != null) {
            try {
                jwtClaims = refreshTokenProvider.getJwtClaimsFromToken(refreshToken)
            } catch (e: JwtErrorException) {
                if (e.errorCode != JwtErrorCode.EXPIRED_TOKEN) {
                    throw e
                }
            }
        }

        if (jwtClaims != null) {
            deleteRefreshToken(userId, jwtClaims)
        }

        if (accessToken != null) {
            deleteAccessToken(userId, accessToken)
        }
    }

    fun removeUserTokens(userId: Long, accessToken: String? = null) {
        log.info("사용자 {} 토큰 {} 삭제", userId, accessToken)
        deleteRefreshToken(userId)
        accessToken?.let {
            val token = if (it.startsWith("Bearer ")) {
                it.substring("Bearer ".length)
            } else {
                it
            }
            deleteAccessToken(userId, token)
        }
    }

    private fun deleteRefreshToken(userId: Long, jwtClaims: JwtClaims) {
        val refreshTokenUserId = JwtClaimsParserUtil.getClaimsValue(jwtClaims, RefreshTokenClaimKeys.USER_ID.value) { it.toLong() }
            ?: throw JwtErrorException(JwtErrorCode.UNEXPECTED_ERROR)

        if (userId != refreshTokenUserId) {
            throw JwtErrorException(JwtErrorCode.WITHOUT_OWNERSHIP_REFRESH_TOKEN)
        }

        refreshTokenService.deleteAll(refreshTokenUserId)
    }

    private fun deleteRefreshToken(userId: Long) {
        refreshTokenService.deleteAll(userId)
    }

    private fun deleteAccessToken(userId: Long, accessToken: String) {
        val expiresAt = accessTokenProvider.getExpiryDate(accessToken)
        log.info("사용자 {} access token: {} 블랙리스트 등록 기간({})", userId, accessToken, expiresAt)
        forbiddenTokenService.createForbiddenToken(userId, accessToken, expiresAt)
    }

    private fun toSeconds(expiryTime: LocalDateTime): Long {
        return Duration.between(LocalDateTime.now(), expiryTime).seconds
    }
}
