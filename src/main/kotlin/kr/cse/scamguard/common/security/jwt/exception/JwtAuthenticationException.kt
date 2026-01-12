package kr.cse.scamguard.common.security.jwt.exception
import org.springframework.security.core.AuthenticationException
import kr.cse.scamguard.common.exception.BaseResponseCode

class JwtAuthenticationException(
    val jwtErrorCode: BaseResponseCode,
    message: String = jwtErrorCode.message
) : AuthenticationException(message)
