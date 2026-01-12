package kr.cse.scamguard.common.security

object WebSecurityUrls {
    val READ_ONLY_PUBLIC_ENDPOINTS = arrayOf("/favicon.ico")
    val PUBLIC_ENDPOINTS = arrayOf("/api/v1/**")
    val ANONYMOUS_ENDPOINTS = arrayOf("/api/v2/**")
    val AUTHENTICATED_ENDPOINTS = arrayOf("/auth")
    val SWAGGER_ENDPOINTS = arrayOf("/api-docs/**", "/v3/api-docs/**", "/swagger-ui/**", "/swagger")
}
