package kr.cse.scamguard.common.security.jwt.refresh

import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import kr.cse.scamguard.common.security.jwt.common.JwtClaims
import kr.cse.scamguard.common.security.jwt.common.JwtProvider
import kr.cse.scamguard.common.security.jwt.exception.JwtErrorCode
import kr.cse.scamguard.common.security.jwt.exception.JwtErrorCodeUtil
import kr.cse.scamguard.common.security.jwt.exception.JwtErrorException
import kr.cse.scamguard.common.security.jwt.refresh.model.RefreshTokenClaim
import kr.cse.scamguard.common.security.jwt.refresh.model.RefreshTokenClaimKeys
import kr.cse.scamguard.common.security.jwt.refresh.model.RefreshTokenStrategy
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import javax.crypto.SecretKey

@RefreshTokenStrategy
@Component
class RefreshTokenProvider(
    @Value("\${jwt.secret-key}") jwtSecretKey: String,
    @Value("\${jwt.refresh-token.expiration-time}") private val tokenExpiration: Duration
) : JwtProvider {

    private val secretKey: SecretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtSecretKey))
    private val log = LoggerFactory.getLogger(this::class.java)

    override fun generateToken(claims: JwtClaims): String {
        val now = Date()
        return Jwts.builder()
            .header().add(createHeader()).and()
            .claims(claims.getClaims())
            .signWith(secretKey)
            .expiration(createExpireDate(now, tokenExpiration.toMillis()))
            .compact()
    }

    override fun getJwtClaimsFromToken(token: String): JwtClaims {
        val claims = getClaimsFromToken(token)
        return RefreshTokenClaim.of(
            claims.get(RefreshTokenClaimKeys.USER_ID.value, String::class.java).toLong(),
            claims.get(RefreshTokenClaimKeys.ROLE.value, String::class.java)
        )
    }

    override fun getExpiryDate(token: String): LocalDateTime {
        val claims = getClaimsFromToken(token)
        return Instant.ofEpochMilli(claims.expiration.time)
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
    }

    override fun isTokenExpired(token: String): Boolean {
        return try {
            val claims = getClaimsFromToken(token)
            claims.expiration.before(Date())
        } catch (e: JwtErrorException) {
            if (JwtErrorCode.EXPIRED_TOKEN == e.errorCode) true
            else throw e
        }
    }

    override fun getClaimsFromToken(token: String): Claims {
        return try {
            Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .payload
        } catch (e: JwtException) {
            val errorCode = JwtErrorCodeUtil.determineErrorCode(e, JwtErrorCode.FAILED_AUTHENTICATION)
            log.warn("Error code : {}, Error - {}, {}", errorCode, e.javaClass, e.message)
            throw JwtErrorException(errorCode)
        }
    }

    private fun createHeader(): Map<String, Any> {
        return mapOf(
            "typ" to "JWT",
            "alg" to "HS256",
            "regDate" to System.currentTimeMillis()
        )
    }

    private fun createExpireDate(now: Date, expirationTime: Long): Date {
        return Date(now.time + expirationTime)
    }
}
