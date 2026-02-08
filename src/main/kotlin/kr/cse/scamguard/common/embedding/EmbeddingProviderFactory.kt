package kr.cse.scamguard.common.embedding

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

/**
 * 임베딩 제공자 선택을 위한 Factory
 * 
 * application.yml의 app.embedding.provider 설정에 따라 적절한 제공자를 반환합니다.
 * 
 * 사용 예:
 * - 로컬 테스트: provider=ollama (Qwen3-Embedding-4B 사용)
 * - 운영 환경: provider=openai 또는 다른 외부 API
 * 
 * RAG 호환성 주의:
 * 문서 수집 시 사용한 모델과 검색 시 사용하는 모델이 동일해야 합니다.
 * getModelName()을 통해 현재 활성 모델을 API 응답에 포함시키는 것을 권장합니다.
 */
@Component
class EmbeddingProviderFactory(
    private val providers: List<EmbeddingProvider>,
    @Value("\${app.embedding.provider}") private val activeProvider: String
) {
    
    fun getActiveProvider(): EmbeddingProvider {
        return when (activeProvider.lowercase()) {
            "gemini" -> getProvider(EmbeddingProviderType.GEMINI_EMBADDING_001)
            else -> throw IllegalArgumentException("지원하지 않는 임베딩 제공자: $activeProvider")
        }
    }
    
    fun getProvider(type: EmbeddingProviderType): EmbeddingProvider {
        return providers.find { provider ->
            when (type) {
                EmbeddingProviderType.GEMINI_EMBADDING_001 -> provider is GeminiEmbeddingProvider
            }
        } ?: throw IllegalStateException("${type.name} 임베딩 제공자가 설정되지 않았습니다")
    }
    
    /**
     * 현재 활성 임베딩 모델 정보 반환
     * API 응답에 포함하여 RAG 호환성 관리에 활용
     */
    fun getActiveModelInfo(): EmbeddingModelInfo {
        val provider = getActiveProvider()
        return EmbeddingModelInfo(
            providerType = activeProvider,
            modelName = provider.getModelName(),
            dimensions = provider.getDimensions()
        )
    }
}

enum class EmbeddingProviderType {
    GEMINI_EMBADDING_001
}

/**
 * 임베딩 모델 정보 DTO
 * 문서 수집 API 응답에 포함하여 검색 시 호환성 체크에 사용
 */
data class EmbeddingModelInfo(
    val providerType: String,
    val modelName: String,
    val dimensions: Int
)
