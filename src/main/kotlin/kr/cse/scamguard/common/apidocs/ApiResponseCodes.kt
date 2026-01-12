package kr.cse.scamguard.common.apidocs

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class ApiResponseCodes(
    vararg val value: String
)

