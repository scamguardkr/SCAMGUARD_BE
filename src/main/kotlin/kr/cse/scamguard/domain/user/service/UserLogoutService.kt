package kr.cse.scamguard.domain.user.service

import kr.cse.scamguard.common.security.jwt.JwtAuthManager
import org.springframework.stereotype.Service

@Service
class UserLogoutService (
    private val jwtAuthManager: JwtAuthManager
) {

    fun execute(userId: Long, accessToken: String?) {
        jwtAuthManager.removeUserTokens(userId, accessToken)
    }
}
