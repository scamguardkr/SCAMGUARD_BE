package kr.cse.scamguard.common.security.jwt.refresh.repository

interface RefreshTokenCustomRepository {
    fun deleteAllByUserId(userId: Long)
}
