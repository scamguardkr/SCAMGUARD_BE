package kr.cse.scamguard.common.security.jwt.refresh.model

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash

@RedisHash("refreshToken")
data class RefreshTokenEntity(
    @Id
    var id: String? = null,
    val userId: Long?,
    val ttl: Long,
    var token: String
) {
    companion object {
        fun of(userId: Long?, token: String, ttl: Long): RefreshTokenEntity {
            return RefreshTokenEntity(
                id = userId.toString(),
                userId = userId,
                token = token,
                ttl = ttl
            )
        }
    }

    fun rotate(token: String) {
        this.token = token
    }
}
