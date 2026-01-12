package kr.cse.scamguard.domain.user.exception

import kr.cse.scamguard.common.exception.BaseResponseCode
import org.springframework.http.HttpStatus

enum class UserErrorCode (
    override val httpStatus: HttpStatus,
    override val message: String
) : BaseResponseCode {

    NOT_FOUND_USER(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다"),
    DUPLICATE_LOGIN_ID(HttpStatus.NOT_FOUND, "중복된 아이디 입니다"),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다"),
    SAME_NICKNAME(HttpStatus.UNAUTHORIZED, "같은 닉네임 입니다"),
    INVALID_CODE(HttpStatus.BAD_REQUEST, "인증 코드가 일치하지 않습니다"),
    TIMEOUT_OR_NOT_FOUND_CODE(HttpStatus.BAD_REQUEST, "인증 코드가 일치하지 않습니다");
}
