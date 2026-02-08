package kr.cse.scamguard.domain.scam.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class ScamAnalysisModelResponse(
    @JsonProperty("models")
    val models: List<AiModelType>,
)
