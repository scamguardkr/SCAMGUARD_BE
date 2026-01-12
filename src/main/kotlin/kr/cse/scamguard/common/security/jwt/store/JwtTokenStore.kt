package kr.cse.scamguard.common.security.jwt.store

import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@Component
class JwtTokenStore<K, V> : TokenStore<K, V> {

    private data class StoredValue<V>(
        val value: V,
        val expireAt: Long
    )

    private val store = ConcurrentHashMap<K, StoredValue<V>>()
    private val executor = Executors.newSingleThreadScheduledExecutor()

    init {
        executor.scheduleAtFixedRate({
            val now = System.currentTimeMillis()
            store.entries.removeIf { (_, v) -> now >= v.expireAt }
        }, 1, 1, TimeUnit.MINUTES)
    }

    override fun save(key: K, value: V, ttlSeconds: Long) {
        store[key] = StoredValue(value, System.currentTimeMillis() + ttlSeconds * 1000)
    }

    override fun find(key: K): V? {
        val data = store[key] ?: return null
        if (System.currentTimeMillis() >= data.expireAt) {
            store.remove(key)
            return null
        }
        return data.value
    }

    override fun delete(key: K) {
        store.remove(key)
    }

    override fun deleteAllByUserId(userId: Long) {
        store.keys.removeIf { key ->
            key.toString().startsWith("refreshToken:$userId:")
        }
    }
}
