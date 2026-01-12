package kr.cse.scamguard.common.security.jwt.exception

import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.UnsupportedJwtException
import io.jsonwebtoken.security.SignatureException
import org.slf4j.LoggerFactory

object JwtErrorCodeUtil {
    private val ERROR_CODE_MAP: Map<Class<out JwtException>, JwtErrorCode> = mapOf(
        ExpiredJwtException::class.java to JwtErrorCode.EXPIRED_TOKEN,
        MalformedJwtException::class.java to JwtErrorCode.MALFORMED_TOKEN,
        SignatureException::class.java to JwtErrorCode.TAMPERED_TOKEN,
        UnsupportedJwtException::class.java to JwtErrorCode.UNSUPPORTED_JWT_TOKEN
    )
    private val log = LoggerFactory.getLogger(this::class.java)

    /**
     * 예외에 해당하는 오류 코드를 반환하거나 기본 오류 코드를 반환합니다.
     *
     * @param exception 예외 객체
     * @param defaultErrorCode 기본 오류 코드
     * @return JwtErrorCode
     */
    fun determineErrorCode(exception: Exception, defaultErrorCode: JwtErrorCode): JwtErrorCode {
        if (exception is JwtErrorException) {
            return exception.errorCode as JwtErrorCode
        }
        return ERROR_CODE_MAP[exception::class.java] ?: defaultErrorCode
    }

    /**
     * 예외에 해당하는 JwtErrorException을 반환합니다.
     * 기본 오류 코드는 UNEXPECTED_ERROR입니다.
     *
     * @param exception 예외 객체
     * @return JwtErrorException
     */
    fun determineAuthErrorException(exception: Exception): JwtErrorException {
        log.warn("오류 발생 : {}", exception.message)
        return findAuthErrorException(exception)
            ?: JwtErrorException(determineErrorCode(exception, JwtErrorCode.UNEXPECTED_ERROR))
    }

    private fun findAuthErrorException(exception: Exception): JwtErrorException? {
        return when {
            exception is JwtErrorException -> exception
            exception.cause is JwtErrorException -> exception.cause as JwtErrorException
            else -> null
        }
    }
}
