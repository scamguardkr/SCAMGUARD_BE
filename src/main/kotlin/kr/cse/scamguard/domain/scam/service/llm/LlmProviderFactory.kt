package kr.cse.scamguard.domain.scam.service.llm

import kr.cse.scamguard.domain.scam.dto.AiModelType
import kr.cse.scamguard.domain.scam.service.llm.provider.*
import org.springframework.stereotype.Component

@Component
class LlmProviderFactory(
    private val openAiProvider: OpenAiLlmProvider,
    private val deepSeekProvider: DeepSeekLlmProvider,
    private val llama3LLMProvider: Llama3LLMProvider,
    private val gptOss120BProvider : OpenAI_GPT_OSS_120BProvider,
    private val solarPro3Provider: SolarPro3Provider
) {
    fun getProvider(modelType: AiModelType): LlmProvider {
        return when (modelType) {
            AiModelType.GPT_5_NANO -> openAiProvider
            AiModelType.DEEPSEEK_R1_0528 -> deepSeekProvider
            AiModelType.LLAMA_3_1_405B -> llama3LLMProvider
            AiModelType.GPT_OSS_120B -> gptOss120BProvider
            AiModelType.SOLAR_PRO_3 -> solarPro3Provider
        }
    }
}
