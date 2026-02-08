package kr.cse.scamguard.domain.document.service

import kr.cse.scamguard.common.embedding.EmbeddingProviderFactory
import kr.cse.scamguard.domain.document.dto.ScamDocumentSearchRequest
import kr.cse.scamguard.domain.document.dto.ScamDocumentSearchResponse
import kr.cse.scamguard.domain.document.dto.SearchHit
import kr.cse.scamguard.domain.document.model.ScamDocumentChunk
import org.slf4j.LoggerFactory
import org.springframework.data.elasticsearch.client.elc.NativeQuery
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.SearchHits
import org.springframework.data.elasticsearch.core.query.StringQuery
import org.springframework.stereotype.Service

/**
 * 벡터 유사도 검색 서비스 (테스트용)
 * 
 * Elasticsearch의 cosineSimilarity 함수를 사용하여 벡터 유사도를 계산합니다.
 */
@Service
class ScamDocumentSearchService(
    private val embeddingProviderFactory: EmbeddingProviderFactory,
    private val elasticsearchOperations: ElasticsearchOperations
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    /**
     * 쿼리 문장과 유사한 문서 청크를 검색합니다.
     *
     * @param request 검색 요청 (쿼리 문장, 반환 개수)
     * @return 유사도 점수와 함께 검색 결과 반환
     */
    fun search(request: ScamDocumentSearchRequest): ScamDocumentSearchResponse {
        val startTime = System.currentTimeMillis()

        log.info("유사도 검색 시작 - 쿼리: {}, topK: {}",
            request.query.take(50), request.topK)

        try {
            // 1. 쿼리 문장 임베딩
            val embeddingProvider = embeddingProviderFactory.getActiveProvider()
            val queryEmbedding = embeddingProvider.embed(request.query)

            log.debug("쿼리 임베딩 완료 - 모델: {}, 차원: {}",
                embeddingProvider.getModelName(), queryEmbedding.size)

            // 2. Elasticsearch 벡터 검색 (script_score 사용)
            val searchHits = executeVectorSearch(queryEmbedding, request.topK)

            // 3. 결과 변환
            val results = searchHits.searchHits.map { hit ->
                val chunk = hit.content
                SearchHit(
                    score = hit.score.toFloat(),
                    documentId = chunk.documentId,
                    chunkIndex = chunk.chunkIndex,
                    content = chunk.content,
                    scamTitle = chunk.scamTitle,
                    scamType = chunk.scamType,
                    occurredPeriod = chunk.occurredPeriod,
                    sourceUrl = chunk.sourceUrl,
                    embeddingModel = chunk.embeddingModel
                )
            }

            val searchTime = System.currentTimeMillis() - startTime

            log.info("유사도 검색 완료 - 결과: {}개, 소요시간: {}ms",
                results.size, searchTime)

            return ScamDocumentSearchResponse(
                embeddingModel = embeddingProviderFactory.getActiveModelInfo(),
                results = results,
                totalHits = searchHits.totalHits.toInt(),
                searchTimeMs = searchTime
            )
        } catch (e: Exception) {
            log.error("벡터 검색 중 오류 발생", e)
            throw RuntimeException("벡터 검색 실패: ${e.message}", e)
        }
    }

    /**
     * Elasticsearch script_score 쿼리로 벡터 유사도 검색
     *
     * cosineSimilarity 함수 사용 (결과: -1 ~ 1, 1에 가까울수록 유사)
     * 점수를 0~1 범위로 정규화: (cosineSimilarity + 1) / 2
     */
    private fun executeVectorSearch(
        queryVector: FloatArray,
        topK: Int
    ): SearchHits<ScamDocumentChunk> {

        // 벡터를 JSON 배열로 변환
        val vectorJson = queryVector.joinToString(", ") { it.toString() }

        // StringQuery는 query 부분만 전달 (전체 search body가 아님!)
        val queryJson = """
        {
            "script_score": {
                "query": {
                    "match_all": {}
                },
                "script": {
                    "source": "(cosineSimilarity(params.query_vector, 'embedding') + 1.0) / 2.0",
                    "params": {
                        "query_vector": [$vectorJson]
                    }
                }
            }
        }
        """.trimIndent()

        log.debug("Elasticsearch 쿼리: {}", queryJson)

        // StringQuery 생성 후 NativeQuery로 래핑
        val stringQuery = StringQuery(queryJson)

        val nativeQuery = NativeQuery.builder()
            .withQuery(stringQuery)
            .withMaxResults(topK)
            .build()

        return elasticsearchOperations.search(nativeQuery, ScamDocumentChunk::class.java)
    }
}
