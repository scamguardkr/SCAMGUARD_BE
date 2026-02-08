package kr.cse.scamguard.domain.document.dto

import io.swagger.v3.oas.annotations.media.Schema
import kr.cse.scamguard.common.embedding.EmbeddingModelInfo

/**
 * 사기 문서 수집 응답 DTO
 * 
 * RAG 호환성을 위해 사용된 임베딩 모델 정보를 포함합니다.
 * 검색 API 호출 시 동일한 모델을 사용해야 정확한 결과를 얻을 수 있습니다.
 */
@Schema(description = "사기 문서 수집 응답")
data class ScamDocumentIngestResponse(
    
    @field:Schema(description = "원본 문서 ID")
    val documentId: String,
    
    @field:Schema(description = "생성된 청크 수")
    val chunkCount: Int,
    
    @field:Schema(description = "사용된 임베딩 모델 정보 (검색 시 동일 모델 사용 필요)")
    val embeddingModel: EmbeddingModelInfo,
    
    @field:Schema(description = "처리 시간 (ms)")
    val processingTimeMs: Long
)
