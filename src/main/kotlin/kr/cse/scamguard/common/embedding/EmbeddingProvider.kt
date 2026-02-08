package kr.cse.scamguard.common.embedding

/**
 * 임베딩 제공자 인터페이스
 * 
 * 문서 수집(indexing)과 검색(retrieval) 시 동일한 모델을 사용해야 RAG 호환성이 보장됩니다.
 * 모델명과 차원수를 제공하여 API 레벨에서 호환성을 관리할 수 있습니다.
 */
interface EmbeddingProvider {
    
    /**
     * 단일 텍스트를 벡터로 변환
     */
    fun embed(text: String): FloatArray
    
    /**
     * 여러 텍스트를 벡터로 변환 (배치 처리)
     * 
     * 기본 구현은 개별 호출이지만, 효율성을 위해 오버라이드 권장
     * 
     * TODO [비동기 처리 가이드]
     * 대용량 처리가 필요한 경우:
     * 1. suspend fun embedAsync(texts: List<String>): List<FloatArray>
     * 2. Flow<FloatArray> 활용
     * 3. 또는 Spring @Async + CompletableFuture 활용
     */
    fun embed(texts: List<String>): List<FloatArray> {
        return texts.map { embed(it) }
    }
    
    /**
     * 벡터 차원수 반환
     * Elasticsearch dense_vector 필드 설정 시 필요
     */
    fun getDimensions(): Int
    
    /**
     * 임베딩 모델명 반환
     * RAG 호환성을 위해 검색 시에도 동일 모델 사용 여부를 확인할 때 사용
     */
    fun getModelName(): String
}
