package kr.cse.scamguard.domain.scam.service.llm

import org.springframework.ai.openai.OpenAiChatModel
import org.springframework.ai.openai.OpenAiChatOptions
import org.springframework.ai.openai.api.OpenAiApi
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class LlmConfiguration {

    @Bean(name = ["openAiChatModel"])
    fun openAiChatModel(
        @Value("\${spring.ai.openai.api-key}") apiKey: String
    ): OpenAiChatModel {
        val openAiApi = OpenAiApi.builder()
            .apiKey(apiKey)
            .build()

        return OpenAiChatModel.builder()
            .openAiApi(openAiApi)
            .defaultOptions(
                OpenAiChatOptions.builder()
                    .model("gpt-5-nano")
                    .temperature(1.0)
                    .build()
            )
            .build()
    }

    @Bean(name = ["deepSeekChatModel"])
    fun deepSeekChatModel(
        @Value("\${app.ai.openrouter.api-key}") apiKey: String,
        @Value("\${app.ai.openrouter.base-url}") baseUrl: String,
    ): OpenAiChatModel {
        val openAiApi = OpenAiApi.builder()
            .baseUrl(baseUrl)
            .apiKey(apiKey)
            .build()

        return OpenAiChatModel.builder()
            .openAiApi(openAiApi)
            .defaultOptions(
                OpenAiChatOptions.builder()
                    .model("deepseek/deepseek-r1-0528:free")
                    .temperature(0.7)
                    .build()
            )
            .build()
    }

    @Bean(name = ["llama3ChatModel"])
    fun llamaChatModel(
        @Value("\${app.ai.openrouter.api-key}") apiKey: String,
        @Value("\${app.ai.openrouter.base-url}") baseUrl: String,
    ): OpenAiChatModel {
        val openAiApi = OpenAiApi.builder()
            .baseUrl(baseUrl)
            .apiKey(apiKey)
            .build()

        return OpenAiChatModel.builder()
            .openAiApi(openAiApi)
            .defaultOptions(
                OpenAiChatOptions.builder()
                    .model("meta-llama/llama-3.1-405b-instruct:free")
                    .temperature(1.0)
                    .build()
            )
            .build()
    }
}
