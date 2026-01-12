package kr.cse.scamguard.common.security.jwt.common

object JwtClaimsParserUtil {
    inline fun <reified T> getClaimsValue(claims: JwtClaims, key: String): T? {
        val value = claims.getClaims()[key]
        return if (value != null && value is T) value else null
    }

    fun <T> getClaimsValue(claims: JwtClaims, key: String, valueConverter: (String) -> T): T? {
        val value = claims.getClaims()[key]
        return if (value != null) valueConverter(value as String) else null
    }
}
