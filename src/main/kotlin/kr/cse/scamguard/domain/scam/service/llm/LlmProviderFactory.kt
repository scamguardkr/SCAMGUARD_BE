package kr.cse.scamguard.domain.scam.service.llm

import kr.cse.scamguard.domain.scam.dto.AiModelType
import kr.cse.scamguard.domain.scam.service.llm.provider.DeepSeekLlmProvider
import kr.cse.scamguard.domain.scam.service.llm.provider.Llama3LLMProvider
import kr.cse.scamguard.domain.scam.service.llm.provider.OpenAiLlmProvider
import org.springframework.stereotype.Component

@Component
class LlmProviderFactory(
    private val openAiProvider: OpenAiLlmProvider,
    private val deepSeekProvider: DeepSeekLlmProvider,
    private val llama3LLMProvider: Llama3LLMProvider
) {
    fun getProvider(modelType: AiModelType): LlmProvider {
        return when (modelType) {
            AiModelType.OPENAI -> openAiProvider
            AiModelType.DEEPSEEK_R1_0528 -> deepSeekProvider
            AiModelType.LLAMA_3_1_405B -> llama3LLMProvider
        }
    }
}
