package kr.cse.scamguard.domain.scam.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import kr.cse.scamguard.common.apidocs.ApiResponseCodes
import kr.cse.scamguard.common.model.CommonResponse
import kr.cse.scamguard.common.security.SecurityUserDetails
import kr.cse.scamguard.domain.scam.dto.AiModelType
import kr.cse.scamguard.domain.scam.dto.ScamAnalysisRequest
import kr.cse.scamguard.domain.scam.model.ScamAnalysisResponse
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam

@Tag(name = "(Normal) ScamAnalysis: 사기 분석", description = "사용자 입력을 이용하여 사기 여부를 분석한다")
interface ScamAnalysisApi {

    @ApiResponseCodes(value = [
        "CommonSuccessCode.OK"
    ])
    @Operation(
        summary = "사기 분석 AI 호출",
        description = "<h3>사기 분석 AI 호출</h3><ul><li>...</li></ul>"
    )
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/api/v1/scam/analyze")
    fun analyzeScam(
        @RequestBody request: ScamAnalysisRequest,
        @RequestParam(name = "model") aiModelType: AiModelType,
        @AuthenticationPrincipal user: SecurityUserDetails
    ): CommonResponse<ScamAnalysisResponse>
}
