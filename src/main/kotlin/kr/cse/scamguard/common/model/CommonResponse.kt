package kr.cse.scamguard.common.model

import kr.cse.scamguard.common.exception.BaseResponseCode
import kr.cse.scamguard.common.exception.CommonErrorCode
import org.springframework.validation.BindingResult
import org.springframework.validation.FieldError

data class CommonResponse<T>(
    val status: String,
    val data: T? = null,
    val errorCode: String? = null,
    val errorMessage: String? = null,
    val fieldErrors: List<FieldErrorResponse>? = null
) {
    companion object {
        private const val SUCCESS = "success"
        private const val FAIL = "fail"
        private const val ERROR = "error"

        fun <T> success(data: T): CommonResponse<T> = CommonResponse(SUCCESS, data)

        fun success(): CommonResponse<Nothing?> = CommonResponse(SUCCESS)

        fun fail(errors: Map<String, String?>): CommonResponse<Map<String, String?>> = CommonResponse(FAIL, errors, errorCode = CommonErrorCode.INVALID_VALUE.name)

        fun failWithFieldErrors(fieldErrors: List<FieldErrorResponse>): CommonResponse<Nothing?> =
            CommonResponse(FAIL, data = null, errorCode = CommonErrorCode.INVALID_VALUE.name, fieldErrors = fieldErrors)

        fun error(errorCode: String, message: String): CommonResponse<Nothing?> = CommonResponse(ERROR, errorCode = errorCode, errorMessage = message)
    }
}

data class FieldErrorResponse(
    val field: String,
    val constraint: String,
    val message: String
)

fun BindingResult.toApiResponse(): CommonResponse<Nothing?> {
    val fieldErrors = this.allErrors.map { error ->
        when (error) {
            is FieldError -> FieldErrorResponse(
                field = error.field,
                constraint = error.code ?: "Unknown",
                message = error.defaultMessage ?: "검증 실패"
            )
            else -> FieldErrorResponse(
                field = error.objectName,
                constraint = error.code ?: "Unknown",
                message = error.defaultMessage ?: "검증 실패"
            )
        }
    }
    return CommonResponse.failWithFieldErrors(fieldErrors)
}

fun <T> T.toSuccessResponse(): CommonResponse<T> = CommonResponse.success(this)

fun Unit.toSuccessResponse(): CommonResponse<Nothing?> = CommonResponse.success()

fun BaseResponseCode.toErrorResponse() : CommonResponse<Nothing?> = CommonResponse.error(this.code, this.message)

