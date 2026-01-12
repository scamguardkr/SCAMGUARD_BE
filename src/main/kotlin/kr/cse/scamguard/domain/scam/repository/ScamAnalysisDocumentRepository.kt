package kr.cse.scamguard.domain.scam.repository

import kr.cse.scamguard.domain.scam.dto.AiModelType
import kr.cse.scamguard.domain.scam.model.RiskLevel
import kr.cse.scamguard.domain.scam.model.ScamAnalysisDocument
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import java.time.LocalDateTime

interface ScamAnalysisDocumentRepository : MongoRepository<ScamAnalysisDocument, String> {

    // 사용자별 분석 이력 조회
    fun findByUserId(userId: Long, pageable: Pageable): Page<ScamAnalysisDocument>

    // 위험도별 조회
    @Query("{ 'response.riskLevel': ?0 }")
    fun findByRiskLevel(riskLevel: RiskLevel, pageable: Pageable): Page<ScamAnalysisDocument>

    // AI 모델별 조회
    fun findByAiModel(aiModel: AiModelType, pageable: Pageable): Page<ScamAnalysisDocument>

    // 날짜 범위로 조회
    fun findByCreatedAtBetween(
        start: LocalDateTime,
        end: LocalDateTime,
        pageable: Pageable
    ): Page<ScamAnalysisDocument>

    // 유사도 이상인 케이스 조회
    @Query("{ 'response.similarityScore': { \$gte: ?0 } }")
    fun findBySimilarityScoreGreaterThanEqual(
        score: Double,
        pageable: Pageable
    ): Page<ScamAnalysisDocument>

    // 사기 유형별 조회
    @Query("{ 'response.scamType': { \$regex: ?0, \$options: 'i' } }")
    fun findByScamTypeContaining(
        keyword: String,
        pageable: Pageable
    ): Page<ScamAnalysisDocument>
}
