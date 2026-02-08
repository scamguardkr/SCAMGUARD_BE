package kr.cse.scamguard.common.embedding

import org.slf4j.LoggerFactory
import org.springframework.ai.embedding.EmbeddingResponse
import org.springframework.ai.google.genai.text.GoogleGenAiTextEmbeddingModel
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

/**
 * Google Gemini 기반 임베딩 Provider
 *
 * text-embedding-001 모델 사용 (무료 tier 사용 가능)
 */
@Component
@ConditionalOnProperty(
    prefix = "app.embedding",
    name = ["provider"],
    havingValue = "gemini"
)
class GeminiEmbeddingProvider(
    private val embeddingModel: GoogleGenAiTextEmbeddingModel,
    @Value("\${app.embedding.model-name}") private val modelName: String,
    @Value("\${app.embedding.dimensions}") private val dimensions: Int
) : EmbeddingProvider {

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun embed(text: String): FloatArray {
        log.debug("Google GenAI 임베딩 요청 - 텍스트 길이: {}", text.length)

        try {
            val embeddingResponse: EmbeddingResponse = embeddingModel.embedForResponse(listOf(text))
            val embedding = embeddingResponse.results.firstOrNull()?.output
                ?: throw IllegalStateException("임베딩 결과가 비어있습니다")

            log.debug("Google GenAI 임베딩 완료 - 벡터 차원: {}", embedding.size)
            return embedding
        } catch (e: Exception) {
            log.error("Google GenAI 임베딩 실패: {}", e.message, e)
            throw RuntimeException("임베딩 생성 중 오류 발생", e)
        }
    }

    override fun embed(texts: List<String>): List<FloatArray> {
        log.debug("Google GenAI 배치 임베딩 요청 - 청크 수: {}", texts.size)

        try {
            val embeddingResponse: EmbeddingResponse = embeddingModel.embedForResponse(texts)
            val embeddings = embeddingResponse.results.map { it.output }

            log.debug("Google GenAI 배치 임베딩 완료 - {} 개 벡터 생성", embeddings.size)
            return embeddings
        } catch (e: Exception) {
            log.error("Google GenAI 배치 임베딩 실패: {}", e.message, e)
            throw RuntimeException("배치 임베딩 생성 중 오류 발생", e)
        }
    }

    override fun getDimensions(): Int = dimensions

    override fun getModelName(): String = modelName
}
