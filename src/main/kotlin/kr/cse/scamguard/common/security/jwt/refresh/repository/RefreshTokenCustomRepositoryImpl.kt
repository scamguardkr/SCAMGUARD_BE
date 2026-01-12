package kr.cse.scamguard.common.security.jwt.refresh.repository

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository

@Repository
class RefreshTokenCustomRepositoryImpl(
    private val redisTemplate: RedisTemplate<String, String>
) : RefreshTokenCustomRepository {

    override fun deleteAllByUserId(userId: Long) {
        val pattern = "refreshToken:$userId:*"
        val keys = redisTemplate.keys(pattern)

        if (keys.isNotEmpty()) {
            redisTemplate.delete(keys)
        }
    }
}
