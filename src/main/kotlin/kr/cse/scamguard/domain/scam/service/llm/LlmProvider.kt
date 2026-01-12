package kr.cse.scamguard.domain.scam.service.llm

import kr.cse.scamguard.domain.scam.model.LlmScamAnalysisResult

interface LlmProvider {
    /**
     * 사기 분석을 수행하고 구조화된 JSON 결과를 반환
     */
    fun analyzeScam(prompt: String): LlmScamAnalysisResult

    /**
     * Provider가 지원하는 모델 이름
     */
    fun getModelName(): String
}
