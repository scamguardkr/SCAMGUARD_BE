package kr.cse.scamguard.common.security.jwt.refresh.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import kr.cse.scamguard.common.exception.CommonErrorCode
import kr.cse.scamguard.common.exception.CustomException
import kr.cse.scamguard.common.security.jwt.exception.JwtErrorCode
import kr.cse.scamguard.common.security.jwt.refresh.model.RefreshTokenEntity
import kr.cse.scamguard.common.security.jwt.refresh.repository.RefreshTokenRepository

@Service
class RefreshTokenServiceRedisImpl(
    private val refreshTokenRepository: RefreshTokenRepository
): RefreshTokenService {
    private val log = LoggerFactory.getLogger(this::class.java)

    override fun save(refreshToken: RefreshTokenEntity) {
        refreshTokenRepository.save(refreshToken)
        log.debug("리프레시 토큰 저장 : {}", refreshToken)
    }

    override fun refresh(userId: Long, oldRefreshToken: String, newRefreshToken: String): RefreshTokenEntity {
        val refreshToken = findOrElseThrow(userId)
        validateToken(oldRefreshToken, refreshToken)

        refreshToken.rotate(newRefreshToken)
        refreshTokenRepository.save(refreshToken)

        log.info("사용자 {}의 리프레시 토큰 갱신", userId)
        return refreshToken
    }

    override fun deleteAll(userId: Long) {
        refreshTokenRepository.deleteAllByUserId(userId)
        log.info("사용자 {}의 리프레시 토큰 삭제", userId)
    }

    private fun findOrElseThrow(userId: Long): RefreshTokenEntity {
        return refreshTokenRepository.findById(userId.toString())
            .orElseThrow { CustomException(JwtErrorCode.INVALID_TOKEN_FORMAT) }
    }

    private fun validateToken(requestRefreshToken: String, expectedRefreshToken: RefreshTokenEntity) {
        if (isTakenAway(requestRefreshToken, expectedRefreshToken.token)) {
            log.warn("리프레시 토큰 불일치(탈취). expected : {}, actual : {}", requestRefreshToken, expectedRefreshToken.token)
            expectedRefreshToken.userId?.let { refreshTokenRepository.deleteAllByUserId(it) }
            log.info("사용자 {}의 리프레시 토큰 삭제", expectedRefreshToken.userId)

            throw CustomException(CommonErrorCode.INVALID_REFRESH_TOKEN)
        }
    }

    private fun isTakenAway(requestRefreshToken: String, expectedRefreshToken: String): Boolean {
        return expectedRefreshToken != requestRefreshToken
    }
}
