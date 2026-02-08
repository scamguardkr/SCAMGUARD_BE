package kr.cse.scamguard.domain.scam.dto

import com.fasterxml.jackson.annotation.JsonFormat
import io.swagger.v3.oas.annotations.media.Schema
import kr.cse.scamguard.domain.scam.model.*
import java.time.LocalDateTime

/**
 * 사기 분석 결과 단건 상세 응답 DTO
 */
@Schema(description = "사기 분석 결과 상세 정보")
data class ScamAnalysisDetailResponse(

    @field:Schema(description = "분석 결과 문서 ID", example = "64f8a2b3c9e77b1234567890")
    val documentId: String,

    @field:Schema(description = "사용자 ID")
    val userId: Long,

    // 요청 정보
    @field:Schema(description = "사용자가 입력한 원문 프롬프트/메시지")
    val prompt: String,

    @field:Schema(description = "사용된 AI 모델")
    val aiModel: AiModelType,

    // 응답 정보
    @field:Schema(description = "위험도 수준")
    val riskLevel: RiskLevel,

    @field:Schema(description = "유사도 점수 (0.0 ~ 1.0)", example = "0.87")
    val similarityScore: Double,

    @field:Schema(description = "판단된 주요 사기 유형", example = "보이스피싱")
    val scamType: String,

    @field:Schema(description = "탐지된 위험 요소 목록")
    val detectedRisks: List<DetectedRiskDetail>,

    @field:Schema(description = "가장 유사하다고 판단된 사례")
    val similarCase: SimilarCaseDetail,

    @field:Schema(description = "상세 분석 정보")
    val analysisDetails: AnalysisDetailsDetail,

    // 메타 정보
    @field:Schema(description = "분석 생성 일시")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    val createdAt: LocalDateTime
) {

    companion object {
        fun from(document: ScamAnalysisDocument): ScamAnalysisDetailResponse {
            requireNotNull(document.id) { "Document ID cannot be null" }

            return ScamAnalysisDetailResponse(
                documentId = document.id,
                userId = document.userId,
                prompt = document.prompt,
                aiModel = document.aiModel,
                riskLevel = document.response.riskLevel,
                similarityScore = document.response.similarityScore,
                scamType = document.response.scamType,
                detectedRisks = document.response.detectedRisks.map { DetectedRiskDetail.from(it) },
                similarCase = SimilarCaseDetail.from(document.response.similarCase),
                analysisDetails = AnalysisDetailsDetail.from(document.response.analysisDetails),
                createdAt = document.createdAt
            )
        }
    }
}

// 내부 DTO들 (필요에 따라 계층 분리)

@Schema(description = "탐지된 개별 위험 요소")
data class DetectedRiskDetail(
    @field:Schema(description = "위험 요소 이름", example = "긴급성 유도")
    val name: String,

    @field:Schema(description = "위험 요소 설명")
    val description: String
) {
    companion object {
        fun from(risk: DetectedRisk): DetectedRiskDetail =
            DetectedRiskDetail(name = risk.name, description = risk.description)
    }
}

@Schema(description = "유사 사례 정보")
data class SimilarCaseDetail(
    @field:Schema(description = "유사 사례 이름", example = "2024년 실제 보이스피싱 사례 A")
    val name: String,

    @field:Schema(description = "유사 사례에 대한 설명 또는 정보")
    val information: String
) {
    companion object {
        fun from(case: SimilarCase): SimilarCaseDetail =
            SimilarCaseDetail(name = case.name, information = case.information)
    }
}

@Schema(description = "분석 수행 메타 정보")
data class AnalysisDetailsDetail(
    @field:Schema(description = "실제 분석에 사용된 모델명", example = "gpt-4o-mini")
    val model: String,

    @field:Schema(description = "분석 수행 일시")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    val analysisTime: LocalDateTime,

    @field:Schema(description = "전체 처리 소요 시간 (ms)", example = "2451")
    val totalProcessingTimeMs: Long
) {
    companion object {
        fun from(details: AnalysisDetails): AnalysisDetailsDetail =
            AnalysisDetailsDetail(
                model = details.model,
                analysisTime = details.analysisTime,
                totalProcessingTimeMs = details.totalProcessingTimeMs
            )
    }
}
