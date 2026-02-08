package kr.cse.scamguard.domain.scam.dto

import io.swagger.v3.oas.annotations.media.Schema
import kr.cse.scamguard.domain.scam.model.RiskLevel
import kr.cse.scamguard.domain.scam.model.ScamAnalysisDocument
import java.time.LocalDateTime

/**
 * 사용자 사기 분석 내역 DTO
 */
@Schema(description = "사용자 사기 분석 내역")
data class ScamAnalysisHistoryListResponse(

    @field:Schema(description = "분석 결과 ID (문서 ID)", example = "64f8a2b3c9e77b1234567890")
    val documentId: String,

    @field:Schema(description = "사기 유형", example = "피싱", allowableValues = ["피싱", "보이스피싱", "투자사기", "로맨스스캠", "기타"])
    val scamType: String,

    @field:Schema(description = "분석 생성 일시", example = "2025-02-08T14:30:22")
    val createdAt: LocalDateTime,

    @field:Schema(description = "위험도", example = "HIGH")
    val riskLevel: RiskLevel? = null,

    @field:Schema(description = "유사도 점수", example = "0.92")
    val similarityScore: Double? = null
) {
    companion object {
        fun from(document: ScamAnalysisDocument): ScamAnalysisHistoryListResponse {
            return ScamAnalysisHistoryListResponse(
                documentId = document.id ?: throw IllegalStateException("Document ID is null"),
                scamType = document.response.scamType,
                createdAt = document.createdAt,
                riskLevel = document.response.riskLevel,
                similarityScore = document.response.similarityScore
            )
        }

        fun from(documents: List<ScamAnalysisDocument>): List<ScamAnalysisHistoryListResponse> {
            return documents.map { from(it) }
        }
    }
}
