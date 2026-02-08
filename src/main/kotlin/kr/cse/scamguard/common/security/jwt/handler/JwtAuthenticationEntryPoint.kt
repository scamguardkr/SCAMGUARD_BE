package kr.cse.scamguard.common.security.jwt.handler

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import kr.cse.scamguard.common.model.toErrorResponse
import kr.cse.scamguard.common.security.jwt.exception.JwtAuthenticationException
import kr.cse.scamguard.common.security.jwt.exception.JwtErrorCode

class JwtAuthenticationEntryPoint(
    private val objectMapper: ObjectMapper
) : AuthenticationEntryPoint {

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun commence(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        authException: AuthenticationException?
    ) {
        if (authException != null) {
            log.info("오류: {}", authException.message)
            log.info("로그: {}", authException.stackTraceToString())
        }

        val errorCode = when (authException) {
            is JwtAuthenticationException -> authException.jwtErrorCode
            else -> JwtErrorCode.FAILED_AUTHENTICATION
        }

        val errorResponse = errorCode.toErrorResponse()

        if (response != null) {
            response.contentType = "application/json;charset=UTF-8"
            response.status = errorCode.httpStatus.value()
            objectMapper.writeValue(response.writer, errorResponse)
        }
    }
}
