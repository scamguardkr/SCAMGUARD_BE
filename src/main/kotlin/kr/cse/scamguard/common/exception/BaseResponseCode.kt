package kr.cse.scamguard.common.exception

import org.springframework.http.HttpStatus

interface BaseResponseCode {
    val httpStatus: HttpStatus
    val message: String
    val code: String
        get() = (this as Enum<*>).name
}
