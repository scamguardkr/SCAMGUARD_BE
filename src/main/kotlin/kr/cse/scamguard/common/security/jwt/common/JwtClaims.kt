package kr.cse.scamguard.common.security.jwt.common

interface JwtClaims {
    fun getClaims(): Map<String, Any?>
}
