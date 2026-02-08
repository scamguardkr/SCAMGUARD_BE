package kr.cse.scamguard.domain.scam.service.llm.provider

import com.fasterxml.jackson.databind.ObjectMapper
import kr.cse.scamguard.domain.scam.model.LlmScamAnalysisResult
import kr.cse.scamguard.domain.scam.service.llm.LlmProvider
import kr.cse.scamguard.domain.scam.service.llm.prompt.SystemPrompt
import org.slf4j.LoggerFactory
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.openai.OpenAiChatModel
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

@Component
class SolarPro3Provider (
    @Qualifier("solarPro3ChatModel") private val chatModel: OpenAiChatModel,
    private val objectMapper: ObjectMapper
) : LlmProvider {

    private val log = LoggerFactory.getLogger(this::class.java)
    private val chatClient = ChatClient.create(chatModel)

    override fun analyzeScam(prompt: String): LlmScamAnalysisResult {
        val systemPrompt = SystemPrompt.getAnalyzeSystemPromptV1()

        try {
            val response = chatClient.prompt()
                .system(systemPrompt)
                .user(prompt)
                .call()
                .content()

            log.debug("solar-pro-3:free 응답: {}", response)

            // JSON 파싱
            return parseResponse(response)
        } catch (e: Exception) {
            log.error("gpt-oss-120B 분석 중 오류 발생 {}", e.message)
            throw RuntimeException("OpenAI 분석 실패", e)
        }
    }

    override fun getModelName(): String = "solar-pro-3:free"

    private fun parseResponse(response: String?): LlmScamAnalysisResult {
        requireNotNull(response) { "OpenAI 응답이 null입니다" }

        val cleanedResponse = response
            .replace("```json", "")
            .replace("```", "")
            .trim()

        return try {
            objectMapper.readValue(cleanedResponse, LlmScamAnalysisResult::class.java)
        } catch (e: Exception) {
            log.error("JSON 파싱 실패. 응답: {}", cleanedResponse, e)
            throw RuntimeException("OpenAI 응답 파싱 실패", e)
        }
    }
}
