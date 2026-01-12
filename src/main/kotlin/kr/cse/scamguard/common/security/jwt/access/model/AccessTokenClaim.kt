package kr.cse.scamguard.common.security.jwt.access.model


import kr.cse.scamguard.common.security.jwt.common.JwtClaims

data class AccessTokenClaim(
    private val claims: Map<String, Any?>
) : JwtClaims {
    companion object {
        fun of(userId: Long?, role: String): AccessTokenClaim {
            val claims = mapOf(
                AccessTokenClaimKeys.USER_ID.value to userId.toString(),
                AccessTokenClaimKeys.ROLE.value to role
            )
            return AccessTokenClaim(claims)
        }
    }

    override fun getClaims(): Map<String, Any?> {
        return claims
    }
}
