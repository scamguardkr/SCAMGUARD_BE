package kr.cse.scamguard.common.security.jwt.refresh.model

enum class RefreshTokenClaimKeys(val value: String) {
    USER_ID("id"),
    ROLE("role")
}
