package kr.cse.scamguard.common.security.jwt.refresh.service

import kr.cse.scamguard.common.security.jwt.refresh.model.RefreshTokenEntity

interface RefreshTokenService {
    fun save(refreshToken: RefreshTokenEntity)
    fun refresh(userId: Long, oldRefreshToken: String, newRefreshToken: String): RefreshTokenEntity
    fun deleteAll(userId: Long)
}
