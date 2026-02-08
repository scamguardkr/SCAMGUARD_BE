package kr.cse.scamguard.domain.document.dto

import io.swagger.v3.oas.annotations.media.Schema
import kr.cse.scamguard.common.embedding.EmbeddingModelInfo
import kr.cse.scamguard.domain.document.model.ScamType

/**
 * 유사도 검색 응답 DTO (테스트용)
 */
@Schema(description = "유사도 검색 응답 (테스트용)")
data class ScamDocumentSearchResponse(
    
    @field:Schema(description = "사용된 임베딩 모델 정보")
    val embeddingModel: EmbeddingModelInfo,
    
    @field:Schema(description = "검색 결과 목록")
    val results: List<SearchHit>,
    
    @field:Schema(description = "총 결과 수")
    val totalHits: Int,
    
    @field:Schema(description = "검색 소요 시간 (ms)")
    val searchTimeMs: Long
)

/**
 * 개별 검색 결과
 */
@Schema(description = "개별 검색 결과")
data class SearchHit(
    
    @field:Schema(description = "유사도 점수 (0~1, 높을수록 유사)")
    val score: Float,
    
    @field:Schema(description = "문서 ID")
    val documentId: String,
    
    @field:Schema(description = "청크 인덱스")
    val chunkIndex: Int,
    
    @field:Schema(description = "청크 내용 (사기 수법 요약의 일부)")
    val content: String,
    
    @field:Schema(description = "사기 사건 제목")
    val scamTitle: String,
    
    @field:Schema(description = "사기 유형")
    val scamType: ScamType,
    
    @field:Schema(description = "사기 발생 시기")
    val occurredPeriod: String?,
    
    @field:Schema(description = "원본 출처 URL")
    val sourceUrl: String?,
    
    @field:Schema(description = "저장된 임베딩 모델명")
    val embeddingModel: String
)
