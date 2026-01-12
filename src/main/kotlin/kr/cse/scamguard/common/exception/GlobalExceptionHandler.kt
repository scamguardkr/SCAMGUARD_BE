package kr.cse.scamguard.common.exception

import jakarta.servlet.http.HttpServletRequest
import kr.cse.scamguard.common.model.CommonResponse
import kr.cse.scamguard.common.model.toApiResponse
import kr.cse.scamguard.common.model.toErrorResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.util.*

@RestControllerAdvice
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {

    private val log = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(CustomException::class)
    fun handleCustomException(e: CustomException, request: HttpServletRequest): ResponseEntity<CommonResponse<Nothing?>> {
        val errorCode = e.errorCode
        val errorTraceId = UUID.randomUUID().toString()
        return buildErrorResponse(errorCode, errorTraceId)
    }

    override fun handleHttpMessageNotReadable(
        ex: HttpMessageNotReadableException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any> {
        log.error("Failed to read request", ex)
        log.error("Cause: ${ex.cause?.message}")
        log.error("Root cause: ${ex.rootCause?.message}")

        return ResponseEntity.badRequest().body(mapOf(
            "error" to "Bad Request",
            "message" to ex.message,
            "cause" to ex.cause?.message,
            "rootCause" to ex.rootCause?.message
        ))
    }

    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any> {
        val errors = ex.bindingResult.toApiResponse()
        log.error(errors.toString())
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors)
    }

    private fun buildErrorResponse(errorCode: BaseResponseCode, traceId: String): ResponseEntity<CommonResponse<Nothing?>> {
        val response = errorCode.toErrorResponse()
        return ResponseEntity.status(errorCode.httpStatus).body(response)
    }
}
