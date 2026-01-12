package kr.cse.scamguard.common.security.jwt.forbidden.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import kr.cse.scamguard.common.security.jwt.forbidden.model.ForbiddenTokenEntity
import kr.cse.scamguard.common.security.jwt.forbidden.repository.ForbiddenTokenRepository
import java.time.Duration
import java.time.LocalDateTime


@Service
class ForbiddenTokenServiceRedisImpl(
    private val forbiddenTokenRepository: ForbiddenTokenRepository
): ForbiddenTokenService {
    private val log = LoggerFactory.getLogger(this::class.java)

    override fun createForbiddenToken(userId: Long, accessToken: String, expiredAt: LocalDateTime) {
        val now = LocalDateTime.now()
        val timeToLive = Duration.between(now, expiredAt).seconds

        val forbiddenToken = ForbiddenTokenEntity.of(accessToken, userId, timeToLive)
        forbiddenTokenRepository.save(forbiddenToken)
    }

    override fun checkForbidden(accessToken: String): Boolean {
        return forbiddenTokenRepository.existsById(accessToken)
    }
}
