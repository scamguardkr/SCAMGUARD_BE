package kr.cse.scamguard.domain.scam.service

import kr.cse.scamguard.domain.scam.dto.AiModelType
import kr.cse.scamguard.domain.scam.dto.ScamAnalysisModelResponse
import kr.cse.scamguard.domain.scam.model.AnalysisDetails
import kr.cse.scamguard.domain.scam.model.RiskLevel
import kr.cse.scamguard.domain.scam.dto.ScamAnalysisRequest
import kr.cse.scamguard.domain.scam.model.ScamAnalysisDocument
import kr.cse.scamguard.domain.scam.model.ScamAnalysisResponse
import kr.cse.scamguard.domain.scam.repository.ScamAnalysisDocumentRepository
import kr.cse.scamguard.domain.scam.service.llm.LlmProviderFactory
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ScamAnalysisService(
    private val llmProviderFactory: LlmProviderFactory,
    private val scamAnalysisRepository: ScamAnalysisDocumentRepository
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    fun analyzeScam(userId: Long, request: ScamAnalysisRequest, aiModelType: AiModelType): ScamAnalysisResponse {
        log.info("사기 분석 시작 - 모델: {}, 프롬프트 길이: {}", aiModelType, request.prompt.length)

        val startTime = System.currentTimeMillis()
        val analysisTime = LocalDateTime.now()

        // LLM Provider 선택
        val provider = llmProviderFactory.getProvider(aiModelType)

        // 분석 실행
        val llmResult = try {
            provider.analyzeScam(request.prompt)
        } catch (e: Exception) {
            log.error("LLM 분석 중 오류 발생", e)
            throw RuntimeException("사기 분석 중 오류가 발생했습니다: ${e.message}", e)
        }

        val processingTime = System.currentTimeMillis() - startTime

        log.info("사기 분석 완료 - 위험도: {}, 유사도: {}%, 처리시간: {}ms",
            llmResult.riskLevel, llmResult.similarityScore, processingTime)

        // 응답 생성
        val response = ScamAnalysisResponse(
            riskLevel = parseRiskLevel(llmResult.riskLevel),
            similarityScore = llmResult.similarityScore,
            scamType = llmResult.scamType,
            detectedRisks = llmResult.detectedRisks,
            similarCase = llmResult.similarCase,
            analysisDetails = AnalysisDetails(
                model = provider.getModelName(),
                analysisTime = analysisTime,
                totalProcessingTimeMs = processingTime
            )
        )

        // MongoDB에 저장
        saveAnalysis(userId, request, response, aiModelType)

        return response
    }

    fun getAvailableModels(): ScamAnalysisModelResponse {
        return ScamAnalysisModelResponse(AiModelType.entries)
    }

    private fun saveAnalysis(
        userId: Long,
        request: ScamAnalysisRequest,
        response: ScamAnalysisResponse,
        aiModelType: AiModelType
    ) {
        try {
            val document = ScamAnalysisDocument.from(userId, request, response, aiModelType)
            scamAnalysisRepository.save(document)
            log.info("분석 결과 저장 완료 - 사용자: {}", userId)
        } catch (e: Exception) {
            log.error("분석 결과 저장 실패", e)
        }
    }

    // 사용자별 분석 이력 조회
    fun getUserAnalysisHistory(userId: Long, pageable: Pageable): Page<ScamAnalysisDocument> {
        return scamAnalysisRepository.findByUserId(userId, pageable)
    }

    // 위험도별 조회
    fun getAnalysisByRiskLevel(riskLevel: RiskLevel, pageable: Pageable): Page<ScamAnalysisDocument> {
        return scamAnalysisRepository.findByRiskLevel(riskLevel, pageable)
    }

    // 고위험 케이스 조회 (유사도 80% 이상)
    fun getHighRiskCases(pageable: Pageable): Page<ScamAnalysisDocument> {
        return scamAnalysisRepository.findBySimilarityScoreGreaterThanEqual(80.0, pageable)
    }

    private fun parseRiskLevel(level: String): RiskLevel {
        return try {
            RiskLevel.valueOf(level.uppercase())
        } catch (e: IllegalArgumentException) {
            log.warn("알 수 없는 위험도 레벨: {}. SUSPICIOUS로 설정", level)
            RiskLevel.SUSPICIOUS
        }
    }
}
