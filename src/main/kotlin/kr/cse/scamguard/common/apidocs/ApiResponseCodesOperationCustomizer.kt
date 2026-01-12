package kr.cse.scamguard.common.apidocs

import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.media.Content
import io.swagger.v3.oas.models.media.MediaType
import io.swagger.v3.oas.models.responses.ApiResponse
import io.swagger.v3.oas.models.responses.ApiResponses
import kr.cse.scamguard.common.exception.BaseResponseCode
import org.springdoc.core.customizers.OperationCustomizer
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod

@Component
class ApiResponseCodesOperationCustomizer(
    private val responseCodeFinder: ResponseCodeFinder,
    private val successResponseHandler: SuccessResponseHandler,
    private val errorResponseHandler: ErrorResponseHandler,
    private val validationErrorResponseHandler: ValidationErrorResponseHandler,
) : OperationCustomizer {

    override fun customize(operation: Operation, handlerMethod: HandlerMethod): Operation {
        val apiResponsesAnnotation = handlerMethod.getMethodAnnotation(ApiResponseCodes::class.java)
            ?: return operation

        val responses = operation.responses ?: ApiResponses()
        responses.clear()

        val groupedCodes: Map<Int, List<BaseResponseCode>> = apiResponsesAnnotation.value
            .map { responseCodeFinder.find(it) }
            .groupBy { it.httpStatus.value() }

        var validationErrorResponseGenerated = false

        groupedCodes.forEach { (statusCode, codes) ->
            val apiResponse = ApiResponse().apply { description = getDefaultDescription(codes.first().httpStatus) }
            val mediaType = MediaType()

            codes.forEach { code ->
                if (code.httpStatus.is2xxSuccessful) {
                    // successResponseHandler.handle(mediaType, handlerMethod.method.genericReturnType)
                    successResponseHandler.handle(mediaType, handlerMethod)
                } else {
                    errorResponseHandler.handle(mediaType, code)
                }
            }

            // @Valid가 있고, 현재 처리중인 코드가 400일 경우, Validation 예시를 추가
            if (validationErrorResponseHandler.hasValidation(handlerMethod) && statusCode == 400) {
                validationErrorResponseHandler.handle(mediaType, handlerMethod)
                validationErrorResponseGenerated = true
            }

            apiResponse.content = Content().addMediaType("application/json", mediaType)
            responses.addApiResponse(statusCode.toString(), apiResponse)
        }

        // @Valid가 있지만, 400 코드가 명시되지 않은 경우를 위해 별도로 추가
        if (validationErrorResponseHandler.hasValidation(handlerMethod) && !validationErrorResponseGenerated) {
            val validationApiResponse = ApiResponse().apply { description = "검증 실패" }
            val validationMediaType = MediaType()
            validationErrorResponseHandler.handle(validationMediaType, handlerMethod)
            validationApiResponse.content = Content().addMediaType("application/json", validationMediaType)
            responses.addApiResponse("400", validationApiResponse)
        }

        operation.responses = responses
        return operation
    }

    private fun getDefaultDescription(httpStatus: HttpStatus): String {
        return when (httpStatus) {
            HttpStatus.OK, HttpStatus.CREATED -> "요청 성공"
            HttpStatus.BAD_REQUEST -> "Bad Request"
            HttpStatus.NOT_FOUND -> "Not Found"
            HttpStatus.UNAUTHORIZED -> "Unauthorized"
            HttpStatus.FORBIDDEN -> "Forbidden"
            HttpStatus.INTERNAL_SERVER_ERROR -> "Internal Server Error"
            else -> "응답"
        }
    }
}
