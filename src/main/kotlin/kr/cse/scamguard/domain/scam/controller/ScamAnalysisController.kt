package kr.cse.scamguard.domain.scam.controller

import kr.cse.scamguard.common.model.CommonResponse
import kr.cse.scamguard.common.model.toSuccessResponse
import kr.cse.scamguard.common.security.SecurityUserDetails
import kr.cse.scamguard.domain.scam.dto.AiModelType
import kr.cse.scamguard.domain.scam.dto.ScamAnalysisModelResponse
import kr.cse.scamguard.domain.scam.dto.ScamAnalysisRequest
import kr.cse.scamguard.domain.scam.model.ScamAnalysisResponse
import kr.cse.scamguard.domain.scam.service.ScamAnalysisService
import org.slf4j.LoggerFactory
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
class ScamAnalysisController(
    private val scamAnalysisService: ScamAnalysisService
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
}
