package kr.cse.scamguard.domain.scam.model

import kr.cse.scamguard.domain.scam.dto.AiModelType
import kr.cse.scamguard.domain.scam.dto.ScamAnalysisRequest
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "scam_analysis")
data class ScamAnalysisDocument(
    @Id
    val id: String? = null,

    val userId: Long,

    // 요청 정보
    val prompt: String,
    val aiModel: AiModelType,

    // 응답 정보 (JSON 그대로 저장)
    val response: ScamAnalysisResponse,

    // 메타 정보
    val createdAt: LocalDateTime = LocalDateTime.now()
) {
    companion object {
        fun from(
            userId: Long,
            request: ScamAnalysisRequest,
            response: ScamAnalysisResponse,
            aiModel: AiModelType
        ): ScamAnalysisDocument {
            return ScamAnalysisDocument(
                userId = userId,
                prompt = request.prompt,
                aiModel = aiModel,
                response = response
            )
        }
    }
}
