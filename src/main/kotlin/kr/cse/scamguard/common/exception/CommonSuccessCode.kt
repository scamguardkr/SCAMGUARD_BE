package kr.cse.scamguard.common.exception

import org.springframework.http.HttpStatus

enum class CommonSuccessCode (
    override val httpStatus: HttpStatus,
    override val message: String
) : BaseResponseCode {

    OK(HttpStatus.OK, "")
}
