package kr.cse.scamguard.common.security.jwt.exception

import kr.cse.scamguard.common.exception.CustomException

class JwtErrorException (
    errorCode: JwtErrorCode,
    detail: String? = null
) : CustomException(errorCode, detail) {
}
