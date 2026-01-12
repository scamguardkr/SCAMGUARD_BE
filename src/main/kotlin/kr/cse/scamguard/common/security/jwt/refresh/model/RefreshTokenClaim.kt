package kr.cse.scamguard.common.security.jwt.refresh.model

import kr.cse.scamguard.common.security.jwt.common.JwtClaims

data class RefreshTokenClaim(
    private val claims: Map<String, Any?>
) : JwtClaims {
    companion object {
        fun of(userId: Long?, role: String): RefreshTokenClaim {
            val claims = mapOf(
                RefreshTokenClaimKeys.USER_ID.value to userId.toString(),
                RefreshTokenClaimKeys.ROLE.value to role
            )
            return RefreshTokenClaim(claims)
        }
    }

    override fun getClaims(): Map<String, Any?> {
        return claims
    }
}
