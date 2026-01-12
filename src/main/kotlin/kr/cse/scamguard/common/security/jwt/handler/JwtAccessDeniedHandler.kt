package kr.cse.scamguard.common.security.jwt.handler

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler
import kr.cse.scamguard.common.exception.CommonErrorCode
import kr.cse.scamguard.common.model.toErrorResponse

class JwtAccessDeniedHandler(
    private val objectMapper: ObjectMapper
) : AccessDeniedHandler {

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun handle(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        accessDeniedException: AccessDeniedException?
    ) {
        if (accessDeniedException != null) {
            log.warn("handle error: {}", accessDeniedException.message)
        }
        val errorResponse = CommonErrorCode.ACCESS_TO_THE_REQUESTED_RESOURCE_IS_FORBIDDEN.toErrorResponse()

        if (response != null) {
            response.contentType = "application/json;charset=UTF-8"
            response.status = CommonErrorCode.ACCESS_TO_THE_REQUESTED_RESOURCE_IS_FORBIDDEN.httpStatus.value()
            objectMapper.writeValue(response.writer, errorResponse)
        }
    }
}
