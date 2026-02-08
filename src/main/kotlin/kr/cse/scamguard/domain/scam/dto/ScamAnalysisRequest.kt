package kr.cse.scamguard.domain.scam.dto

import io.swagger.v3.oas.annotations.media.Schema

data class ScamAnalysisRequest(
    @field:Schema(description = "프롬프트", example = "분석 대상 텍스트 프롬프트")
    val prompt: String
)

enum class AiModelType {
    GPT_5_NANO, DEEPSEEK_R1_0528, LLAMA_3_1_405B, GPT_OSS_120B, SOLAR_PRO_3
}
