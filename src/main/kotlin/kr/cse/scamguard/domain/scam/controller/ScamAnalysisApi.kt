package kr.cse.scamguard.domain.scam.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import kr.cse.scamguard.common.apidocs.ApiResponseCodes
import kr.cse.scamguard.common.model.CommonResponse
import kr.cse.scamguard.common.security.SecurityUserDetails
import kr.cse.scamguard.domain.scam.dto.AiModelType
import kr.cse.scamguard.domain.scam.dto.ScamAnalysisModelResponse
import kr.cse.scamguard.domain.scam.dto.ScamAnalysisRequest
import kr.cse.scamguard.domain.scam.model.ScamAnalysisResponse
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
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

    @Operation(
        summary = "현재 활성 분석 모델 정보 조회",
        description = """
            현재 분석에 사용할 수 있는 AI 모델 정보를 조회합니다.
            
            분석 API 호출 전에 이 정보를 확인하여 호환성을 체크할 수 있습니다.
        """
    )
    @GetMapping("/api/v1/scam/model/available")
    fun getAvailableModels() : CommonResponse<ScamAnalysisModelResponse>
}
