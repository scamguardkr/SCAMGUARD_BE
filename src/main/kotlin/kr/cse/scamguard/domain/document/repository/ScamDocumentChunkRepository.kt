package kr.cse.scamguard.domain.document.repository

import kr.cse.scamguard.domain.document.model.ScamDocumentChunk
import kr.cse.scamguard.domain.document.model.ScamType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository

/**
 * 사기 문서 청크 Repository
 * 
 * Spring Data Elasticsearch가 자동으로 구현체를 생성합니다.
 * 
 * TODO: 벡터 유사도 검색은 별도의 쿼리 메서드 필요
 * - NativeQuery 또는 ElasticsearchOperations 활용
 * - Script Score Query 또는 kNN Search 사용
 */
interface ScamDocumentChunkRepository : ElasticsearchRepository<ScamDocumentChunk, String> {
    
    /**
     * 원본 문서 ID로 모든 청크 조회
     */
    fun findByDocumentId(documentId: String): List<ScamDocumentChunk>
    
    /**
     * 원본 문서 ID로 청크 삭제
     */
    fun deleteByDocumentId(documentId: String)
    
    /**
     * 사기 유형별 조회
     */
    fun findByScamType(scamType: ScamType, pageable: Pageable): Page<ScamDocumentChunk>
    
    /**
     * 사기 제목으로 검색
     */
    fun findByScamTitleContaining(scamTitle: String, pageable: Pageable): Page<ScamDocumentChunk>
    
    /**
     * 임베딩 모델명으로 조회 (RAG 호환성 체크용)
     */
    fun findByEmbeddingModel(embeddingModel: String, pageable: Pageable): Page<ScamDocumentChunk>
    
    /**
     * 특정 모델로 임베딩된 문서 수 카운트
     */
    fun countByEmbeddingModel(embeddingModel: String): Long
}
