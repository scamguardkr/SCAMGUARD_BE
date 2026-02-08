package kr.cse.scamguard.domain.scam.exception

import kr.cse.scamguard.common.exception.BaseResponseCode
import org.springframework.http.HttpStatus

enum class ScamErrorCode (
    override val httpStatus: HttpStatus,
    override val message: String
) : BaseResponseCode {
    NOT_FOUND_DOCUMENT(HttpStatus.NOT_FOUND, "분석 결과를 찾을 수 없습니다"),
    ACCESS_DENIED(HttpStatus.UNAUTHORIZED, "해당 분석 결과에 대한 접근 권한이 없습니다."),
}
