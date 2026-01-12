package kr.cse.scamguard.common.security.jwt.forbidden.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import kr.cse.scamguard.common.security.jwt.forbidden.model.ForbiddenTokenEntity
import kr.cse.scamguard.common.security.jwt.store.TokenStore
import java.time.Duration
import java.time.LocalDateTime

@Service
class ForbiddenTokenServiceInMemoryImpl (
    private val store: TokenStore<String, ForbiddenTokenEntity>
): ForbiddenTokenService {

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun createForbiddenToken(userId: Long, accessToken: String, expiredAt: LocalDateTime) {
        val ttlSeconds = Duration.between(LocalDateTime.now(), expiredAt).seconds
        store.save(accessToken, ForbiddenTokenEntity(accessToken, userId, ttlSeconds), ttlSeconds)
        log.info("Forbidden 토큰 저장: {}", accessToken)
    }

    override fun checkForbidden(accessToken: String): Boolean {
        return store.find(accessToken) != null
    }
}
