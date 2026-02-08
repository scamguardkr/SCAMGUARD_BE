package kr.cse.scamguard.domain.document.service

import kr.cse.scamguard.common.embedding.EmbeddingProviderFactory
import kr.cse.scamguard.domain.document.dto.ScamDocumentIngestRequest
import kr.cse.scamguard.domain.document.dto.ScamDocumentIngestResponse
import kr.cse.scamguard.domain.document.model.ScamDocumentChunk
import kr.cse.scamguard.domain.document.repository.ScamDocumentChunkRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.UUID

@Service
class ScamDocumentIngestService(
    private val chunkingService: ChunkingService,
    private val embeddingProviderFactory: EmbeddingProviderFactory,
    private val documentChunkRepository: ScamDocumentChunkRepository
) {
    
    private val log = LoggerFactory.getLogger(this::class.java)
    
    /**
     * 문서 수집 및 벡터화
     * 
     * @param request 문서 수집 요청
     * @return 수집 결과 (청크 수, 사용된 모델 정보 포함)
     */
    fun ingestDocument(request: ScamDocumentIngestRequest): ScamDocumentIngestResponse {
        val startTime = System.currentTimeMillis()
        val documentId = UUID.randomUUID().toString()
        
        log.info("문서 수집 시작 - documentId: {}, 제목: {}, 사기유형: {}",
            documentId, request.scamTitle, request.scamType)
        
        // 1. scamMethodSummary를 청킹 (벡터화 대상)
        val chunks = chunkingService.chunk(request.scamMethodSummary)
        
        log.debug("청킹 완료 - {} 개의 청크 생성", chunks.size)
        
        // 2. 임베딩 생성
        val embeddingProvider = embeddingProviderFactory.getActiveProvider()
        val embeddings = embeddingProvider.embed(chunks)
        
        log.debug("임베딩 완료 - 모델: {}, 차원: {}",
            embeddingProvider.getModelName(), embeddingProvider.getDimensions())
        
        // 3. 청크 엔티티 생성 및 저장
        val documentChunks = chunks.mapIndexed { index, chunkContent ->
            ScamDocumentChunk(
                documentId = documentId,
                chunkIndex = index,
                content = chunkContent,
                embedding = embeddings[index],
                embeddingModel = embeddingProvider.getModelName(),
                scamTitle = request.scamTitle,
                scamType = request.scamType,
                occurredPeriod = request.occurredPeriod,
                sourceUrl = request.sourceUrl,
                createdAt = LocalDate.now()
            )
        }
        
        documentChunkRepository.saveAll(documentChunks)
        
        val processingTime = System.currentTimeMillis() - startTime
        
        log.info("문서 수집 완료 - documentId: {}, 청크 수: {}, 처리 시간: {}ms",
            documentId, chunks.size, processingTime)
        
        return ScamDocumentIngestResponse(
            documentId = documentId,
            chunkCount = chunks.size,
            embeddingModel = embeddingProviderFactory.getActiveModelInfo(),
            processingTimeMs = processingTime
        )
    }
    
    /**
     * 문서 삭제
     */
    fun deleteDocument(documentId: String) {
        log.info("문서 삭제 - documentId: {}", documentId)
        documentChunkRepository.deleteByDocumentId(documentId)
    }
    
    /**
     * 현재 활성 임베딩 모델 정보 조회
     * 검색 API에서 호환성 체크에 사용
     */
    fun getActiveEmbeddingModelInfo() = embeddingProviderFactory.getActiveModelInfo()
}
