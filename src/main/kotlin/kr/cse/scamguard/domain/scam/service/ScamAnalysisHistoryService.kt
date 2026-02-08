package kr.cse.scamguard.domain.scam.service

import kr.cse.scamguard.common.exception.CustomException
import kr.cse.scamguard.common.model.Criteria
import kr.cse.scamguard.domain.scam.dto.ScamAnalysisDetailResponse
import kr.cse.scamguard.domain.scam.dto.ScamAnalysisHistoryListResponse
import kr.cse.scamguard.domain.scam.dto.ScamAnalysisHistoryPageResponse
import kr.cse.scamguard.domain.scam.exception.ScamErrorCode
import kr.cse.scamguard.domain.scam.repository.ScamAnalysisDocumentRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

@Service
class ScamAnalysisHistoryService (
    private val scamAnalysisDocumentRepository: ScamAnalysisDocumentRepository
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    fun getUserScamAnalysisHistory(
        userId: Long,
        page: Int?,
        limit: Int?
    ): ScamAnalysisHistoryPageResponse {
        val criteria = Criteria.of(page, limit)

        val pageable = PageRequest.of(
            criteria.page,
            criteria.limit,
            Sort.by(Sort.Direction.DESC, "createdAt")
        )

        val pageResult = scamAnalysisDocumentRepository.findByUserId(userId, pageable)

        return ScamAnalysisHistoryPageResponse.of(pageResult) { doc ->
            ScamAnalysisHistoryListResponse.from(doc)
        }
    }

    fun getScamAnalysisDetail(
        documentId: String,
        userId: Long
    ): ScamAnalysisDetailResponse {
        val document = scamAnalysisDocumentRepository.findById(documentId)
            .orElseThrow { CustomException(ScamErrorCode.NOT_FOUND_DOCUMENT) }

        if (document.userId != userId) {
            throw CustomException(ScamErrorCode.ACCESS_DENIED)
        }

        return ScamAnalysisDetailResponse.from(document)
    }
}
