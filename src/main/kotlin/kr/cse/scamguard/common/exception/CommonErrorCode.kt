package kr.cse.scamguard.common.exception

import org.springframework.http.HttpStatus

enum class CommonErrorCode (
    override val httpStatus: HttpStatus,
    override val message: String
) : BaseResponseCode{

    INVALID_VALUE(HttpStatus.BAD_REQUEST, ""),
    MISSING_OR_INVALID_AUTHENTICATION_CREDENTIALS(HttpStatus.UNAUTHORIZED, "인증 정보가 없거나 유효하지 않습니다"),
    ACCESS_TO_THE_REQUESTED_RESOURCE_IS_FORBIDDEN(HttpStatus.FORBIDDEN, "요청한 리소스에 대한 접근이 금지되었습니다"),
    NOT_FOUND(HttpStatus.NOT_FOUND, "요청한 리소스를 찾을 수 없습니다"),
    INVALID_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "리프레시 토큰 불일치"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류"),
    RESOURCE_CONFLICT(HttpStatus.CONFLICT, "요청한 리소스가 이미 존재합니다"),
    ILLEGAL_STATE(HttpStatus.BAD_REQUEST, "")
    ;
}
