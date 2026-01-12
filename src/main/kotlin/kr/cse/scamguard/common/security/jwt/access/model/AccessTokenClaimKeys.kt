package kr.cse.scamguard.common.security.jwt.access.model

enum class AccessTokenClaimKeys(val value: String) {
    USER_ID("id"),
    ROLE("role")
}
