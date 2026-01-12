package kr.cse.scamguard.common.security.jwt.forbidden.model

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.TimeToLive

@RedisHash("forbiddenToken")
data class ForbiddenTokenEntity(
    @Id
    val accessToken: String,
    val userId: Long,
    @TimeToLive
    val ttl: Long
) {
    companion object {
        fun of(accessToken: String, userId: Long, ttl: Long): ForbiddenTokenEntity {
            return ForbiddenTokenEntity(accessToken, userId, ttl)
        }
    }
}
