package kr.cse.scamguard.common.security.jwt.refresh.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import kr.cse.scamguard.common.exception.CommonErrorCode
import kr.cse.scamguard.common.exception.CustomException
import kr.cse.scamguard.common.security.jwt.refresh.model.RefreshTokenEntity
import kr.cse.scamguard.common.security.jwt.store.TokenStore

@Service
class RefreshTokenServiceInMemoryImpl (
    private val store: TokenStore<String, RefreshTokenEntity>
): RefreshTokenService {
    private val log = LoggerFactory.getLogger(this::class.java)

    override fun save(refreshToken: RefreshTokenEntity) {
        store.save(key(refreshToken.userId!!, refreshToken.token), refreshToken, refreshToken.ttl)
        log.debug("리프레시 토큰 저장: {}", refreshToken)
    }

    override fun refresh(userId: Long, oldRefreshToken: String, newRefreshToken: String): RefreshTokenEntity {
        val existing = store.find(key(userId, oldRefreshToken))
            ?: throw CustomException(CommonErrorCode.INVALID_REFRESH_TOKEN)
        if (existing.token != oldRefreshToken) {
            log.warn("리프레시 토큰 불일치. expected: {}, actual: {}", existing.token, oldRefreshToken)
            store.deleteAllByUserId(userId)
            throw CustomException(CommonErrorCode.INVALID_REFRESH_TOKEN)
        }
        existing.rotate(newRefreshToken)
        store.save(key(userId, newRefreshToken), existing, existing.ttl)
        log.info("사용자 {} 리프레시 토큰 갱신", userId)
        return existing
    }

    override fun deleteAll(userId: Long) {
        store.deleteAllByUserId(userId)
        log.info("사용자 {} 리프레시 토큰 삭제", userId)
    }

    private fun key(userId: Long, token: String) = "$userId:${token.hashCode()}"
}
