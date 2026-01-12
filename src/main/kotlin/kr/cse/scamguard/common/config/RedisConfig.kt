package kr.cse.scamguard.common.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisKeyValueAdapter
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
@EnableRedisRepositories(enableKeyspaceEvents = RedisKeyValueAdapter.EnableKeyspaceEvents.ON_STARTUP)
class RedisConfig {

    @Bean
    fun redisTemplate(connectionFactory: RedisConnectionFactory): RedisTemplate<String, Any> {
        val template = RedisTemplate<String, Any>()
        val mapper = ObjectMapper().apply {
            registerModule(JavaTimeModule())
        }
        val serializer = GenericJackson2JsonRedisSerializer(mapper)

        template.keySerializer = StringRedisSerializer()
        template.valueSerializer = serializer
        template.connectionFactory = connectionFactory
        template.hashKeySerializer = StringRedisSerializer()
        template.hashValueSerializer = serializer

        return template
    }
}
