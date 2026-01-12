package kr.cse.scamguard.common.exception

open class CustomException (
    val errorCode: BaseResponseCode,
    val detail: String? = null,
) : RuntimeException() {

}

