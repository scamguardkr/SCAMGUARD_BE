package kr.cse.scamguard.common.security.jwt.store

interface TokenStore<K, V> {
    fun save(key: K, value: V, ttlSeconds: Long)
    fun find(key: K): V?
    fun delete(key: K)
    fun deleteAllByUserId(userId: Long)
}
