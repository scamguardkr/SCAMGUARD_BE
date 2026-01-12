package kr.cse.scamguard.domain.scam.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class ScamAnalysisResponse(
    @JsonProperty("risk_level")
    val riskLevel: RiskLevel,

    @JsonProperty("similarity_score")
    val similarityScore: Double,

    @JsonProperty("scam_type")
    val scamType: String,

    @JsonProperty("detected_risks")
    val detectedRisks: List<DetectedRisk>,

    @JsonProperty("similar_case")
    val similarCase: SimilarCase,

    @JsonProperty("analysis_details")
    val analysisDetails: AnalysisDetails
)

data class DetectedRisk(
    @JsonProperty("name")
    val name: String,

    @JsonProperty("description")
    val description: String
)

data class SimilarCase(
    @JsonProperty("name")
    val name: String,

    @JsonProperty("information")
    val information: String
)

data class AnalysisDetails(
    @JsonProperty("model")
    val model: String,

    @JsonProperty("analysis_time")
    val analysisTime: LocalDateTime,

    @JsonProperty("total_processing_time_ms")
    val totalProcessingTimeMs: Long
)

data class LlmScamAnalysisResult(
    @JsonProperty("risk_level")
    val riskLevel: String,

    @JsonProperty("similarity_score")
    val similarityScore: Double,

    @JsonProperty("scam_type")
    val scamType: String,

    @JsonProperty("detected_risks")
    val detectedRisks: List<DetectedRisk>,

    @JsonProperty("similar_case")
    val similarCase: SimilarCase
)
