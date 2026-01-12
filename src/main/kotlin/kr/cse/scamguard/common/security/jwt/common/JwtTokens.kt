package kr.cse.scamguard.common.security.jwt.common

data class JwtTokens(
    val accessToken: String,
    val refreshToken: String,
    val userId: Long?
) {

    companion object {
        fun of(accessToken: String, refreshToken: String, userId: Long?): JwtTokens {
            return JwtTokens(accessToken, refreshToken, userId)
        }
    }
}
