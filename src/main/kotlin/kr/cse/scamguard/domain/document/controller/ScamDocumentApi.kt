package kr.cse.scamguard.domain.document.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import kr.cse.scamguard.common.embedding.EmbeddingModelInfo
import kr.cse.scamguard.common.model.CommonResponse
import kr.cse.scamguard.domain.document.dto.ScamDocumentIngestRequest
import kr.cse.scamguard.domain.document.dto.ScamDocumentIngestResponse
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails

@Tag(name = "문서 수집 API", description = "사기 관련 문서를 벡터화하여 Elasticsearch에 저장")
interface ScamDocumentApi {
    
    @Operation(
        summary = "문서 수집",
        description = """
            사기 관련 문서(뉴스, 커뮤니티 게시글, 경찰 발표, 사용자 보고)를 수집합니다.
            
            - 문서는 청크로 분할되어 벡터 임베딩됩니다.
            - 응답에 사용된 임베딩 모델 정보가 포함됩니다.
            - 검색 시 동일한 임베딩 모델을 사용해야 정확한 결과를 얻을 수 있습니다.
            
            필요 권한: ADMIN
        """
    )
    fun ingestDocument(
        request: ScamDocumentIngestRequest,
        user: UserDetails
    ): CommonResponse<ScamDocumentIngestResponse>
    
    @Operation(
        summary = "현재 활성 임베딩 모델 정보 조회",
        description = """
            현재 문서 수집에 사용되는 임베딩 모델 정보를 조회합니다.
            
            검색 API 호출 전에 이 정보를 확인하여 호환성을 체크할 수 있습니다.
        """
    )
    fun getActiveEmbeddingModel(): CommonResponse<EmbeddingModelInfo>
}
