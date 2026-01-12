package kr.cse.scamguard.domain.user.service

import kr.cse.scamguard.common.security.jwt.JwtAuthManager
import kr.cse.scamguard.common.security.jwt.access.AccessTokenProvider
import kr.cse.scamguard.common.security.jwt.access.model.AccessTokenClaimKeys
import kr.cse.scamguard.common.security.jwt.common.JwtClaimsParserUtil
import kr.cse.scamguard.domain.user.dto.UserAuthRefreshResponse
import kr.cse.scamguard.domain.user.dto.UserAuthStateResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserLoginStatusService (
    private val accessTokenProvider: AccessTokenProvider,
    private val jwtAuthManager: JwtAuthManager
) {

    @Transactional(readOnly = true)
    fun readAuthStatus(authHeader: String): UserAuthStateResponse {
        val accessToken = accessTokenProvider.resolveToken(authHeader)
        val tokenClaims = accessTokenProvider.getJwtClaimsFromToken(accessToken)
        val userId = JwtClaimsParserUtil.getClaimsValue(tokenClaims, AccessTokenClaimKeys.USER_ID.value) { it.toLong() }

        return UserAuthStateResponse(userId)
    }

    fun refresh(refreshToken: String): UserAuthRefreshResponse {
        val jwtTokens = jwtAuthManager.refresh(refreshToken)
        return UserAuthRefreshResponse(jwtTokens.userId, jwtTokens.accessToken, jwtTokens.refreshToken)
    }
}
