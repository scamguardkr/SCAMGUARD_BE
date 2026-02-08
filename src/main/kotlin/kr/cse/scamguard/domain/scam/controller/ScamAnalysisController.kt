package kr.cse.scamguard.domain.scam.controller

import kr.cse.scamguard.common.model.CommonResponse
import kr.cse.scamguard.common.model.toSuccessResponse
import kr.cse.scamguard.common.security.SecurityUserDetails
import kr.cse.scamguard.domain.scam.dto.*
import kr.cse.scamguard.domain.scam.model.ScamAnalysisResponse
import kr.cse.scamguard.domain.scam.service.ScamAnalysisHistoryService
import kr.cse.scamguard.domain.scam.service.ScamAnalysisService
import org.slf4j.LoggerFactory
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
class ScamAnalysisController(
    private val scamAnalysisService: ScamAnalysisService,
    private val scamAnalysisHistoryService: ScamAnalysisHistoryService
) : ScamAnalysisApi {

    private val log = LoggerFactory.getLogger(this::class.java)

    @PostMapping("/api/v1/scam/analyze")
    @PreAuthorize("isAuthenticated()")
    override fun analyzeScam(
        @RequestBody request: ScamAnalysisRequest,
        @RequestParam(name = "model") aiModelType: AiModelType,
        @AuthenticationPrincipal user: SecurityUserDetails
    ): CommonResponse<ScamAnalysisResponse> {
        log.info("execute analyzeScam")
        return scamAnalysisService.analyzeScam(user.getUserId(), request, aiModelType).toSuccessResponse()
    }

    @GetMapping("/api/v1/scam/model/available")
    override fun getAvailableModels(): CommonResponse<ScamAnalysisModelResponse> {
        return scamAnalysisService.getAvailableModels().toSuccessResponse()
    }

    @GetMapping("/api/v1/scam/analyze/results")
    @PreAuthorize("isAuthenticated()")
    override fun getUserAnalyzeReportList(
        @RequestParam(name = "page", defaultValue = "1") page: Int,
        @RequestParam(name = "limit", defaultValue = "10", required = false) limit: Int,
        @AuthenticationPrincipal user: SecurityUserDetails
    ): CommonResponse<ScamAnalysisHistoryPageResponse> {
        return scamAnalysisHistoryService.getUserScamAnalysisHistory(user.getUserId(), page, limit).toSuccessResponse()
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/api/v1/scam/analyze/result/{id}")
    override fun getUserDetailAnalyzeReport(
        @AuthenticationPrincipal user: SecurityUserDetails,
        @PathVariable id: String
    ): CommonResponse<ScamAnalysisDetailResponse> {
        return scamAnalysisHistoryService.getScamAnalysisDetail(documentId = id, userId = user.getUserId()).toSuccessResponse()
    }
}
