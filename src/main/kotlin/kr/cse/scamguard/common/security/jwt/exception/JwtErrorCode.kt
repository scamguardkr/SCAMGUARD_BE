package kr.cse.scamguard.common.security.jwt.exception

import org.springframework.http.HttpStatus
import kr.cse.scamguard.common.exception.BaseResponseCode
import kr.cse.scamguard.common.model.CommonResponse

enum class JwtErrorCode (
    override val httpStatus: HttpStatus,
    override val message: String
) : BaseResponseCode {

    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다"),
    INVALID_TOKEN_FORMAT(HttpStatus.UNAUTHORIZED, "비정상적인 토큰입니다"),
    EMPTY_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "엑세스 토큰이 존재하지 않습니다"),
    MALFORMED_TOKEN(HttpStatus.UNAUTHORIZED, "비정상적인 토큰입니다"),
    TAMPERED_TOKEN(HttpStatus.UNAUTHORIZED, "서명이 조작된 토큰입니다"),
    UNSUPPORTED_JWT_TOKEN(HttpStatus.UNAUTHORIZED, "지원하지 않는 토큰입니다"),
    FAILED_AUTHENTICATION(HttpStatus.UNAUTHORIZED, "인증에 실패하였습니다"),
    TAKEN_AWAY_TOKEN(HttpStatus.FORBIDDEN, "탈취당한 토큰입니다. 다시 로그인 해주세요"),
    FORBIDDEN_ACCESS_TOKEN(HttpStatus.FORBIDDEN, "비활성화된 토큰입니다"),
    WITHOUT_OWNERSHIP_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "소유권이 없는 리프레시 토큰입니다"),
    UNEXPECTED_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류 발생"),
}

fun JwtErrorCode.toErrorResponse(): CommonResponse<Nothing?> =
    CommonResponse.error(this.name, this.message)
