package kr.cse.scamguard.common.security.jwt.common

import io.jsonwebtoken.Claims
import java.time.LocalDateTime

interface JwtProvider {
    fun resolveToken(authHeader: String?): String {
        requireNotNull(authHeader) { "" }
        require(authHeader.startsWith("Bearer ")) { "" }
        return authHeader.substring("Bearer ".length)
    }

    fun generateToken(claims: JwtClaims): String

    fun getJwtClaimsFromToken(token: String): JwtClaims

    fun getExpiryDate(token: String): LocalDateTime

    fun isTokenExpired(token: String): Boolean

    fun getClaimsFromToken(token: String): Claims
}
