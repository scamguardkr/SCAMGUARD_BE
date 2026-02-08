package kr.cse.scamguard.common.apidocs

import kr.cse.scamguard.common.exception.BaseResponseCode
import org.springframework.stereotype.Component

@Component
class ResponseCodeFinder {

    private val packagesToScan = listOf(
        "kr.cse.scamguard.common.exception",
        "kr.cse.scamguard.domain.user.exception",
        "kr.cse.scamguard.domain.scam.exception",
        "kr.cse.scamguard.common.security.jwt.exception"
    )

    @Suppress("UNCHECKED_CAST")
    fun find(codeString: String): BaseResponseCode {
        val parts = codeString.split(".")
        if (parts.size != 2) throw IllegalArgumentException("ApiResponseCodes 응답 코드 형식 에러: 'EnumClassName.CONSTANT_NAME' 형태로 작성해주세요. (입력: $codeString)")

        val enumClassName = parts[0]
        val codeName = parts[1]

        val foundClass = packagesToScan.firstNotNullOfOrNull { pkg ->
            try {
                Class.forName("$pkg.$enumClassName")
            } catch (e: ClassNotFoundException) {
                null
            }
        } ?: throw ClassNotFoundException("ApiResponseCodes 응답 코드 에러: 작성한 Enum 클래스 '$enumClassName'를 찾을 수 없습니다. ResponseCodeFinder packagesToScan에 패키지가 등록되었는지 확인해주세요.")

        if (!BaseResponseCode::class.java.isAssignableFrom(foundClass)) {
            throw IllegalArgumentException("클래스 '$enumClassName'는 BaseResponseCode를 구현해야 합니다.")
        }

        return java.lang.Enum.valueOf(foundClass as Class<out Enum<*>>, codeName) as BaseResponseCode
    }
}

