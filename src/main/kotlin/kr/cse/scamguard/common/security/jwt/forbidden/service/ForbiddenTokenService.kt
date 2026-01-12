package kr.cse.scamguard.common.security.jwt.forbidden.service

import java.time.LocalDateTime

interface ForbiddenTokenService {
    fun createForbiddenToken(userId: Long, accessToken: String, expiredAt: LocalDateTime)
    fun checkForbidden(accessToken: String): Boolean
}
